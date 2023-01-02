package ext.narae.util.query;

import javax.servlet.http.HttpServletRequest;

import ext.narae.util.CommonUtil;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;

public class NaraePageQueryBroker implements wt.method.RemoteAccess, java.io.Serializable {

	int psize = 20;
	int cpage = 1;
	int total = 0;
	int pageCount = 10;
	int topListCount = 0;
	long sessionid;

	QuerySpec qs;
	HttpServletRequest req;
	PagingQueryResult result;

	String key;

	boolean isAdvanced = false;

	String sessionKeyword;

	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;

	public NaraePageQueryBroker(HttpServletRequest req, QuerySpec spec) {
		this(req, spec, "");
	}

	public NaraePageQueryBroker(HttpServletRequest req, QuerySpec spec, String key) {
		this.req = req;
		this.qs = spec;
		this.key = key;
		String sessionidstring = req.getParameter("sessionid" + key);
		if (sessionidstring == null || sessionidstring.length() == 0)
			sessionidstring = "0";
		sessionid = Long.parseLong(sessionidstring.trim());

		sessionKeyword = req.getParameter("sessionKeyword" + key);
	}

	public void setAdvanced(boolean isAdvanced) {
		this.isAdvanced = isAdvanced;
	}

	public static PagingQueryResult openPagingSession(int i, int j, StatementSpec qs, boolean isAdvanced)
			throws Exception {

		if (isAdvanced && !SERVER) {
			Class argTypes[] = new Class[] { int.class, int.class, StatementSpec.class, boolean.class };
			Object args[] = new Object[] { new Integer(i), new Integer(j), qs, new Boolean(isAdvanced) };
			return (PagingQueryResult) wt.method.RemoteMethodServer.getDefault().invoke("openPagingSession",
					"com.e3ps.common.web.PageQueryBroker", null, argTypes, args);
		}

		if (isAdvanced && !qs.isAdvancedQueryEnabled()) {
			qs.setAdvancedQueryEnabled(true);
		}

		return PagingSessionHelper.openPagingSession(i, j, qs);
	}

	public static PagingQueryResult fetchPagingSession(int i, int j, long sessionid, StatementSpec qs,
			boolean isAdvanced) throws Exception {

		try {
			return PagingSessionHelper.fetchPagingSession(i, j, sessionid);
		} catch (Exception ex) {
			return openPagingSession(i, j, qs, isAdvanced);
		}
	}

	public PagingQueryResult search() throws Exception {
		String pagestring = req.getParameter("tpage" + key);
		if (pagestring != null && pagestring.length() > 0)
			cpage = Integer.parseInt(pagestring);
		String psizestring = req.getParameter("psize" + key);
		if (psizestring != null && psizestring.length() > 0)
			psize = Integer.parseInt(psizestring);
		if (sessionid <= 0) {
			result = openPagingSession(0, psize, qs, isAdvanced);
		} else {
			result = fetchPagingSession((cpage - 1) * psize, psize, sessionid, qs, isAdvanced);
		}
		if (result != null) {
			total = result.getTotalSize();
			sessionid = result.getSessionId();
			topListCount = total - ((cpage - 1) * psize);
		}
		return result;
	}

//	public static ArrayList instreamSearch(String classname,String keyword)throws Exception{
//		
//		if(!SERVER){
//			Class argTypes[] = new Class[]{String.class,String.class};
//			Object args[] = new Object[]{classname,keyword};
//			return (ArrayList)wt.method.RemoteMethodServer.getDefault().invoke(
//					"instreamSearch",
//					"com.e3ps.common.web.PageQueryBroker",
//					null,
//					argTypes,
//					args);
//		}
//		
//		ArrayList result = new ArrayList();
//		
//
//		// �˻� ���� �Է� ��� �ʵ��  ��ǥ(,) �� ���� ������ �Է� ����
//		IndexSearchInput input = new IndexSearchInput();
//		
//		input.setSearchString(keyword);
//	    input.setBusinessTypes(classname); 
//	    input.setQueryLanguage(Locale.KOREA.getLanguage());
//	    input.setCollections("wblib");
//	    Vector fetchFields = new Vector();
//	    fetchFields.add("ufid");
//	    input.setFetchFields(fetchFields.elements());
//	    
//	    /* �߰� �˻� ���� �ʵ�
//	    input.setSearchFields(String s);
//		
//		input.setContainerReference(String s);
//		input.setFilterAccess(true);
//		input.setOrganizationReference(String s);
//		input.setResultView(String s);
//		input.setMinHits(int i);
//		input.setMaxDocs(int i);
//		input.setQueryPrefs(Hashtable);
//		input.setSearchTime(int i);
//		input.setSortFields(ArrayList arr);
//		input.setSortFields(String s, String s1);
//		input.setSortOrder(ArrayList list);
//		input.setSpellCheck(true);
//		input.setSearchFields(s);	//�˻� ��� �ʵ�
//	    input.setCollapse(true);
//		*/
//	    
//		InstreamSearchDelegate del = new InstreamSearchDelegate();
//		del.setSearchInput(input);
//		Group gReturn = del.search(null);
//
//		int i = gReturn.getElementCount();
//		
//		 for ( int j = 0; j < i; j++ ) {
//			 
//	         Element element = gReturn.getElementAt(j);
//	         
//	         Att att = element.getAtt("obid");
//	         
//	         if(att!=null){
//	        	 String oid = att.getValue().toString();
//	        	 if(oid.startsWith("OR"))continue;
//	        	 result.add(oid);
//	         }
//	     }
//		 
//		return result;
//	}

//	public QueryResult searchKeyword(String classname,String keyword) throws Exception{
//		
//		if(sessionKeyword!=null){
//			keyword = sessionKeyword;
//		}
//		else{
//			sessionKeyword = keyword;
//		}
//		String pagestring = req.getParameter("tpage" + key);
//		if(pagestring!=null && pagestring.length()>0)cpage = Integer.parseInt(pagestring);
//		String psizestring = req.getParameter("psize" + key);
//		if(psizestring!=null && psizestring.length()>0)psize = Integer.parseInt(psizestring);
//		
//		
//		ReferenceFactory rf = new ReferenceFactory();
//		
//		ObjectVector ifc = new ObjectVector();
//		
//		ArrayList list = instreamSearch(classname, keyword);
//		
//		total = list.size();
//	
//		for(int j=0; j< list.size(); j++){
//			if( ((cpage-1)*psize < (j+1)) && ( (j+1) <= cpage*psize )  ){
//				try{
//					String ufid = (String)list.get(j);
//					WTReference ref = (WTReference)rf.getReference(ufid);
//					Object o = ref.getObject();
//					ifc.addElement(new Object[]{new Object[]{o}});
//				}catch(Exception ex){
//					ex.printStackTrace();
//				}
//			}
//		}
//		
//		result = new PagingQueryResult();
//
//		result.append(ifc);
//		
//		topListCount = total - ((cpage - 1) * psize);
//
//		return result;
//	}

	public void setPsize(int psize) {
		this.psize = psize;
	}

	public void setCpage(int cpage) {
		this.cpage = cpage;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public void setSessionid(long sessionid) {
		this.sessionid = sessionid;
	}

	public void setQuerySpec(QuerySpec qs) {
		this.qs = qs;
	}

	public void setRequest(HttpServletRequest request) {
		this.req = request;
	}

	public void setResult(PagingQueryResult result) {
		this.result = result;
	}

	public int getPsize() {
		return psize;
	}

	public int getCpage() {
		return cpage;
	}

	public int getTotal() {
		return total;
	}

	public int getPageCount() {
		return pageCount;
	}

	public long getSessionid() {
		return sessionid;
	}

	public QuerySpec getQuerySpec() {
		return qs;
	}

	public HttpServletRequest getRequest() {
		return req;
	}

	public PagingQueryResult getResult() {
		return result;
	}

	public String getHtml(String formName) {

		int ksize = total / psize;
		int x = total % psize;
		if (x > 0)
			ksize++;
		int temp = cpage / pageCount;
		if ((cpage % pageCount) > 0)
			temp++;
		int start = (temp - 1) * pageCount + 1;
		int end = start + pageCount - 1;
		if (end > ksize) {
			end = ksize;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("<script>");
		sb.append("function gotoPageQuery" + formName + key + "(p){");
		sb.append("document." + formName + ".sessionid" + key + ".value='" + sessionid + "';");
		sb.append("document." + formName + ".tpage" + key + ".value=p;");
		sb.append("document." + formName + ".submit();");
		sb.append("}");
		sb.append("</script>");

		sb.append("<input type=hidden name=sessionid" + key + " value=" + sessionid + ">");
		sb.append("<input type=hidden name=tpage" + key + " value=" + cpage + ">");

		if (sessionKeyword != null) {
			sb.append("<input type=hidden name=sessionKeyword" + key + " value=" + sessionKeyword + ">");
		}

		sb.append("<table border=0 cellspacing=0 cellpadding=0 width=100% align=center bgcolor=white>");
		sb.append("<tr bgcolor=white>");
		if (CommonUtil.isUSLocale())
			sb.append("  <td class='small' width=200><span class='small'>[Pages:" + ksize + "][Items:" + total
					+ "]</span></td>");
		else
			sb.append("  <td class='small' width=200><span class='small'>[전체페이지" + ksize + "][전체개수:" + total
					+ "]</span></td>");
		sb.append("  <td>");
		sb.append("		<table border=0 align=center cellpadding=0 cellspacing=0  bgcolor=white>");
		sb.append("			<tr  bgcolor=white>");
		sb.append("				<td width='30' align='center'>");

		if (start > 1) {
			sb.append("<a href='JavaScript:gotoPageQuery" + formName + key
					+ "(1)' class='small'><img src='/plm/jsp/portal/images/img_lotte/BBS_start.gif' border='0' align='middle'></a>");
		}
		sb.append("</td>");
		sb.append("				<td width='1' bgcolor='#dddddd'></td>");

		if (start > 1) {
			sb.append("				<td width='30' class='quick' align='center'><a href='JavaScript:gotoPageQuery"
					+ formName + key + "(" + (start - 1)
					+ ")' class='smallblue'><img src='/plm/jsp/portal/images/img_lotte/BBS_prev.gif' border='0' align='middle'></a></td>");
			sb.append("				<td width='1' bgcolor='#dddddd'></td>");
		}

		for (int i = start; i <= end; i++) {

			sb.append(
					"				<td style='padding:2 8 0 7;cursor:hand' onMouseOver='this.style.background=\"#ECECEC\"' OnMouseOut='this.style.background=\"\"' class='nav_on' onclick='gotoPageQuery"
							+ formName + key + "(" + i + ")'>");

			if (i == cpage) {
				sb.append("<b>");
			}
			sb.append("" + i + "</td>");
		}
		if (end < ksize) {
			sb.append("				<td width='1' bgcolor='#dddddd'></td>");
			sb.append("				<td width='30' align='center'><a href='JavaScript:gotoPageQuery" + formName + key
					+ "(" + (end + 1)
					+ ")' class='smallblue'><img src='/plm/jsp/portal/images/img_lotte/BBS_next.gif' border='0' align='middle'></a></td>");
		}
		;
		sb.append("				<td width='1' bgcolor='#dddddd'></td>");
		sb.append("				<td width='30'align='center'>");

		if (end < ksize) {
			sb.append("<a href='JavaScript:gotoPageQuery" + formName + key + "(" + ksize
					+ ")' class='small'><img src='/plm/jsp/portal/images/img_lotte/BBS_end.gif' border='0' align='middle'></a>");
		}
		sb.append("				</td>");
		sb.append("			</tr>");
		sb.append("		</table>");
		sb.append("  </td>");
		sb.append("	<td class='small' align='right'  width=200><select name=psize" + key + " onchange='gotoPageQuery"
				+ formName + key + "(" + cpage + ")'>");
		sb.append(" <option value='10' " + (psize == 10 ? "selected" : "") + ">10</option>");
		sb.append(" <option value='15' " + (psize == 15 ? "selected" : "") + ">15</option>");
		sb.append(" <option value='20' " + (psize == 20 ? "selected" : "") + ">20</option>");
		sb.append(" <option value='30' " + (psize == 30 ? "selected" : "") + ">30</option>");
		sb.append(" <option value='50' " + (psize == 50 ? "selected" : "") + ">50</option>");
		sb.append(" <option value='100' " + (psize == 100 ? "selected" : "") + ">100</option>");
		if (CommonUtil.isUSLocale())
			sb.append("</select>items View</td>");
		else
			sb.append("</select>개 보기</td>");
		sb.append("</tr>");
		sb.append("</table>");

		return sb.toString();
	}

	public int getTopListCount() {
		return topListCount;
	}

	public void setTopListCount(int topListCount) {
		this.topListCount = topListCount;
	}

}