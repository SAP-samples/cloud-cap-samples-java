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

	public static String getDiscountTitle(CqnExpression discountRule, Map<String, ?> row, int discountPercent) {
		ExpressionProcessor v = new ExpressionProcessor(row, discountPercent);
		discountRule.accept(v);
		return v.getTitle();
	}
}

class ExpressionProcessor implements CqnVisitor {

	private final int discount;
	private final Map<String, ?> row;
	private String title = "";

	public ExpressionProcessor(Map<String, ?> row, int discount) {
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
