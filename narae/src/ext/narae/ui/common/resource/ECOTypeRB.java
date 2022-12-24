package ext.narae.ui.common.resource;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("ext.narae.ui.common.resource.StockControlRB")
public class ECOTypeRB extends WTListResourceBundle {
	@RBEntry("Customer")
	public static final String PRIVATE_CONSTANT_001 = "A"; //고객사유
	
	@RBEntry("Internal")
	public static final String PRIVATE_CONSTANT_002 = "B"; //내부 사유
	
	@RBEntry("Collaborator")
	public static final String PRIVATE_CONSTANT_003 = "C"; //협력 업체 사유
}
