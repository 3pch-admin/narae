package ext.narae.component;

import javax.servlet.jsp.JspContext;

public interface SearchDataPopulator {
	public static final String VERSION = "$Id: $";

    public void populateSearchCriteria(JspContext jspContext);
}
