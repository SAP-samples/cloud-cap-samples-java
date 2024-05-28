package my.bookshop.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.sap.cds.impl.parser.ExprParser;
import com.sap.cds.ql.CQL;
import com.sap.cds.reflect.CdsElement;
import com.sap.cds.reflect.CdsEntity;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.request.FeatureTogglesInfo;

import cds.gen.catalogservice.Books;

public class CatalogServiceHandlerTest {

	@Test
	public void testDiscountHandler() {
		CdsElement el = mock(CdsElement.class);
		CdsEntity entity = mock(CdsEntity.class);
		CdsReadEventContext ctx = mock(CdsReadEventContext.class);
		when(ctx.getTarget()).thenReturn(entity);
		when(entity.getElement(anyString())).thenReturn(el);
		when(el.getAnnotationValue(anyString(), any()))
				.thenReturn(new ExprParser().parseValue(
						List.of(CQL.get("title"), CQL.plain("+"), CQL.val(" -- %d%% discount"))));

		Books book1 = Books.create();
		book1.setTitle("Book 1");
		book1.setStock(10);
		Books book2 = Books.create();
		book2.setTitle("Book 2");
		book2.setStock(200);

		CatalogServiceHandler handler = new CatalogServiceHandler(null, null, null, FeatureTogglesInfo.create(), null,
				null);
		handler.discountBooks(ctx, Stream.of(book1, book2));

		assertEquals("Book 1", book1.getTitle(), "Book 1 was discounted");
		assertEquals("Book 2 -- 11% discount", book2.getTitle(), "Book 2 was not discounted");
	}
}
