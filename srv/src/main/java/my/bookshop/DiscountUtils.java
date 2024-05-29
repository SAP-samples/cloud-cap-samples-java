package my.bookshop;

import java.util.Map;
import java.util.function.BiFunction;

import com.sap.cds.ql.cqn.CqnElementRef;
import com.sap.cds.ql.cqn.CqnExpression;
import com.sap.cds.ql.cqn.CqnPlain;
import com.sap.cds.ql.cqn.CqnStringLiteral;
import com.sap.cds.ql.cqn.CqnVisitor;

/**
 * Takes care of processing annotation expressions to calculate the title with
 * discount
 */
public class DiscountUtils {

	public static String getDiscountTitle(CqnExpression expression, Map<String, ?> row) {
		ExpressionVisitor v = new ExpressionVisitor(row);
		expression.accept(v);
		return v.getTitle();
	}
}

class ExpressionVisitor implements CqnVisitor {

	private final Map<String, ?> row;
	private String title;
	private BiFunction<String, Object, String> operator;

	public ExpressionVisitor(Map<String, ?> row) {
		this.row = row;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public void visit(CqnElementRef elementRef) {
		if (operator == null) {
			title = (String) row.get(elementRef.displayName());
		} else {
			title = operator.apply(title, row.get(elementRef.displayName()));
		}
	}

	@Override
	public void visit(CqnPlain p) {
		if ("||".equals(p.plain())) {
			operator = (s1, s2) -> s1 + s2;
		}
	}

	@Override
	public void visit(CqnStringLiteral l) {
		title = operator.apply(title, l.value());
	}
}
