package my.bookshop.index;

import java.io.PrintWriter;

import com.sap.cds.adapter.IndexContentProvider;
import com.sap.cds.adapter.IndexContentProviderFactory;

/**
 * Explicitly adds links to UI resources provided by this application to the index page
 */
public class UiIndexContentProviderFactory implements IndexContentProviderFactory {

	@Override
	public IndexContentProvider create() {
		return new UiIndexContentProvider();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	private static class UiIndexContentProvider implements IndexContentProvider {

		private static final String ENDPOINT_START = "" +
		"                <ul>\n";

		private static final String ENDPOINT = "" +
		"                    <li>\n" +
		"                        <a href=\"%s\">%s</a>\n" +
		"                    </li>\n";

		private static final String ENDPOINT_END = "" +
		"                </ul>\n";

		@Override
		public String getSectionTitle() {
			return "UI endpoints";
		}

		@Override
		public void writeContent(PrintWriter writer, String contextPath) {
			writer.print(ENDPOINT_START);
			writer.printf(ENDPOINT, contextPath + "/fiori.html", "Fiori UI");
			writer.printf(ENDPOINT, contextPath + "/vue/index.html", "Vue.js UI");
			writer.printf(ENDPOINT, contextPath + "/swagger/index.html", "Swagger UI");
			writer.print(ENDPOINT_END);
		}

	}

}
