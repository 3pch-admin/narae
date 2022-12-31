package ext.narae.util.query;

import javax.servlet.http.HttpServletRequest;

import wt.query.QuerySpec;

public class NaraeSimplePageQueryBroker extends NaraePageQueryBroker {
	public NaraeSimplePageQueryBroker(HttpServletRequest req, QuerySpec spec) {
		this(req, spec, "");
	}

	public NaraeSimplePageQueryBroker(HttpServletRequest req, QuerySpec spec, String key) {
		super(req, spec, key);

		setPageCount(5);
		setPsize(5);
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

		sb.append("<table border=0 cellspacing=0 cellpadding=0 width=100% align=center bgcolor=white>");
		sb.append("<tr bgcolor=white>");
		sb.append("  <td>");
		sb.append("		<table border=0 align=center cellpadding=0 cellspacing=0  bgcolor=white>");
		sb.append("			<tr  bgcolor=white>");
		sb.append("				<td width='30' align='center'>");

		if (start > 1) {
			sb.append("<a href='JavaScript:gotoPageQuery" + formName + key
					+ "(1)' class='small'><img src='/Windchill/netmarkets/jsp/narae/portal/images/img_lotte/BBS_start.gif' border='0' align='middle'></a>");
		}
		sb.append("</td>");
		sb.append("				<td width='1' bgcolor='#dddddd'></td>");

		if (start > 1) {
			sb.append("				<td width='30' class='quick' align='center'><a href='JavaScript:gotoPageQuery"
					+ formName + key + "(" + (start - 1)
					+ ")' class='smallblue'><img src='/Windchill/netmarkets/jsp/narae/portal/images/img_lotte/BBS_prev.gif' border='0' align='middle'></a></td>");
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
					+ ")' class='smallblue'><img src='/Windchill/netmarkets/jsp/narae/portal/images/img_lotte/BBS_next.gif' border='0' align='middle'></a></td>");
		}
		;
		sb.append("				<td width='1' bgcolor='#dddddd'></td>");
		sb.append("				<td width='30' align='center'>");

		if (end < ksize) {
			sb.append("<a href='JavaScript:gotoPageQuery" + formName + key + "(" + ksize
					+ ")' class='small'><img src='/Windchill/netmarkets/jsp/narae/portal/images/img_lotte/BBS_end.gif' border='0' align='middle'></a>");
		}
		sb.append("				</td>");
		sb.append("			</tr>");
		sb.append("		</table>");
		sb.append("  </td>");
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
