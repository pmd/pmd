package net.sourceforge.pmd;

import java.util.Enumeration;
import java.util.Properties;

/**
 *
 * @author Donald A. Leckie
 * @since October 1, 2002
 * @version $Revision$, $Date$
 */
public class RuleProperties {

    private Properties properties = new Properties();

    // Constants
    private static final String SEPARATOR = "&PS;";

    public boolean containsKey(String name) {
        return properties.containsKey(name);
    }

    public Enumeration keys() {
        return properties.keys();
    }

    public int size() {
        return properties.size();
    }

    public String getValue(String name) {
        String property = properties.getProperty(name);
        if (property == null) {
            return null;
        }
        int index = property.indexOf(SEPARATOR);
        if (index == -1) {
            return property;
        }
        return property.substring(0, index);
    }

    public String getValueType(String name) {
        if (name.length() > 0) {
            String property = properties.getProperty(name);
            if (property != null) {
                int index = property.indexOf(SEPARATOR) + SEPARATOR.length();
                if (index > 0) {
                    return property.substring(index);
                }
            }
        }
        return "";
    }

    public boolean getBooleanValue(String name) {
        return Boolean.valueOf(getValue(name)).booleanValue();
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

    public void setValue(String name, String value) {
        String valueType = getValueType(name);
        String property = value + SEPARATOR + valueType;
        properties.setProperty(name, property);
    }

    public void setValueType(String name, String valueType) {
        name = (name == null) ? "" : name.trim();

        if (name.length() > 0) {
            if (valueType == null) {
                valueType = "";
            }

            String value = getValue(name);
            String property = value + SEPARATOR + valueType;

            properties.setProperty(name, property);
        }
    }
}
