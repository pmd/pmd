/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.ArrayList;
import java.util.Collection;
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
final class GenericMultiValuePropertyDescriptor<V, C extends Collection<V>> extends AbstractMultiValueProperty<V> {


    private final Set<PropertyConstraint<? super C>> listValidators;
    private final ValueParser<V> parser;
    private final Class<V> type;


    GenericMultiValuePropertyDescriptor(String name, String description, float uiOrder,
                                        Collection<V> defaultValue,
                                        Set<PropertyConstraint<? super C>> listValidators,
                                        ValueParser<V> parser,
                                        char delim,
                                        Class<V> type) {
        // this cast is safe until 7.0.0
        super(name, description, (List<V>) defaultValue, uiOrder, delim, false);
        this.listValidators = listValidators;
        this.parser = parser;
        this.type = type;

        String dftValueError = errorFor(new ArrayList<>(defaultValue));
        if (dftValueError != null) {
            throw new IllegalArgumentException(dftValueError);
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public String errorFor(List<V> values) {
        for (PropertyConstraint<? super C> lv : listValidators) {
            // Note: the unchecked cast is safe because pre-7.0.0,
            // we only allow building property descriptors for lists.
            // C is thus always List<V>, and the cast doesn't fail

            // Post-7.0.0, the multi-value property classes will be removed
            // and C will be the actual type parameter of the returned property
            // descriptor

            String error = lv.validate((C) values);
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
