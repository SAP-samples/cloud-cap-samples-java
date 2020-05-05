/* Copyright (c) 2001-2019 by SAP SE, Walldorf, Germany.
 * All rights reserved. Confidential and proprietary.
 */
package my.bookshop.handlers;

import java.math.BigDecimal;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cds.gen.adminservice.AdminService_;
import cds.gen.adminservice.Books;
import cds.gen.adminservice.Books_;
import cds.gen.adminservice.Orders;
import my.bookshop.MessageKeys;


/**
 * Custom business logic for the "Admin Service" (see admin-service.cds)
 *
 * Handles creating and editing orders.
 */
@Component
@ServiceName(AdminService_.CDS_NAME)
public class AdminServiceHandler implements EventHandler {

	@Autowired
	PersistenceService db;

    /**
	 * Finish an order
	 * @param orders
	 */
	@Before(event = { CdsService.EVENT_CREATE, CdsService.EVENT_UPSERT })
	public void beforeCreateOrder(Stream<Orders> orders) {
		orders.forEach(order -> {
			// reset total
			order.setTotal(BigDecimal.valueOf(0));
			order.getItems().forEach(orderItem -> {
				// validation of the request
				Integer amount = orderItem.getAmount();
				if (amount == null || amount <= 0) {
					// exceptions with localized messages from property files
					// exceptions abort the request and set an error http status code
					throw new ServiceException(ErrorStatuses.BAD_REQUEST, MessageKeys.AMOUNT_REQUIRE_MINIMUM);
				}

				String id = orderItem.getBookId();
				if (id == null) {
					// using static text without localization is still possible in exceptions and messages
					throw new ServiceException(ErrorStatuses.BAD_REQUEST, "You have to specify the book to order");
				}

				// check if enough books are available
				Result result = db.run(Select.from(Books_.class).columns(b -> b.ID(), b -> b.stock(), b -> b.price()).byId(id));
				Books book = result.first(Books.class).orElseThrow(notFound(MessageKeys.BOOK_MISSING));
				if (book.getStock() < amount) {
					throw new ServiceException(ErrorStatuses.BAD_REQUEST, MessageKeys.BOOK_REQUIRE_STOCK, amount, book.getId());
				}

				// update the net amount
				BigDecimal updatedNetAmount = book.getPrice().multiply(BigDecimal.valueOf(amount));
				orderItem.setNetAmount(updatedNetAmount);
				// update the total
				order.setTotal(order.getTotal().add(updatedNetAmount));

				// update the book with the new stock
				book.setStock(book.getStock() - amount);
				db.run(Update.entity(Books_.class).data(book));
			});
		});
	}

	private Supplier<ServiceException> notFound(String message) {
		return () -> new ServiceException(ErrorStatuses.NOT_FOUND, message);
	}

}