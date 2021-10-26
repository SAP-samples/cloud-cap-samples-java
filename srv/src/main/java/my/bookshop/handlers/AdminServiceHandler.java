package my.bookshop.handlers;

import static cds.gen.adminservice.AdminService_.ORDERS;
import static cds.gen.adminservice.AdminService_.ORDER_ITEMS;
import static cds.gen.my.bookshop.Bookshop_.BOOKS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.Upsert;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnDelete;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.EventContext;
import com.sap.cds.services.ServiceException;
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
import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.draft.DraftCancelEventContext;
import com.sap.cds.services.draft.DraftPatchEventContext;
import com.sap.cds.services.draft.DraftService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.messages.Messages;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.adminservice.AddToOrderContext;
import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.Books;
import cds.gen.adminservice.Books_;
import cds.gen.adminservice.OrderItems;
import cds.gen.adminservice.OrderItems_;
import cds.gen.adminservice.Orders;
import cds.gen.adminservice.Orders_;
import cds.gen.adminservice.Upload;
import cds.gen.adminservice.Upload_;
import cds.gen.my.bookshop.Bookshop_;
import my.bookshop.MessageKeys;

/**
 * Custom business logic for the "Admin Service" (see admin-service.cds)
 *
 * Handles creating and editing orders.
 */
@Component
@ServiceName(AdminService_.CDS_NAME)
class AdminServiceHandler implements EventHandler {

	private final DraftService adminService;

	private final PersistenceService db;

	private final Messages messages;

	private final CqnAnalyzer analyzer;

	private final AuditLogService auditLog;

	AdminServiceHandler(@Qualifier(AdminService_.CDS_NAME) DraftService adminService, PersistenceService db,
			Messages messages, CdsModel model, AuditLogService auditLog) {
		this.adminService = adminService;
		this.db = db;
		this.messages = messages;
		this.auditLog = auditLog;

		// model is a tenant-dependant model proxy
		this.analyzer = CqnAnalyzer.create(model);
	}

	@After(event = { CqnService.EVENT_READ })
	public void afterReadOrder(Stream<Orders> orders) {
		orders.forEach(this::auditAccess);
	}

	/**
	 * Validate correctness of an order before finishing the order proces:
	 * 1. Check Order amount for each Item and return a message if amount is empty or <= 0
	 * 2. Check Order amount for each Item is available, return message if the stock is too low
	 *
	 * @param orders
	 */
	@Before(event = { CqnService.EVENT_CREATE, CqnService.EVENT_UPSERT, CqnService.EVENT_UPDATE })
	public void beforeCreateOrder(Stream<Orders> orders, EventContext context) {
		orders.forEach(order -> {
			// reset total
			order.setTotal(BigDecimal.valueOf(0));
			if(order.getItems() != null) {
				order.getItems().forEach(orderItem -> {
					// validation of the Order creation request
					Integer amount = orderItem.getAmount();
					if (amount == null || amount <= 0) {
						// errors with localized messages from property files
						// exceptions abort the request and set an error http status code
						// messages in contrast allow to collect multiple errors
						messages.error(MessageKeys.AMOUNT_REQUIRE_MINIMUM)
								.target("in", ORDERS, o -> o.Items(i -> i.ID().eq(orderItem.getId()).and(i.IsActiveEntity().eq(false))).amount());
					}

					String bookId = orderItem.getBookId();
					if (bookId == null) {
						// Tip: using static text without localization is still possible in exceptions and messages
						messages.error("You have to specify the book to order")
								.target("in", ORDERS, o -> o.Items(i -> i.ID().eq(orderItem.getId()).and(i.IsActiveEntity().eq(false))).book_ID());
					}

					if(amount == null || amount <= 0 || bookId == null) {
						return; // follow up validations rely on these
					}

					// calculate the actual amount difference
					// FIXME this should handle book changes, currently only amount changes are handled
					int diffAmount = amount - db.run(Select.from(Bookshop_.ORDER_ITEMS).columns(i -> i.amount()).byId(orderItem.getId()))
												.first(OrderItems.class).map(i -> i.getAmount()).orElse(0);

					// check if enough books are available
					Result result = db.run(Select.from(BOOKS).columns(b -> b.ID(), b -> b.stock(), b -> b.price()).byId(bookId));
					Books book = result.first(Books.class).orElseThrow(notFound(MessageKeys.BOOK_MISSING));
					if (book.getStock() < diffAmount) {
						// Tip: you can have localized messages and use parameters in your messages
						messages.error(MessageKeys.BOOK_REQUIRE_STOCK, book.getStock())
								.target("in", ORDERS, o -> o.Items(i -> i.ID().eq(orderItem.getId()).and(i.IsActiveEntity().eq(false))).amount());
						return; // no need to update follow-up values with invalid amount / stock
					}

					// update the book with the new stock
					book.setStock(book.getStock() - diffAmount);
					db.run(Update.entity(BOOKS).data(book));

					// update the net amount
					BigDecimal updatedNetAmount = book.getPrice().multiply(BigDecimal.valueOf(amount));
					orderItem.setNetAmount(updatedNetAmount);

					// update the total
					order.setTotal(order.getTotal().add(updatedNetAmount));
				});
			}

			auditChanges(order, context);
		});
	}

	@Before(event = { CqnService.EVENT_DELETE })
	public void beforeDelete(CdsDeleteEventContext context) {
		auditDelete(context);
	}

	/**
	 * Calculate the total order value preview when editing an order item
	 *
	 * @param context
	 * @param orderItem
	 */
	@Before(event = DraftService.EVENT_DRAFT_PATCH)
	public void patchOrderItems(DraftPatchEventContext context, OrderItems orderItem) {
		// check if amount or book was updated
		Integer amount = orderItem.getAmount();
		String bookId = orderItem.getBookId();
		String orderItemId = orderItem.getId();
		BigDecimal netAmount = calculateNetAmountInDraft(orderItemId, amount, bookId);
		if (netAmount != null) {
			orderItem.setNetAmount(netAmount);
		}
	}

	/**
	 * Calculate the total order value preview when deleting an order item from the order
	 *
	 * @param context
	 */
	@Before(event = DraftService.EVENT_DRAFT_CANCEL, entity = OrderItems_.CDS_NAME)
	public void cancelOrderItems(DraftCancelEventContext context) {
		String orderItemId = (String) analyzer.analyze(context.getCqn()).targetKeys().get(OrderItems.ID);
		if(orderItemId != null) {
			calculateNetAmountInDraft(orderItemId, 0, null);
		}
	}

	private BigDecimal calculateNetAmountInDraft(String orderItemId, Integer newAmount, String newBookId) {
		Integer amount = newAmount;
		String bookId = newBookId;
		if (amount == null && bookId == null) {
			return null; // nothing changed
		}

		// get the order item that was updated (to get access to the book price, amount and order total)
		Result result = adminService.run(Select.from(ORDER_ITEMS)
				.columns(o -> o.amount(), o -> o.netAmount(),
						o -> o.book().expand(b -> b.ID(), b -> b.price()),
						o -> o.parent().expand(p -> p.ID(), p -> p.total()))
				.where(o -> o.ID().eq(orderItemId).and(o.IsActiveEntity().eq(false))));
		OrderItems itemToPatch = result.first(OrderItems.class).orElseThrow(notFound(MessageKeys.ORDERITEM_MISSING));
		BigDecimal bookPrice = null;

		// fallback to existing values
		if(amount == null) {
			amount = itemToPatch.getAmount();
		}

		if(bookId == null && itemToPatch.getBook() != null) {
			bookId = itemToPatch.getBook().getId();
			bookPrice = itemToPatch.getBook().getPrice();
		}

		if(amount == null || bookId == null) {
			return null; // not enough data available
		}

		// only warn about invalid values as we are in draft mode
		if(amount <= 0) {
			// Tip: add additional messages with localized messages from property files
			// these messages are transported in sap-messages and do not abort the request
			messages.warn(MessageKeys.AMOUNT_REQUIRE_MINIMUM);
		}

		// get the price of the updated book ID
		if(bookPrice == null) {
			result = db.run(Select.from(BOOKS).byId(bookId).columns(b -> b.price()));
			Books book = result.first(Books.class).orElseThrow(notFound(MessageKeys.BOOK_MISSING));
			bookPrice = book.getPrice();
		}

		// update the net amount of the order item
		BigDecimal updatedNetAmount = bookPrice.multiply(BigDecimal.valueOf(amount));

		// update the order's total
		BigDecimal previousNetAmount = defaultZero(itemToPatch.getNetAmount());
		BigDecimal currentTotal = defaultZero(itemToPatch.getParent().getTotal());
		BigDecimal newTotal = currentTotal.subtract(previousNetAmount).add(updatedNetAmount);
		adminService.patchDraft(Update.entity(ORDERS)
				.where(o -> o.ID().eq(itemToPatch.getParent().getId()).and(o.IsActiveEntity().eq(false)))
				.data(Orders.TOTAL, newTotal));

		return updatedNetAmount;
	}

	/**
	 * Adds a book to an order
	 * @param context
	 */
	@On(entity = Books_.CDS_NAME)
	public void addBookToOrder(AddToOrderContext context) {
		String orderId = context.getOrderId();
		List<Orders> orders = adminService.run(Select.from(ORDERS).columns(o -> o._all(), o -> o.Items().expand()).where(o -> o.ID().eq(orderId))).listOf(Orders.class);
		Orders order = orders.stream().filter(p -> p.getIsActiveEntity()).findFirst().orElse(null);

		// check that the order with given ID exists and is not in draft-mode
		if((orders.size() > 0 && order == null) || orders.size() > 1) {
			throw new ServiceException(ErrorStatuses.CONFLICT, MessageKeys.ORDER_INDRAFT);
		} else if (orders.size() <= 0) {
			throw new ServiceException(ErrorStatuses.NOT_FOUND, MessageKeys.ORDER_MISSING);
		}

		if(order.getItems() == null) {
			order.setItems(new ArrayList<>());
		}

		// get ID of the book on which the action was called (bound action)
		String bookId = (String) analyzer.analyze(context.getCqn()).targetKeys().get(Books.ID);

		// create order item
		OrderItems newItem = OrderItems.create();
		newItem.setId(UUID.randomUUID().toString());
		newItem.setBookId(bookId);
		newItem.setAmount(context.getAmount());
		order.getItems().add(newItem);

		Orders updatedOrder = adminService.run(Update.entity(ORDERS).data(order)).single(Orders.class);
		messages.success(MessageKeys.BOOK_ADDED_ORDER);
		context.setResult(updatedOrder);
	}

	/**
	 * @return the static CSV singleton upload entity
	 */
	@On(entity = Upload_.CDS_NAME, event = CdsService.EVENT_READ)
	public Upload getUploadSingleton() {
		return Upload.create();
	}

	/**
	 * Handles CSV uploads with book data
	 * @param context
	 * @param csv
	 */
	@On(event = CdsService.EVENT_UPDATE)
	public void addBooksViaCsv(CdsUpdateEventContext context, Upload upload) {
		InputStream is = upload.getCsv();
		if (is != null) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
				br.lines().skip(1).forEach((line) -> {
					String[] p = line.split(";");
					Books book = Books.create();
					book.setId(p[0]);
					book.setTitle(p[1]);
					book.setDescr(p[2]);
					book.setAuthorId(p[3]);
					book.setStock(Integer.valueOf(p[4]).intValue());
					book.setPrice(BigDecimal.valueOf(Double.valueOf(p[5])));
					book.setCurrencyCode(p[6]);
					book.setGenreId(Integer.valueOf(p[7]));

					// separate transaction per line
					context.getCdsRuntime().changeSetContext().run(ctx -> {
						db.run(Upsert.into(Bookshop_.BOOKS).entry(book));
					});
				});
			} catch (IOException e) {
				throw new ServiceException(ErrorStatuses.SERVER_ERROR, MessageKeys.BOOK_IMPORT_FAILED, e);
			} catch (IndexOutOfBoundsException e) {
				throw new ServiceException(ErrorStatuses.SERVER_ERROR, MessageKeys.BOOK_IMPORT_INVALID_CSV, e);
			}
		}
		context.setResult(Arrays.asList(upload));
	}

	private Supplier<ServiceException> notFound(String message) {
		return () -> new ServiceException(ErrorStatuses.NOT_FOUND, message);
	}

	private BigDecimal defaultZero(BigDecimal decimal) {
		return decimal == null ? BigDecimal.valueOf(0) : decimal;
	}

	// audit logging

	/**
	 * Writes a data access message to the audit log for the given order if the order number is read.
	 *
	 * @param order the accessed order
	 */
	private void auditAccess(Orders order) {
		if (order.getOrderNo() != null) {
			this.auditLog.logDataAccess(createAccess(order));
		}
	}

	/**
	 * Writes a data modification message to the auditlog if the order number has changed.
	 *
	 * @param order the modified order
	 */
	private void auditChanges(Orders orders, EventContext context) {
		DataModification dataModification = null;

		if (orders.getId() != null) {
			switch (context.getEvent()) {
			case CqnService.EVENT_CREATE:
				dataModification = createDataModification(orders, null, Action.CREATE);
				break;
			case CqnService.EVENT_UPDATE:
			case CqnService.EVENT_UPSERT:
				Optional<Orders> oldOrders = readOldOrders(orders.getId());
				if (oldOrders.isPresent()) {
					if (!StringUtils.equals(orders.getOrderNo(), oldOrders.get().getOrderNo())) {
						dataModification = createDataModification(orders, oldOrders.get(), Action.UPDATE);
					}
				} else {
					dataModification = createDataModification(orders, null, Action.CREATE);
				}
				break;
			default:
				break;
			}
		}

		if (dataModification != null) {
			this.auditLog.logDataModification(Arrays.asList(dataModification));
		}
	}

	private Optional<Orders> readOldOrders(String ordersId) {
		// prepare a select statement to read old order number
		Select<Orders_> ordersSelect = Select.from(ORDERS).columns(Orders_::OrderNo)
				.where(o -> o.ID().eq(ordersId).and(o.IsActiveEntity().eq(true)));

		// read old orders from DB
		return this.db.run(ordersSelect).first(Orders.class);
	}

	/**
	 * Writes a data modification message to the auditlog if the order number was deleted.
	 *
	 * @param context the {@link CdsDeleteEventContext delete event context}
	 */
	private void auditDelete(CdsDeleteEventContext context) {
		// prepare a select statement to read old order number
		Select<?> ordersSelect = toSelect(context.getCqn());

		// read old order number from DB
		this.db.run(ordersSelect).first(Orders.class).ifPresent(oldOrders -> {
			DataModification dataModification = createDataModification(null, oldOrders, Action.DELETE);
			this.auditLog.logDataModification(Arrays.asList(dataModification));
		});
	}

	private Access createAccess(Orders orders) {
		Access access = Access.create();
		access.setDataObject(createDataObject(orders));
		access.setDataSubject(createDataSubject(orders));
		access.setAttributes(createAttributes(Orders.ORDER_NO));
		return access;
	}

	private static DataModification createDataModification(Orders orders, Orders oldOrders, Action action) {
		ChangedAttribute attribute = createChangedAttribute(Orders.ORDER_NO,
				orders != null ? orders.getOrderNo() : null, oldOrders != null ? oldOrders.getOrderNo() : null);

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

	public static Select<?> toSelect(CqnDelete delete) {
		Select<?> select = Select.from(delete.ref());
		delete.where().ifPresent(select::where);
		return select;
	}

}
