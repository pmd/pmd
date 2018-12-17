/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.properties.modules.NumericPropertyModule;


/**
 * Base class for multi-valued numeric properties.
 *
 * @param <T> The type of number
 *
 * @author Brian Remedios
 * @author Clément Fournier
 * @version Refactored June 2017 (6.0.0)
 */
@Deprecated
/* default */ abstract class AbstractMultiNumericProperty<T extends Number> extends AbstractMultiValueProperty<T>
    implements NumericPropertyDescriptor<List<T>> {

    private final NumericPropertyModule<T> module;


    /**
     * Constructor for a multi-valued numeric property using a list of defaults.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param lower          Minimum value of the property
     * @param upper          Maximum value of the property
     * @param theDefault     List of defaults
     * @param theUIOrder     UI order
     *
     * @throws IllegalArgumentException if lower > upper, or one of them is null, or one of the defaults is not between
     *                                  the bounds
     */
    AbstractMultiNumericProperty(String theName, String theDescription, T lower, T upper, List<T> theDefault,
                                 float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, theDefault, theUIOrder, isDefinedExternally);

        module = new NumericPropertyModule<>(lower, upper);
        for (T num : theDefault) {
            module.checkNumber(num);
        }
    }


    @Override
    protected String valueErrorFor(T value) {
        return module.valueErrorFor(value);
    }


    @Override
    public Number lowerLimit() {
        return module.getLowerLimit();
    }


    @Override
    public Number upperLimit() {
        return module.getUpperLimit();
    }


    @Override
    protected void addAttributesTo(Map<PropertyDescriptorField, String> attributes) {
        super.addAttributesTo(attributes);
        module.addAttributesTo(attributes);
    }

}
