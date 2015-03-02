/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.util.StringUtil;

/**
 * No, subclasses are not necessarily scalar per se, they're just easy to parse
 * without error. If you can come up with a better name...
 * 
 * @author Brian Remedios
 * @param <T>
 */
public abstract class AbstractScalarProperty<T> extends AbstractProperty<T> {

    /**
     * Constructor for AbstractScalarProperty.
     * 
     * @param theName String
     * @param theDescription String
     * @param theDefault Object
     * @param theUIOrder float
     */
    protected AbstractScalarProperty(String theName, String theDescription, T theDefault, float theUIOrder) {
        super(theName, theDescription, theDefault, theUIOrder);
    }

    /**
     * @param value String
     * @return Object
     */
    protected abstract Object createFrom(String value);

    /**
     * @param size int
     * @return Object[]
     */
    protected Object[] arrayFor(int size) {
        if (isMultiValue()) {
            throw new IllegalStateException("Subclass '" + this.getClass().getSimpleName()
                    + "' must implement the arrayFor(int) method.");
        }
        throw new UnsupportedOperationException("Arrays not supported on single valued property descriptors.");
    }

    /**
     * @param valueString String
     * @return Object[]
     * @throws IllegalArgumentException
     * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
     */
    @SuppressWarnings("unchecked")
    public T valueFrom(String valueString) throws IllegalArgumentException {

        if (!isMultiValue()) {
            return (T) createFrom(valueString);
        }

        String[] strValues = StringUtil.substringsOf(valueString, multiValueDelimiter());

        Object[] values = arrayFor(strValues.length);
        for (int i = 0; i < strValues.length; i++) {
            values[i] = createFrom(strValues[i]);
        }
        return (T) values;
    }
}
