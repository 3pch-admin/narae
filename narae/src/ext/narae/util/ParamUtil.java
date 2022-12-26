/**
 * @(#) ParamUtil.java Copyright (c) jerred. All rights reserverd
 * @version 1.00
 * @since jdk 1.4.02
 * @createdate 2004. 3. 3.
 * @author Cho Sung Ok, jerred@bcline.com
 * @desc
 */

package ext.narae.util;

import javax.servlet.http.HttpServletRequest;

import ext.narae.util.jdf.config.ConfigImpl;

/**
 */
public class ParamUtil {
    private static boolean isDecode = ConfigImpl.getInstance ().getBoolean ( "http.parameter.isdecode" );

    public static String getStrParameter(String str, String defaultStr) {
        if (str == null || str.equals ( "" )) return defaultStr;
        else {
            if (isDecode) return CharUtil.E2K ( str.trim () );
            else return str.trim ();
        }
    }

    public static String getStrParameter(String str) {
        if (str == null || str.equals ( "" )) return "";
        else {
            if (isDecode) return CharUtil.E2K ( str.trim () );
            else return str.trim ();
        }
    }

    public static String checkStrParameter(String str, String defaultStr) {
        if (str == null || str.equals ( "" )) return defaultStr;
        else return str.trim ();
    }

    public static String checkStrParameter(String str) {
        if (str == null || str.equals ( "" )) return "";
        else return str.trim ();
    }

    public static String[] getStrParameterValues(String[] str) {
        String[] defaultReturnValue = {};
        if (str == null) return defaultReturnValue;
        if (str.length == 0) return defaultReturnValue;

        if (isDecode) {
            String[] returnValue = new String[str.length];
            for (int i = 0; i < str.length; i++) {
                returnValue[i] = CharUtil.E2K ( str[i] );
            }
            return returnValue;
        } else {
            return str;
        }
    }

    public static int getIntParameter(String str, int ifNulltoReplace) {
        try {
            if (str == null || str.equals ( "" )) return ifNulltoReplace;
            else return Integer.parseInt ( str.toString () );
        } catch (NumberFormatException e) {
            return ifNulltoReplace;
        }
    }

    public static int getIntParameter(String str) {
        try {
            if (str == null || str.equals ( "" )) return 0;
            else return Integer.parseInt ( str.toString () );
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * @see     getParameter(HttpServletRequest request, String name, String defaultVal = "")
     */
    public static String getParameter(HttpServletRequest request, String name) {
        return getParameter(request, name, "");
    }
    
    /**
     * @param   request     HttpServletRequest
     *          name        얻고자 하는 값의 키값
     *          defaultVal  
     * @return  디폴트 값이 지정이 안되었고 <code>null</code>값이라면 
     *          <code>defaultVal</code>값 리턴, 그외의 경우는 해당 value값 리턴
     */
    public static String getParameter(HttpServletRequest request, String name, String defaultVal) {
        String temp = request.getParameter(name);
        if(temp != null) {
            return temp.trim();
        } else {
            return defaultVal;
        }
    }
}
