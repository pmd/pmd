/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;
import java.util.Set;


/**
 * @author Clément Fournier
 * @since 6.7.0
 */
final class GenericMultiValuePropertyDescriptor<V> extends AbstractMultiValueProperty<V> {


    private final Set<PropertyValidator<List<V>>> listValidators;
    private final Set<PropertyValidator<V>> componentValidators;
    private final ValueParser<V> parser;
    private final Class<V> type;


    GenericMultiValuePropertyDescriptor(String name, String description, float uiOrder,
                                        List<V> defaultValue,
                                        Set<PropertyValidator<List<V>>> listValidators,
                                        Set<PropertyValidator<V>> componentValidators,
                                        ValueParser<V> parser,
                                        Class<V> type) {

        super(name, description, defaultValue, uiOrder, false);
        this.listValidators = listValidators;
        this.componentValidators = componentValidators;
        this.parser = parser;
        this.type = type;
    }


    @Override
    public String errorFor(List<V> values) {
        for (PropertyValidator<List<V>> lv : listValidators) {
            String error = lv.validate(values);
            if (error != null) {
                return error;
            }
        }

        for (PropertyValidator<V> cv : componentValidators) {
            for (V v : values) {
                String error = cv.validate(v);
                if (error != null) {
                    return error;
                }
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
