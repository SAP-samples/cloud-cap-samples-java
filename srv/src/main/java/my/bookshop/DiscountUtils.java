package my.bookshop;

import java.util.Map;

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

	public static String getDiscountTitle(CqnExpression expression, Map<String, ?> row, int discountPercent) {
		ExpressionVisitor v = new ExpressionVisitor(row, discountPercent);
		expression.accept(v);
		return v.getTitle();
	}
}

class ExpressionVisitor implements CqnVisitor {

	private final int discount;
	private final Map<String, ?> row;
	private String title = "";

	public ExpressionVisitor(Map<String, ?> row, int discount) {
		this.row = row;
		this.discount = discount;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public void visit(CqnElementRef elementRef) {
		title += row.get(elementRef.displayName());
	}

	@Override
	public void visit(CqnPlain p) {
		if ("+".equals(p.plain())) {
			title += " ";
		}
	}

	@Override
	public void visit(CqnStringLiteral l) {
		title += l.value().replace("%d%", String.valueOf(discount));
	}
}
