package net.sourceforge.pmd.swingui;


import java.util.Enumeration;
import java.util.Properties;


/**
 *
 * @author Donald A. Leckie
 * @since October 1, 2002
 * @version $Revision$, $Date$
 */
public class RuleProperties {
    // Constants
    private static final String SEPARATOR = "&PS;";
    private Properties m_properties = new Properties();

    public RuleProperties(Properties props) {
        m_properties = props;
    }

    public boolean containsKey(String name) {
        return m_properties.containsKey(name);
    }

    /**
     ******************************************************************************
     *
     * Returns an enumeration of the property names in this properties table.
     *
     * @return An enumeration of the property names in this properties table.
     */
    public Enumeration keys() {
        return m_properties.keys();
    }

    public int size() {
        return m_properties.size();
    }

    public String getValue(String name) {
        name = (name == null) ? "" : name.trim();

        if (name.length() > 0) {
            String property = m_properties.getProperty(name);

            if (property != null) {
                int index = property.indexOf(SEPARATOR);

                return (index < 0) ? property : property.substring(0, index);
            }
        }

        return "";
    }

    public String getValueType(String name) {
        try {
            name = (name == null) ? "" : name.trim();

            if (name.length() > 0) {
                String property = m_properties.getProperty(name);

                if (property != null) {
                    int index = property.indexOf(SEPARATOR) + SEPARATOR.length();

                    if (index > 0) {
                        System.out.println(index);

                        return property.substring(index);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public boolean getBooleanValue(String name) {
        return Boolean.getBoolean(getValue(name));
    }

    public double getDoubleValue(String name) {
        try {
            return Double.parseDouble(getValue(name));
        } catch (NumberFormatException exception) {
            return 0.0;
        }
    }

    public int getIntegerValue(String name) {
        try {
            return Integer.parseInt(getValue(name));
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    public String getProperty(String name) {
        return getValue(name);
    }

    public Object setProperty(String name, String value) {
        setValue(name, value);

        return null;
    }

    public void setValue(String name, String value) {
        name = (name == null) ? "" : name.trim();

        if (name.length() > 0) {
            if (value == null) {
                value = "";
            }

            String valueType = getValueType(name);
            String property = value + SEPARATOR + valueType;

            m_properties.setProperty(name, property);
        }
    }

    public void setValueType(String name, String valueType) {
        name = (name == null) ? "" : name.trim();

        if (name.length() > 0) {
            if (valueType == null) {
                valueType = "";
            }

            String value = getValue(name);
            String property = value + SEPARATOR + valueType;

            m_properties.setProperty(name, property);
        }
    }
}
