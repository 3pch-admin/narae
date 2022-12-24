package ext.narae.ui.common.resource;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("ext.narae.ui.common.resource.StockControlRB_ko")
public class ECOTypeRB_ko extends WTListResourceBundle {
	@RBEntry("고객사유")
	public static final String PRIVATE_CONSTANT_001 = "A"; //고객사유
	
	@RBEntry("내부 사유")
	public static final String PRIVATE_CONSTANT_002 = "B"; //내부 사유
	
	@RBEntry("협력 업체 사유")
	public static final String PRIVATE_CONSTANT_003 = "C"; //협력 업체 사유
}
