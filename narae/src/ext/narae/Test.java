package ext.narae;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

import ext.narae.service.erp.beans.ERPUtil;
import ext.narae.util.CommonUtil;
import ext.narae.util.code.NumberCode;
import ext.narae.util.code.NumberCodeType;
import ext.narae.util.db.DBConnectionManager;
import wt.fc.PersistenceHelper;

public class Test {

	public static void main(String[] args) throws Exception {
//		,02
		String oid = "ext.narae.util.code.NumberCode:855643";
		
		NumberCode parent = (NumberCode)CommonUtil.getObject(oid);
		
		NumberCode c = NumberCode.newNumberCode();
		c.setEngName("Non");
		c.setCode("00");
		c.setName("Non");
		c.setDescription("Non");
		c.setParent(parent);
		c.setCodeType(NumberCodeType.toNumberCodeType("CADATTRIBUTE"));
		PersistenceHelper.manager.save(c);
		System.out.println("종료.");
		System.exit(0);
	}
}