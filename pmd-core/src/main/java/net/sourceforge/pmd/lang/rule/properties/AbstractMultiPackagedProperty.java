/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

/**
 * 
 * @author Brian Remedios
 * @param <T>
 */
public abstract class AbstractMultiPackagedProperty<T> extends AbstractPackagedProperty<T> {

    protected static final char DELIMITER = '|';

    /**
     * Constructor for AbstractMultiPackagedProperty.
     * 
     * @param theName String
     * @param theDescription String
     * @param theDefault T
     * @param theLegalPackageNames String[]
     * @param theUIOrder float
     */
    protected AbstractMultiPackagedProperty(String theName, String theDescription, T theDefault,
            String[] theLegalPackageNames, float theUIOrder) {
        super(theName, theDescription, theDefault, theLegalPackageNames, theUIOrder);
    }

    /**
     * @return boolean
     * @see net.sourceforge.pmd.PropertyDescriptor#isMultiValue()
     */
    @Override
    public boolean isMultiValue() {
        return true;
    }

    /**
     * @return String
     */
    protected String defaultAsString() {
        return asDelimitedString(defaultValue());
    }
}
