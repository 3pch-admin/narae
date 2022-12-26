package ext.narae.component;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class SearchDataPopulatorTag extends SimpleTagSupport {
	public static final String VERSION = "$Id: $";
	private String name;

	@Override
	public void doTag() throws JspException, IOException {
		SearchDataPopulator populator = null;
		try {
			System.out.println("name:" + name);
			final Class<?> clas = Class.forName(name);
			populator = (SearchDataPopulator) clas.newInstance();

		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
		}

		if (populator != null) {
			populator.populateSearchCriteria(getJspContext());
		} else {
			throw new JspException("Unable to initialize search populator:\t" + name);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
