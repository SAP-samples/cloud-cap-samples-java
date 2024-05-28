package my.bookshop;

import java.util.Map;

import com.sap.cds.ql.cqn.CqnElementRef;
import com.sap.cds.ql.cqn.CqnExpression;
import com.sap.cds.ql.cqn.CqnStringLiteral;
import com.sap.cds.ql.cqn.CqnVisitor;

/**
 * Takes care of processing expressions to calculate the discount title
 */
public class DiscountUtils {

	public static String getDiscountTitle(CqnExpression discountRule, Map<String, ?> row, int discountPercent) {
		ExpressionProcessor v = new ExpressionProcessor(row, discountPercent);
		discountRule.accept(v);
		return v.getTitle();
	}
}

class ExpressionProcessor implements CqnVisitor {

	private int discountPercent;
	private Map<String, ?> row;
	private String result;

	public ExpressionProcessor(Map<String, ?> row, int discountPercent) {
		this.row = row;
		this.discountPercent = discountPercent;
		this.result = "";
	}

	public String getTitle() {
		return result;
	}

	@Override
	public void visit(CqnElementRef elementRef) {
		result += row.get(elementRef.displayName());
	}

	@Override
	public void visit(CqnStringLiteral l) {
		String value = l.value();
		result += value.replace("%d%", String.valueOf(discountPercent));
	}
}
