/*
 * @(#) ConfigExImpl.java  Create on 2006. 6. 15.
 * Copyright (c) e3ps. All rights reserverd
 */
package ext.narae.util.jdf.config;

import java.util.Properties;
import java.util.StringTokenizer;

import ext.narae.util.CharUtil;

/**
 * 
 * @author Choi Seunghwan, skyprda@e3ps.com
 * @version 1.00, 2006. 6. 15.
 * @since 1.4
 */
public class ConfigExImpl implements Config
{
    private Properties DEFAULT = null;

    protected ConfigExImpl(Properties prop)
    {
        DEFAULT = prop;
    }

    public boolean getBoolean(String key)
    {
        boolean value = false;
        try
        {
            if (getString(key).equalsIgnoreCase("true")) value = true;
        }
        catch (Exception e)
        {
            throw new ConfigurationException(this.getClass().getName() + ":getBoolean(key) - Illegal Boolean Key : " + key);
        }
        return value;
    }

    public boolean getBoolean(String key, boolean _default)
    {
        boolean value = false;

        if (getString(key, "" + _default).equalsIgnoreCase("true")) value = true;

        return value;
    }

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

    public int getInt(String key, int _default)
    {
        return Integer.parseInt(getString(key, "" + _default));
    }

    public String getString(String key)
    {
        String value = DEFAULT.getProperty(key);
        if (value == null) throw new ConfigurationException(this.getClass().getName() + ":getString(key) - Illegal String Key : " + key);
        else value = CharUtil.E2K(value);
        return value;
    }

    public String getString(String _key, String _default)
    {
        String value = DEFAULT.getProperty(_key);
        return value == null ? _default : CharUtil.E2K(value);
    }

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

    public long getLong(String key, long _default)
    {
        return Long.parseLong(getString(key, "" + _default));
    }

    public String[] getArray(String key)
    {
        String value = getString(key);
        if (value == null) return null;
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
