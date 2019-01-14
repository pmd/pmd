/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Map;

import net.sourceforge.pmd.properties.modules.NumericPropertyModule;


/**
 * Maintains a pair of boundary limit values between which all values managed by the subclasses must fit.
 *
 * @param <T> The type of value.
 *
 * @author Brian Remedios
 * @author Clément Fournier
 * @version Refactored June 2017 (6.0.0)
 */
@Deprecated
/* default */ abstract class AbstractNumericProperty<T extends Number> extends AbstractSingleValueProperty<T>
        implements NumericPropertyDescriptor<T> {


    private final NumericPropertyModule<T> module;


    /**
     * Constructor for a single-valued numeric property.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param lower          Minimum value of the property
     * @param upper          Maximum value of the property
     * @param theDefault     List of defaults
     * @param theUIOrder     UI order
     *
     * @throws IllegalArgumentException if lower > upper, or one of them is null, or the default is not between the
     *                                  bounds
     */
    protected AbstractNumericProperty(String theName, String theDescription, T lower, T upper, T theDefault,
                                      float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, theDefault, theUIOrder, isDefinedExternally);

        module = new NumericPropertyModule<>(lower, upper);
        if (theDefault == null) {
            return; // TODO: remove me when you scrap StatisticalRule (see pull #727)
        }
        module.checkNumber(theDefault);
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
