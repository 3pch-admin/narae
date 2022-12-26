package ext.narae.populator;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspContext;

import wt.lifecycle.State;
import wt.util.WTContext;
import ext.narae.component.SearchDataPopulator;

public class MyWorkPopulator implements SearchDataPopulator {
	public static final String VERSION = "$Id: $";
	private static String RESOURCE = "wt.lifecycle.State";

	@Override
	public void populateSearchCriteria(JspContext jspContext) {
		// TODO Auto-generated method stub
		try {
            // Set all of key in here
            //Sample01 – Part Type Population
            List<String> stateKey = new ArrayList<String>();
            List<String> selectedKey = new ArrayList<String>();
            stateKey.add("");
            stateKey.add("INWORK");
            stateKey.add("CHECKWAIT");
            stateKey.add("APPROVEWAIT");
            stateKey.add("APPROVED");
            stateKey.add("RETURN");
            
            selectedKey.add("INWORK");
            
            List<String> stateDisplay = new ArrayList<String>();
            stateDisplay.add("");
            for( int index=1; index < stateKey.size(); index++) {
            	State tempState = State.toState(stateKey.get(index));
            	stateDisplay.add(tempState.getDisplay(WTContext.getContext().getLocale()));
            }
            

            jspContext.setAttribute("stateKey", stateKey);
            jspContext.setAttribute("stateDisplay", stateDisplay); 
            jspContext.setAttribute("selectedKey", selectedKey);
        } catch(Exception e) {
            e.printStackTrace();
        }

	}

}
