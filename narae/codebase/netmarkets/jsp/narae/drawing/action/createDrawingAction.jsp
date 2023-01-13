<%@page import="ext.narae.service.drawing.beans.DrawingHelper2"%>
<%@page import="ext.narae.util.content.NeoHttpRequestField"%>
<%@page import="ext.narae.util.content.NeoHttpRequest"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="ext.narae.service.part.*" %>
<%@ page import="wt.epm.*" %>
<%@ page import="wt.fc.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="ext.narae.service.drawing.*" %>
<%@ page import="ext.narae.component.*" %>
<jsp:useBean id="wtcontext" class="wt.httpgw.WTContextBean" scope="request" />
<jsp:setProperty name="wtcontext" property="request" value="<%= request %>" />
<%
// 파일 추출
	//response.getWriter().write("==================   여기서 부터 multipart/form-data 수신 결과  ===============");
    NeoHttpRequest _request= null;
	try{
		_request = new NeoHttpRequest(request);
	}catch(Exception e){
		int i = 0;
	}

	List<NeoHttpRequestField> fields = _request.getFields();
	
	HashMap<String,Object> hash = new HashMap<String,Object>();	
	List<InputStream> secondary = new ArrayList<InputStream>();
	List<String> secondaryName = new ArrayList<String>();
	int secondaryCount = 0;
	int primaryCount = 0;
	for(NeoHttpRequestField field : fields){
		if( field.IsFile() ) {
			if( field.getFieldName().equals("primary") ) {
				hash.put("primary", new SerializableInputStream(field.getInputStream()));
				hash.put("primaryFileName", field.getFileName());
			} else {
				if( field.getSize() > 0 ) {
					secondary.add(new SerializableInputStream(field.getInputStream()));
					secondaryName.add(field.getFileName());
					secondaryCount++;
				}
			}
		} else {
			hash.put(field.getFieldName(), field.getValue());
		}
		//System.out.println("=======> " + field.getFieldName() + " : " + (field.IsFile() ? "file" : "not file") + ", " + field.getSize() + "|" + field.getValue() + "|" + field.getFileName() +"<br>\r\n");
	}
	
	if( secondaryCount > 0 ) {
		hash.put("secondary", secondary);
		hash.put("secondaryFileName", secondaryName);
	}
	
	String oid = "";
	try {
		EPMDocument part = DrawingHelper2.create(hash);
		if( part != null ) {
			oid = part.getPersistInfo().getObjectIdentifier().toString();
		}
	} catch(Exception e) {
		e.printStackTrace();
	}

%>
<script>
parent.showFinished('<%=oid%>');
</script>