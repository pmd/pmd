/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a property type that supports multiple Boolean values.
 *
 * @author Brian Remedios
 */
public class BooleanMultiProperty extends AbstractMultiValueProperty<Boolean> {

    /**
     * Factory.
     */
    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<Boolean>(
        Boolean.class) {
        @Override
        public BooleanMultiProperty createWith(Map<String, String> valuesById) {
            char delimiter = delimiterIn(valuesById);
            return new BooleanMultiProperty(nameIn(valuesById), descriptionIn(valuesById),
                                            booleanValuesIn(defaultValueIn(valuesById), delimiter), 0f);
        }
    };

    /**
     * Constructor for BooleanMultiProperty that allows for multiple values.
     *
     * @param theName        String
     * @param theDescription String
     * @param defaultValues  Boolean[]
     * @param theUIOrder     float
     */
    public BooleanMultiProperty(String theName, String theDescription, Boolean[] defaultValues, float theUIOrder) {
        super(theName, theDescription, defaultValues, theUIOrder);
    }

    /**
     * Constructor for BooleanMultiProperty that allows for multiple values.
     *
     * @param theName        String
     * @param theDescription String
     * @param defaultValues  Boolean[]
     * @param theUIOrder     float
     */
    public BooleanMultiProperty(String theName, String theDescription, List<Boolean> defaultValues, float theUIOrder) {
        super(theName, theDescription, defaultValues, theUIOrder);
    }

    @Override
    protected Boolean createFrom(String toParse) {
        return Boolean.valueOf(toParse);
    }


    @Override
    public Class<Boolean> type() {
        return Boolean.class;
    }


    @Override
    public List<Boolean> valueFrom(String propertyString) throws IllegalArgumentException {
        return null;
    }
}
