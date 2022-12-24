package ext.narae.ui.common.resource;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("ext.narae.ui.common.resource.StockControlRB")
public class StockControlRB extends WTListResourceBundle {
	@RBEntry("After use")
	public static final String PRIVATE_CONSTANT_001 = "A"; //소진 후 적용
	
	@RBEntry("Scrap")
	public static final String PRIVATE_CONSTANT_002 = "B"; //폐기
	
	@RBEntry("Reuse")
	public static final String PRIVATE_CONSTANT_003 = "C"; //재가공 소진
	
	@RBEntry("None")
	public static final String PRIVATE_CONSTANT_004 = "D"; //해당 사항 없음
}
