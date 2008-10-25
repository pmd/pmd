package net.sourceforge.pmd.eclipse.ui.preferences;

import net.sourceforge.pmd.Rule;

/**
 * Helper class to display rule properties in a table
 * 
 * @author Philippe Herlin
 * @deprecated
 */
public class RuleProperty {
    private Rule rule;
    private String property;
    
    /**
     * Constructor with a Rule object and a key
     */
    public RuleProperty(Rule rule, String key) {
        this.rule = rule;
        property = key;
    }
    
    /**
     * Returns the booleanValue.
     * @return boolean
     */
    public boolean isBooleanValue() {
        return rule.getBooleanProperty(property);
    }

    /**
     * Returns the doubleValue.
     * @return double
     */
    public double getDoubleValue() {
        return rule.getDoubleProperty(property);
    }

    /**
     * Returns the integerValue.
     * @return int
     */
    public int getIntegerValue() {
        return rule.getIntProperty(property);
    }

    /**
     * Returns the property.
     * @return String
     */
    public String getProperty() {
        return property;
    }

    /**
     * Returns the value.
     * @return String
     */
    public String getValue() {
        return rule.getStringProperty(property);
    }

    /**
     * Sets the booleanValue.
     * @param booleanValue The booleanValue to set
     */
    public void setBooleanValue(boolean booleanValue) {
        rule.getProperties().setProperty(property, String.valueOf(booleanValue));
    }

    /**
     * Sets the doubleValue.
     * @param doubleValue The doubleValue to set
     */
    public void setDoubleValue(double doubleValue) {
        rule.getProperties().setProperty(property, String.valueOf(doubleValue));
    }

    /**
     * Sets the integerValue.
     * @param integerValue The integerValue to set
     */
    public void setIntegerValue(int integerValue) {
        rule.getProperties().setProperty(property, String.valueOf(integerValue));
    }

    /**
     * Sets the value.
     * @param value The value to set
     */
    public void setValue(String value) {
        rule.getProperties().setProperty(property, value);
    }

}
