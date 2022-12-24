package ext.narae.ui.common.resource;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("ext.narae.ui.common.resource.RequestTypeRB")
public class RequestTypeRB extends WTListResourceBundle {
	@RBEntry("Intital BOM")
	public static final String PRIVATE_CONSTANT_001 = "A"; //BOM(최초)
	
	@RBEntry("ECO BOM")
	public static final String PRIVATE_CONSTANT_002 = "B"; //BOM(ECO)
	
	@RBEntry("Spec Change(User)")
	public static final String PRIVATE_CONSTANT_003 = "D"; //사양변경(USER)
	
	@RBEntry("Spec Change(Internal)")
	public static final String PRIVATE_CONSTANT_004 = "E"; //사양변경(내부)
	
	@RBEntry("Spec Change(Delivery)")
	public static final String PRIVATE_CONSTANT_005 = "F"; //사양변경(조달대응)
	
	@RBEntry("Design Error")
	public static final String PRIVATE_CONSTANT_006 = "H"; //설계오류
	
	@RBEntry("Manufacturing Error")
	public static final String PRIVATE_CONSTANT_007 = "J"; //가공/제작 불량
	
	@RBEntry("Setup(Internal)")
	public static final String PRIVATE_CONSTANT_008 = "K"; //Setup(사내)
	
	@RBEntry("Setup(Out-side)")
	public static final String PRIVATE_CONSTANT_009 = "L"; //Setup(사외)
	
	@RBEntry("Setup(Spare)")
	public static final String PRIVATE_CONSTANT_010 = "M"; //Setup(Spare)
	
	@RBEntry("Setup(Test)")
	public static final String PRIVATE_CONSTANT_011 = "N"; //Setup(Test)
	
	@RBEntry("Break/Lost(Internal)")
	public static final String PRIVATE_CONSTANT_012 = "O"; //파손/분실(사내)
	
	@RBEntry("Break/Lost(Delivery)")
	public static final String PRIVATE_CONSTANT_013 = "P"; //파손/분실(운송)
	
	@RBEntry("Break/Lost(Setup)")
	public static final String PRIVATE_CONSTANT_014 = "Q"; //파손/분실(Setup)
	
	@RBEntry("Etc")
	public static final String PRIVATE_CONSTANT_015 = "R"; //기타
	
	@RBEntry("Sale product")
	public static final String PRIVATE_CONSTANT_016 = "S"; //상품판매
	
	@RBEntry("Non exist in Suggest(BOM/Quantity) ")
	public static final String PRIVATE_CONSTANT_017 = "T"; //기안누락(BOM/수량)
	
	@RBEntry("Additional execution")
	public static final String PRIVATE_CONSTANT_018 = "U"; //추가가공
	
}
