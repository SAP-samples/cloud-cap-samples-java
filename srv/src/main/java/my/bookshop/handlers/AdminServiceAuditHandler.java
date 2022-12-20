package my.bookshop.handlers;

import static cds.gen.adminservice.AdminService_.ORDERS;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnDelete;
import com.sap.cds.services.EventContext;
import com.sap.cds.services.auditlog.Action;
import com.sap.cds.services.auditlog.AuditLogService;
import com.sap.cds.services.auditlog.ChangedAttribute;
import com.sap.cds.services.auditlog.ConfigChange;
import com.sap.cds.services.auditlog.DataObject;
import com.sap.cds.services.auditlog.KeyValuePair;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
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

	@Before(event = { CqnService.EVENT_CREATE })
	public void beforeCreateOrder(Stream<Orders> orders) {
		orders.forEach(order -> {
			ConfigChange cfgChange = createConfigChange(order, null);
			this.auditLog.logConfigChange(Action.CREATE, cfgChange);
		});
	}

	@Before(event = { CqnService.EVENT_UPDATE, CqnService.EVENT_UPSERT })
	public void beforeUpdateOrUpsertOrder(EventContext context, Stream<Orders> orders) {
		orders.forEach(order -> {
			ConfigChange cfgChange = null;
			Action action = null;
			Optional<Orders> oldOrders = readOldOrders(order.getId());
			if (oldOrders.isPresent()) {
				if (!StringUtils.equals(order.getCurrencyCode(), oldOrders.get().getCurrencyCode())) {
					cfgChange = createConfigChange(order, oldOrders.get());
					action = Action.UPDATE;
				}
			} else {
				cfgChange = createConfigChange(order, null);
				action = Action.CREATE;
			}
			if (cfgChange != null && action != null) {
				auditCfgChange(action, cfgChange, context);
			}
		});
	}

	@Before(event = { CqnService.EVENT_DELETE }, entity = { Orders_.CDS_NAME })
	public void beforeDelete(CdsDeleteEventContext context) {
		// prepare a select statement to read old currency code
		Select<?> ordersSelect = toSelect(context.getCqn());

		// read old order number from DB
		this.db.run(ordersSelect).first(Orders.class).ifPresent(oldOrders -> {
			ConfigChange cfgChange = createConfigChange(null, oldOrders);
			auditCfgChange(Action.DELETE, cfgChange, context);
		});
	}

	private void auditCfgChange(final Action action, final ConfigChange cfgChange, EventContext context) {
		// create new request context and set tenant to null
		context.getCdsRuntime().requestContext().modifyUser(user -> user.setTenant(null)).run(ctx -> {
			// send audit log message into provider tenant as user's tenant is null
			this.auditLog.logConfigChange(action, cfgChange);
		});
	}

	private Optional<Orders> readOldOrders(String ordersId) {
		// prepare a select statement to read old order number
		Select<Orders_> ordersSelect = Select.from(ORDERS).columns(Orders_::OrderNo)
				.where(o -> o.ID().eq(ordersId).and(o.IsActiveEntity().eq(true)));

		// read old orders from DB
		return this.db.run(ordersSelect).first(Orders.class);
	}

	private static ConfigChange createConfigChange(Orders orders, Orders oldOrders) {
		ChangedAttribute currencyCodeAttr = createChangedAttribute(orders != null ? orders.getCurrencyCode() : null,
				oldOrders != null ? oldOrders.getCurrencyCode() : null);

		ConfigChange cfgChange = ConfigChange.create();
		cfgChange.setDataObject(createDataObject(orders != null ? orders : oldOrders));
		cfgChange.setAttributes(Arrays.asList(currencyCodeAttr));
		return cfgChange;
	}

	private static DataObject createDataObject(Orders order) {
		KeyValuePair id = createId(order);

		DataObject dataObject = DataObject.create();
		dataObject.setType(Orders_.CDS_NAME);
		dataObject.setId(Arrays.asList(id));
		return dataObject;
	}

	private static ChangedAttribute createChangedAttribute(String newValue, String oldValue) {
		ChangedAttribute attribute = ChangedAttribute.create();
		attribute.setName(Orders.CURRENCY_CODE);
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
