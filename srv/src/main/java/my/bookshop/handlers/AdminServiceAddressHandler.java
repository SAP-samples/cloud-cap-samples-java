package my.bookshop.handlers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Upsert;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.ql.cqn.CqnSelectListItem;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.draft.DraftPatchEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.Orders;
import cds.gen.api_business_partner.ABusinessPartnerAddress;
import cds.gen.api_business_partner.ABusinessPartnerAddress_;
import cds.gen.api_business_partner.ApiBusinessPartner_;
import cds.gen.my.bookshop.Addresses;
import cds.gen.my.bookshop.Addresses_;
import my.bookshop.MessageKeys;
import my.bookshop.context.BusinessPartnerChangedEventContext;
import my.bookshop.context.BusinessPartnerChangedEventContext.BoBusinessPartnerChanged.BusinessPartnerKey;

/**
 * Custom handler for the Admin Service Addresses, which come from a remote S/4 System
 */
@Component
@ServiceName(AdminService_.CDS_NAME)
public class AdminServiceAddressHandler implements EventHandler {

	private final static Logger logger = LoggerFactory.getLogger(AdminServiceAddressHandler.class);

	// We are mashing up the AdminService with two other services...
	@Autowired
	private PersistenceService db;

	@Resource(name = ApiBusinessPartner_.CDS_NAME)
	private CdsService bupa;

	// Using reflected definitions from the model (request-dependant, and therefore tenant-dependant)
	private Class<Addresses_> addresses = Addresses_.class;
	private Class<ABusinessPartnerAddress_> externalAddresses = ABusinessPartnerAddress_.class;

	@Autowired
	private CdsModel model;

	private String[] getRelevantColumns() {
		// determine columns from simplified Address entity definition
		return model.getEntity(cds.gen.api_business_partner.Addresses_.CDS_NAME).elements().map(e -> e.getName()).collect(Collectors.toList()).toArray(new String[0]);
	}

	// Delegate ValueHelp requests to S/4 backend, fetching current user's addresses from there
	@On(entity = cds.gen.adminservice.Addresses_.CDS_NAME)
	public Result readAddresses(CdsReadEventContext context) {
		String businessPartner = context.getUserInfo().getAttribute("businessPartner").findFirst()
			.orElseThrow(() -> new ServiceException(ErrorStatuses.FORBIDDEN, MessageKeys.BUPA_MISSING));

		// forward the existing where condition
		Select<?> select = Select.from(externalAddresses).where(a -> a.BusinessPartner().eq(businessPartner));
		Optional<CqnPredicate> where = context.getCqn().where();
		if(where.isPresent()) {
			select = select.where(CQL.and(select.where().get(), where.get()));
		}

		// forward the selected columns (in Java, we cannot use our simplified projection Addresses yet)
		List<CqnSelectListItem> columns = context.getCqn().columns();
		boolean hasStar = columns.stream().anyMatch(c -> c.toJson().equals("\"*\""));
		if (hasStar || columns.isEmpty()) {
			select.columns(getRelevantColumns());
		} else {
			List<String> relevntColumnsList = Arrays.asList(getRelevantColumns());
			select.columns(columns.stream().filter(i -> relevntColumnsList.contains(i.displayName())).collect(Collectors.toList()));
		}

		return bupa.run(select);
	}

	// Replicate chosen addresses from S/4 when filling orders.
	@Before
	public void patchAddressId(DraftPatchEventContext context, Stream<Orders> orders) {
		String businessPartner = context.getUserInfo().getAttribute("businessPartner").findFirst()
			.orElseThrow(() -> new ServiceException(ErrorStatuses.FORBIDDEN, MessageKeys.BUPA_MISSING));

		orders.forEach(order -> {
			// check if the address was updated
			String addressId = order.getShippingAddressAddressID();
			if(addressId != null) {
				Result replica = db.run(Select.from(addresses).where(a -> a.BusinessPartner().eq(businessPartner).and(a.AddressID().eq(addressId))));
				// check if the address was not yet replicated
				if(replica.rowCount() < 1) {
					Result remoteAddresses = bupa.run(Select.from(externalAddresses).columns(getRelevantColumns())
							.where(a -> a.BusinessPartner().eq(businessPartner).and(a.AddressID().eq(addressId))));

					if(remoteAddresses.rowCount() == 1) {
						db.run(Upsert.into(addresses).entries(remoteAddresses));
					} else {
						logger.warn("Unexpected number of shipping addresses for ID '{}': {}", addressId, remoteAddresses.rowCount());
					}
				}
			}
		});
	}

	@On(service = ApiBusinessPartner_.CDS_NAME)
	public void updateBusinessPartnerAddresses(BusinessPartnerChangedEventContext context) {
		logger.info(">> received: " + context.getData().toJson());
		for(BusinessPartnerKey key : context.getData().getKeys()) {
			String businessPartner = key.getBusinessPartner(); // S/4 HANA's payload format
			if(businessPartner != null) {
				// fetch affected entries from local replicas
				Result replicas = db.run(Select.from(addresses).where(a -> a.BusinessPartner().eq(businessPartner)));
				if(replicas.rowCount() > 0) {
					logger.info("Updating Addresses for BusinessPartner '{}'", businessPartner);
					// fetch changed data from S/4 -> might be less than local due to deletes
					Result remoteAddresses = bupa.run(Select.from(externalAddresses).columns(getRelevantColumns()).where(a -> a.BusinessPartner().eq(businessPartner)));
					// update replicas or add tombstone if external address was deleted
					replicas.streamOf(Addresses.class).forEach(rep -> {
						Optional<ABusinessPartnerAddress> matching = remoteAddresses.streamOf(ABusinessPartnerAddress.class).filter(ext -> ext.getAddressID().equals(rep.getAddressID())).findFirst();
						if(!matching.isPresent()) {
							rep.setTombstone(true);
						} else {
							rep.replaceAll((k, v) -> matching.get().get(k));
						}
					});
					// update local replicas with changes from S/4
					db.run(Upsert.into(addresses).entries(replicas));
				}
			}
		}
		context.setCompleted();
	}

}
