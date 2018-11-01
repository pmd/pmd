/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.properties.constraints.PropertyConstraint;


/**
 * If we implement schema changes to properties, delimiter logic will probably be scrapped,
 * and hence the divide between multi-value and single-value property descriptors. We can then
 * use a single class for all property descriptors.
 *
 * @author Clément Fournier
 * @since 6.10.0
 */
final class GenericMultiValuePropertyDescriptor<V> extends AbstractMultiValueProperty<V> {


    private final Set<PropertyConstraint<? super List<V>>> listValidators;
    private final ValueParser<V> parser;
    private final Class<V> type;


    GenericMultiValuePropertyDescriptor(String name, String description, float uiOrder,
                                        List<V> defaultValue,
                                        Set<PropertyConstraint<? super List<V>>> listValidators,
                                        ValueParser<V> parser,
                                        char delim,
                                        Class<V> type) {

        super(name, description, defaultValue, uiOrder, delim, false);
        this.listValidators = listValidators;
        this.parser = parser;
        this.type = type;
    }


    @Override
    public String errorFor(List<V> values) {
        for (PropertyConstraint<? super List<V>> lv : listValidators) {
            String error = lv.validate(values);
            if (error != null) {
                return error;
            }
        }
        return null;
    }


    @Override
    protected V createFrom(String toParse) {
        return parser.valueOf(toParse);
    }


    @Override
    public Class<V> type() {
        return type;
    }
}
