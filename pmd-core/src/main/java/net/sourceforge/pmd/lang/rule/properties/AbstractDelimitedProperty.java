/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

/**
 * Concrete subclasses represent properties whose values when serialized onto a string can 
 * be problematic without specifying a unique delimiter that won't appear in the value set.
 * 
 * @author Brian Remedios
 * @param <T>
 */
public abstract class AbstractDelimitedProperty<T> extends AbstractProperty<T> {

    private char multiValueDelimiter;
    
    private static final String DELIM_ID = "delimiter";
    
    /**
     * Constructor for AbstractDelimitedProperty.
     * @param theName String
     * @param theDescription String
     * @param theDefault T
     * @param delimiter char
     * @param theUIOrder float
     */
    protected AbstractDelimitedProperty(String theName, String theDescription, T theDefault, char delimiter, float theUIOrder) {
        super(theName, theDescription, theDefault, theUIOrder);
        
        multiValueDelimiter = delimiter;
    }

    protected static char delimiterIn(Map<String, String> parameters) {
        if (!parameters.containsKey(DELIM_ID)) {
            throw new IllegalArgumentException("missing delimiter value");
        }
        
        return parameters.get(DELIM_ID).charAt(0);
    }
    
    /**
     * @param attributes Map<String,String>
     */
    protected void addAttributesTo(Map<String, String> attributes) {
        super.addAttributesTo(attributes);
        
        attributes.put(DELIM_ID, Character.toString(multiValueDelimiter));
    }
    
    /**
     * @return String
     */
    protected String defaultAsString() {
        return asDelimitedString(defaultValue(), multiValueDelimiter);
    }
    
    /**
     * @param aDelimiter char
     */
    protected void multiValueDelimiter(char aDelimiter) {
        multiValueDelimiter = aDelimiter;
    }
    
    /**
     * @return char
     * @see net.sourceforge.pmd.PropertyDescriptor#multiValueDelimiter()
     */
    public char multiValueDelimiter() {
        return multiValueDelimiter;
    }
    
    /**
     * @return boolean
     * @see net.sourceforge.pmd.PropertyDescriptor#isMultiValue()
     */
    @Override
    public boolean isMultiValue() {
        return true;
    }
}
