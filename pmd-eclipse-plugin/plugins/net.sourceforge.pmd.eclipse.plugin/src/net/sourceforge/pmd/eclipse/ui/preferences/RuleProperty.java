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
        return "void"; //rule.getProperty(property);
    }

    /**
     * Sets the value.
     * @param value The value to set
     */
    public void setValue(String value) {
       // rule.getProperties().setProperty(property, value);
    }

}
