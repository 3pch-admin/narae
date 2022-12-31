<%@page import="ext.narae.util.StringUtil"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.OrderBy"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="ext.narae.service.org.Department"%>
<%@page import="ext.narae.service.org.People"%>
<%@page import="wt.query.QuerySpec"%>
<%@page contentType ="text/xml; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@page import ="java.util.*" %>
<%@page import ="wt.fc.*" %>
<%@page import ="wt.org.*" %>
<models>
<%
String userKey = request.getParameter("userKey");

//System.out.println("========= userKey = " + userKey);

if(userKey==null)
	userKey = "";

			ReferenceFactory rf = new ReferenceFactory();
			
			QuerySpec qs = new QuerySpec(People.class);
			int ii = qs.addClassList(People.class,true);
			int jj = qs.addClassList(WTUser.class,true);
			int kk = qs.addClassList(Department.class,true);
						
			qs.appendWhere(new SearchCondition(People.class,"departmentReference.key.id",Department.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{ii,kk});
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(People.class,"userReference.key.id",WTUser.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{ii,jj});
			
			if (userKey != null && !userKey.equals("")) {
			    if (qs.getConditionCount() > 0)
			    	qs.appendAnd();

				qs.appendOpenParen();
			    qs.appendWhere(new SearchCondition(People.class, "name", SearchCondition.LIKE, "%" + userKey + "%", false), new int[] { ii });
			    qs.appendOr();
			    qs.appendWhere(new SearchCondition(WTUser.class, "name", SearchCondition.LIKE, "%" + userKey + "%", false), new int[] { jj });
			    qs.appendCloseParen();
			    
			}
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(People.class, People.DUTY_CODE, "<>", "DC_10"),new int[]{ii});
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(People.class, People.DUTY_CODE, "<>", "DC_09"),new int[]{ii});
			qs.appendOrderBy(new OrderBy(new ClassAttribute(People.class,"dutyCode"),false),new int[]{ii});

			System.out.println("@@@ qs = " + qs.toString());
			
			QueryResult qr = PersistenceHelper.manager.find(qs);
			System.out.println("quert="+qs);
			Object[] o = null;
			People people = null;
			Department dept1 = null;
			WTUser user = null;

			request.setCharacterEncoding("utf-8");
	
			if( qr != null ){ 
				System.out.println("@@@ people size = " + qr.size());
				while(qr.hasMoreElements()){
					o = (Object[])qr.nextElement();
					people = (People)o[0];
					dept1 = people.getDepartment();
					user = (WTUser)o[1]; 
					//System.out.println("ID = " + StringUtil.checkNull(user.getName()));
					
%>
<user>
<useroid><%=StringUtil.checkValue( PersistenceHelper.getObjectIdentifier ( user ).getStringValue())%></useroid>
<peopleoid><%=StringUtil.checkValue(PersistenceHelper.getObjectIdentifier ( people ).getStringValue())%></peopleoid>
<deptoid><%=StringUtil.checkValue(PersistenceHelper.getObjectIdentifier ( dept1 ).getStringValue())%></deptoid>
<id><%=StringUtil.checkValue(user.getName())%></id>
<name><%= StringUtil.checkValue(user.getFullName())%></name>
<departmentname><%=StringUtil.checkValue(dept1.getName())%></departmentname>
<duty><%=StringUtil.checkValue(people.getDuty())%></duty>
<dutycode><%=StringUtil.checkValue(people.getDutyCode())%></dutycode>
<email><%=StringUtil.checkValue(user.getEMail())%></email>
<temp><%=StringUtil.checkValue(people.getName())%></temp>
</user>
<%                    
				}
			}
%>
</models>
