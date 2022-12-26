/**
 * @(#) ConfigImp.java
 * Copyright (c) jerred. All rights reserverd
 * 
 *	@version 1.00
 *	@since jdk 1.4.02
 *	@createdate 2004. 3. 3.
 *	@author Cho Sung Ok, jerred@bcline.com
 *	@desc	
 */

package ext.narae.util.jdf.config;

import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import ext.narae.util.CharUtil;

/**
 * <b>이 소스는 <a href="http://www.javaservice.net/">JavaService Net</a>의 <br>
 * Java Development Framework(JDF) ver1.0.0을 기초로 만들어졌음을 밝힙니다.</b>
 * <p>
 * <code>ConfigImpl</code>클래스는 하나의 Configuration에 대한 하나의 객체를 생성하는 클래스 이다.<br>
 * 즉 특정 Configuration File을 이 클래스를 이용해서 사용할수 있게 된다.
 */
public class ConfigImpl implements Config
{
    private static Properties DEFAULT = null;
    private static ConfigImpl INSTANCE = null;

    /**
     * 객체 생성을 방지하기 위해서 생성자를 Private로 선언
     */
    private ConfigImpl()
    {
        this.initialize();
    }

    /**
     * 객체를 초기화 시켜주는 Method
     * 
     * @exception <code>e3ps.config.ConfigurationException</code> Configutation
     *                File을 찾을수 없거나 로드할수 없을때 발생
     */
    private void initialize()
    {
        if (ConfigImpl.DEFAULT == null)
        {
            // 클래스를 로딩하면서 기본적인 정보를 가지고 있는 System Properties 속성인 config.fileName
            // 에 해당하는 파일을 로딩한다.
            try
            {
//                WTProperties prop = WTProperties.getServerProperties();
//                String configFileName = prop.getProperty("e3ps.config.fileName");

//                File file = new File(configFileName);
//                FileInputStream fin = new FileInputStream(file);
//                ConfigImpl.DEFAULT = new Properties();
//                ConfigImpl.DEFAULT.load(fin);
//                fin.close();
                
                InputStream is = this.getClass().getResourceAsStream("/ext/narae/narae.properties");
                ConfigImpl.DEFAULT = new Properties();
                ConfigImpl.DEFAULT.load(is);
                is.close();
            }
            catch (Exception e)
            {
                System.err.println(this.getClass().getName() + ":initialize() ConfigImpl - Can't initialize : "
                        + e.getMessage());
            }
        }
    }

    /**
     * 특정 키에 해당하는 Configuration File을 초기화 시켜주는 Method
     * 
     * @return <code>e3ps.config.Configuraion</code> key에 해당하는 Configuration
     *         File로 초기화된 Configuration객체
     * @param key
     *            <code>java.lang.String</code> neocast.properties에 설정된 특정 키
     */
    public synchronized static Config getInstance()
    {
        if (INSTANCE == null)
            INSTANCE = new ConfigImpl();
        return INSTANCE;
    }

    /**
     * system의 Root를 리턴하는 Method
     * 
     * @return <code>java.lang.String</code>
     */
    public static String getRoot()
    {
        return DEFAULT.getProperty("root.path");
    }

    /**
     * key를 받아서 key에 해당하는 값을 <code>boolean</code>형태로 리턴하는 Method<br>
     * key의 값이 없을 경우 _default 로 리턴한다.
     * 
     * @param key
     *            <code>java.lang.String</code> 얻고자 하는 value의 key
     * @return <code>boolean</code> key에 해당하는 value
     * @exception <code>e3ps.config.ConfigurationException</code> 잘못된 key를 이용했거나
     *                설정된 값이 boolean이 아닌 경우 발생
     */
    public boolean getBoolean(String key)
    {
        boolean value = false;
        try
        {
            if (getString(key).equalsIgnoreCase("true"))
                value = true;
        }
        catch (Exception e)
        {
            throw new ConfigurationException(this.getClass().getName() + ":getBoolean(key) - Illegal Boolean Key : "
                    + key);
        }
        return value;
    }

    /**
     * key를 받아서 key에 해당하는 값을 <code>boolean</code>형태로 리턴하는 Method
     * 
     * @return <code>boolean</code>
     * @param key
     *            <code>java.lang.String</code>
     */
    public boolean getBoolean(String key, boolean _default)
    {
        boolean value = false;

        if (getString(key, "" + _default).equalsIgnoreCase("true"))
            value = true;

        return value;
    }

    /**
     * key를 받아서 key에 해당하는 값을 <code>int</code>형태로 리턴하는 Method
     * 
     * @param key
     *            <code>java.lang.String</code> 얻고자 하는 value의 key
     * @return <code>int</code> key에 해당하는 value
     * @exception <code>e3ps.config.ConfigurationException</code> 잘못된 key를 이용했거나
     *                설정된 값이 int이 아닌 경우 발생
     */
    public int getInt(String key)
    {
        int value = -1;
        try
        {
            value = Integer.parseInt(getString(key));
        }
        catch (Exception e)
        {
            throw new ConfigurationException(this.getClass().getName() + ":getInt(key) - Illegal Integer Key : " + key);
        }
        return value;
    }

    /**
     * key를 받아서 key에 해당하는 값을 <code>int</code>형태로 리턴하는 Method <br>
     * key의 값이 없을 경우 _default 로 리턴한다.
     * 
     * @param key
     * @param _default
     * @return
     */
    public int getInt(String key, int _default)
    {
        return Integer.parseInt(getString(key, "" + _default));
    }

    /**
     * key를 받아서 key에 해당하는 값을 <code>java.lang.String</code>형태로 리턴하는 Method
     * 
     * @param key
     *            <code>java.lang.String</code> 얻고자 하는 value의 key
     * @return <code>java.lang.String</code> key에 해당하는 value
     * @exception <code>e3ps.config.ConfigurationException</code> 잘못된 key를 이용했을
     *                경우 발생
     */
    public String getString(String key)
    {

        String value = DEFAULT.getProperty(key);
        if (value == null)
            throw new ConfigurationException(this.getClass().getName() + ":getString(key) - Illegal String Key : "
                    + key);
        else
            value = CharUtil.E2K(value);
        return value;
    }

    /**
     * key를 받아서 key에 해당하는 값을 <code>java.lang.String</code>형태로 리턴하는 Method
     * key의 값이 없을 경우 _default 로 리턴한다.
     */
    public String getString(String _key, String _default)
    {
        String value = DEFAULT.getProperty(_key);
        return value == null ? _default : CharUtil.E2K(value);
    }

    /**
     * key를 받아서 key에 해당하는 값을 <code>long</code>형태로 리턴하는 Method
     * 
     * @param key
     *            <code>java.lang.String</code> 얻고자 하는 value의 key
     * @return <code>long</code> key에 해당하는 value
     * @exception <code>e3ps.config.ConfigurationException</code> 잘못된 key를 이용했거나
     *                설정된 값이 double이 아닌 경우 발생
     */
    public long getLong(String key)
    {
        long value = -1;
        try
        {
            value = Long.parseLong(getString(key));
        }
        catch (Exception e)
        {
            throw new ConfigurationException(this.getClass().getName() + ":getLong(key) - Illegal String Key : " + key);
        }
        return value;
    }

    /**
     * key를 받아서 key에 해당하는 값을 <code>long</code>형태로 리턴하는 Method <br>
     * key의 값이 없을 경우 _default 로 리턴한다.
     * 
     * @param key
     * @param _default
     * @return
     */
    public long getLong(String key, long _default)
    {
        return Long.parseLong(getString(key, "" + _default));
    }

    /**
     * Default 이외의 properties 를 가져온다
     */
    private ResourceBundle getBundle(String add)
    {
        try
        {
            return ResourceBundle.getBundle(add, Locale.getDefault());
        }
        catch (Exception e)
        {
            System.err.println(this.getClass().getName() + ":getBundle(add) ConfigImpl - Can't initialize " + add
                    + "  : " + e.getMessage());
            return null;
        }
    }

    /**
     * Default 이외의 properties 에서 값을 읽어온다
     */
    public String getStringFromOther(String add, String key)
    {
        String value = getBundle(add).getString(key);
        if (value == null)
            throw new ConfigurationException(this.getClass().getName()
                    + ":getStringFromOther(add,key) - Illegal String Key : " + key);
        else
            value = CharUtil.E2K(value);
        return value;
    }

    /**
     * 주어진 key의 값이 ,로 구성되어있을 경우 배열로 리턴한다.
     * 
     * @param key
     * @return
     */
    public String[] getArray(String key)
    {
        String value = getString(key);
        if (value == null)
            return null;
        StringTokenizer st = new StringTokenizer(value, ";");
        String[] result = new String[st.countTokens()];

        int idx = 0;
        while (st.hasMoreElements())
        {
            result[idx++] = st.nextToken();
        }

        return result;
    }
}
