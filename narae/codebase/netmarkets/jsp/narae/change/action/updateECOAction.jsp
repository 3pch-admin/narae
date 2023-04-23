<%@page import="ext.narae.service.change.beans.ChangeHelper"%>
<%@page import="ext.narae.service.change.beans.ChangeHelper2"%>
<%@page import="ext.narae.util.content.NeoHttpRequestField"%>
<%@page import="ext.narae.util.content.NeoHttpRequest"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*, java.util.*" %>
<%@ page import="ext.narae.service.drawing.*" %>
<%@ page import="wt.epm.*" %>
<%@ page import="wt.fc.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="ext.narae.service.change.*" %>
<%@ page import="ext.narae.component.*" %>
<jsp:useBean id="wtcontext" class="wt.httpgw.WTContextBean" scope="request" />
<jsp:setProperty name="wtcontext" property="request" value="<%= request %>" />
<%
// 파일 추출
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
	HashMap<String,String> secondaryDelFile = new HashMap<String,String>();
	int secondaryCount = 0;
	int secondaryDelFileCount = 0;
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
			if( field.getFieldName().equals("secondaryDelFile") ) {
				secondaryDelFile.put(field.getValue(), field.getValue());
				secondaryDelFileCount++;
			} else {
				hash.put(field.getFieldName(), field.getValue());
			}
		}
		//System.out.println("=======> " + field.getFieldName() + " : " + (field.IsFile() ? "file" : "not file") + ", " + field.getSize() + "|" + field.getValue() + "|" + field.getFileName());
	}
	
	if( secondaryCount > 0 ) {
		hash.put("secondary", secondary);
		hash.put("secondaryFileName", secondaryName);
	}
	
	if( secondaryDelFileCount > 0 ) {
		hash.put("secondaryDelFile",secondaryDelFile);
	}
	
	ChangeHelper2.updateECO(hash, false);

%>
<script>
parent.showFinished('save');
</script>