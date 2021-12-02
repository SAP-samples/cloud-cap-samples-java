package my.bookshop.handlers;

import static cds.gen.adminservice.AdminService_.ORDERS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnDelete;
import com.sap.cds.services.auditlog.Access;
import com.sap.cds.services.auditlog.Action;
import com.sap.cds.services.auditlog.Attribute;
import com.sap.cds.services.auditlog.AuditLogService;
import com.sap.cds.services.auditlog.ChangedAttribute;
import com.sap.cds.services.auditlog.DataModification;
import com.sap.cds.services.auditlog.DataObject;
import com.sap.cds.services.auditlog.DataSubject;
import com.sap.cds.services.auditlog.KeyValuePair;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.Orders;
import cds.gen.adminservice.Orders_;

/**
 * A custom handler that creates AuditLog messages.
 */
@Component
@ServiceName(AdminService_.CDS_NAME)
class AdminServiceAuditHandler implements EventHandler {

	private final PersistenceService db;

	private final AuditLogService auditLog;

	AdminServiceAuditHandler(PersistenceService db, AuditLogService auditLog) {
		this.db = db;
		this.auditLog = auditLog;
	}

	@After(event = { CqnService.EVENT_READ })
	public void afterReadOrder(Stream<Orders> orders) {
		orders.forEach(order -> {
			if (order.getOrderNo() != null) {
				this.auditLog.logDataAccess(createAccess(order));
			}
		});
	}

	@Before(event = { CqnService.EVENT_CREATE })
	public void beforeCreateOrder(Stream<Orders> orders) {
		orders.forEach(order -> {
			DataModification dataModification = createDataModification(order, null, Action.CREATE);
			if (dataModification != null) {
				this.auditLog.logDataModification(Arrays.asList(dataModification));
			}
		});
	}

	@Before(event = { CqnService.EVENT_UPDATE, CqnService.EVENT_UPSERT })
	public void beforeUpdateOrUpsertOrder(Stream<Orders> orders) {
		orders.forEach(order -> {
			DataModification dataModification = null;
			Optional<Orders> oldOrders = readOldOrders(order.getId());
			if (oldOrders.isPresent()) {
				if (!StringUtils.equals(order.getOrderNo(), oldOrders.get().getOrderNo())) {
					dataModification = createDataModification(order, oldOrders.get(), Action.UPDATE);
				}
			} else {
				dataModification = createDataModification(order, null, Action.CREATE);
			}
			if (dataModification != null) {
				this.auditLog.logDataModification(Arrays.asList(dataModification));
			}
		});
	}

	@Before(event = { CqnService.EVENT_DELETE }, entity = { Orders_.CDS_NAME })
	public void beforeDelete(CdsDeleteEventContext context) {
		// prepare a select statement to read old order number
		Select<?> ordersSelect = toSelect(context.getCqn());

		// read old order number from DB
		this.db.run(ordersSelect).first(Orders.class).ifPresent(oldOrders -> {
			DataModification dataModification = createDataModification(null, oldOrders, Action.DELETE);
			this.auditLog.logDataModification(Arrays.asList(dataModification));
		});
	}

	private Optional<Orders> readOldOrders(String ordersId) {
		// prepare a select statement to read old order number
		Select<Orders_> ordersSelect = Select.from(ORDERS).columns(Orders_::OrderNo)
				.where(o -> o.ID().eq(ordersId).and(o.IsActiveEntity().eq(true)));

		// read old orders from DB
		return this.db.run(ordersSelect).first(Orders.class);
	}

	private Access createAccess(Orders orders) {
		Access access = Access.create();
		access.setDataObject(createDataObject(orders));
		access.setDataSubject(createDataSubject(orders));
		access.setAttributes(createAttributes(Orders.ORDER_NO));
		return access;
	}

	private static DataModification createDataModification(Orders orders, Orders oldOrders, Action action) {
		ChangedAttribute attribute = createChangedAttribute(Orders.ORDER_NO, orders != null ? orders.getOrderNo() : null,
				oldOrders != null ? oldOrders.getOrderNo() : null);

		DataModification dataModification = DataModification.create();
		dataModification.setDataObject(createDataObject(orders != null ? orders : oldOrders));
		dataModification.setDataSubject(createDataSubject(orders != null ? orders : oldOrders));
		dataModification.setAction(action);
		dataModification.setAttributes(Arrays.asList(attribute));
		return dataModification;
	}

	private static DataObject createDataObject(Orders order) {
		KeyValuePair id = createId(order);

		DataObject dataObject = DataObject.create();
		dataObject.setType(Orders_.CDS_NAME);
		dataObject.setId(Arrays.asList(id));
		return dataObject;
	}

	private static DataSubject createDataSubject(Orders order) {
		KeyValuePair id = createId(order);

		DataSubject dataSubject = DataSubject.create();
		dataSubject.setType(Orders_.CDS_NAME);
		dataSubject.setId(Arrays.asList(id));
		return dataSubject;
	}

	private List<Attribute> createAttributes(String name) {
		List<Attribute> attributes = new ArrayList<>();
		Attribute attr = Attribute.create();
		attr.setName(name);
		attributes.add(attr);
		return attributes;
	}

	private static ChangedAttribute createChangedAttribute(String name, String newValue, String oldValue) {
		ChangedAttribute attribute = ChangedAttribute.create();
		attribute.setName(name);
		attribute.setOldValue(oldValue);
		attribute.setNewValue(newValue);
		return attribute;
	}

	private static KeyValuePair createId(Orders order) {
		KeyValuePair id = KeyValuePair.create();
		id.setKeyName(Orders.ID);
		id.setValue(order.getId());
		return id;
	}

	private static Select<?> toSelect(CqnDelete delete) {
		Select<?> select = Select.from(delete.ref());
		delete.where().ifPresent(select::where);
		return select;
	}

}
