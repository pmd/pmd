package net.sourceforge.pmd;

import net.sourceforge.pmd.swingui.IConstants;

import java.util.Enumeration;
import java.util.Properties;

/**
 *
 * @author Donald A. Leckie
 * @since October 1, 2002
 * @version $Revision$, $Date$
 */
public class RuleProperties implements IConstants
{

    private Properties m_properties = new Properties();

    // Constants
    private static final String SEPARATOR = "&PS;";

    /**
     ******************************************************************************
     *
     */
    public RuleProperties()
    {
        super();
    }

    /**
     ******************************************************************************
     *
     * @param name
     *
     * @return
     */
    public boolean containsKey(String name)
    {
        return m_properties.containsKey(name);
    }

    /**
     ******************************************************************************
     *
     * Returns an enumeration of the property names in this properties table.
     *
     * @return An enumeration of the property names in this properties table.
     */
    public Enumeration keys()
    {
        return m_properties.keys();
    }

    /**
     ******************************************************************************
     *
     * @return
     */
    public int size()
    {
        return m_properties.size();
    }

    /**
     ******************************************************************************
     *
     * @param name
     *
     * @return
     */
    public String getValue(String name)
    {
        name = (name == null) ? EMPTY_STRING : name.trim();

        if (name.length() > 0)
        {
            String property = m_properties.getProperty(name);

            if (property != null)
            {
                int index = property.indexOf(SEPARATOR);

                return (index < 0) ? property : property.substring(0, index);
            }
        }

        return EMPTY_STRING;
    }

    /**
     ******************************************************************************
     *
     * @param name
     *
     * @return
     */
    public String getValueType(String name)
    {
        name = (name == null) ? EMPTY_STRING : name.trim();

        if (name.length() > 0)
        {
            String property = m_properties.getProperty(name);

            if (property != null)
            {
                int index = property.indexOf(SEPARATOR) + SEPARATOR.length();

                if (index > 0)
                {
                    return property.substring(index);
                }
            }
        }

        return EMPTY_STRING;
    }

    /**
     *******************************************************************************
     *
     * @param name
     *
     * @return
     */
    public boolean getBooleanValue(String name)
    {
        return Boolean.getBoolean(getValue(name));
    }

    /**
     *******************************************************************************
     *
     * @param name
     *
     * @return
     */
    public double getDoubleValue(String name)
    {
        try
        {
            return Double.parseDouble(getValue(name));
        }
        catch (NumberFormatException exception)
        {
            return 0.0;
        }
    }

    /**
     *******************************************************************************
     *
     * @param name
     *
     * @return
     */
    public int getIntegerValue(String name)
    {
        try
        {
            return Integer.parseInt(getValue(name));
        }
        catch (NumberFormatException exception)
        {
            return 0;
        }
    }

    /**
     *******************************************************************************
     *
     * @param name
     *
     * @return
     */
    public String getProperty(String name)
    {
        return getValue(name);
    }

    /**
     *******************************************************************************
     *
     * @param name
     * @param value
     */
    public Object setProperty(String name, String value)
    {
        setValue(name, value);

        return null;
    }

    /**
     *******************************************************************************
     *
     * @param name
     * @param value
     */
    public void setValue(String name, String value)
    {
        name = (name == null) ? EMPTY_STRING : name.trim();

        if (name.length() > 0)
        {
            if (value == null)
            {
                value = EMPTY_STRING;
            }

            String valueType = getValueType(name);
            String property = value + SEPARATOR + valueType;

            m_properties.setProperty(name, property);
        }
    }

    /**
     *******************************************************************************
     *
     * @param name
     * @param valueType
     */
    public void setValueType(String name, String valueType)
    {
        name = (name == null) ? EMPTY_STRING : name.trim();

        if (name.length() > 0)
        {
            if (valueType == null)
            {
                valueType = EMPTY_STRING;
            }

            String value = getValue(name);
            String property = value + SEPARATOR + valueType;

            m_properties.setProperty(name, property);
        }
    }
}