/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.newframework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;


/**
 * @author Clément Fournier
 * @since 6.7.0
 */
public abstract class AbstractMultiValuePropertyBuilder<B extends AbstractPropertyBuilder<B, List<V>>, V> extends AbstractPropertyBuilder<B, List<V>> {
    private final Set<PropertyValidator<V>> componentValidators = new LinkedHashSet<>();
    private final Function<String, V> parser;
    private final Class<V> type;

    private List<V> defaultValues;


    AbstractMultiValuePropertyBuilder(String name, Function<String, V> parser, Class<V> type) {
        super(name);
        this.parser = parser;
        this.type = type;
    }


    /**
     * Specify a default value.
     *
     * @param val List of values
     *
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public B defaultValues(Collection<? extends V> val) {
        this.defaultValues = new ArrayList<>(val);
        return (B) this;
    }


    /**
     * Specify default values.
     *
     * @param val List of values
     *
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public B defaultValues(V... val) {
        this.defaultValues = Arrays.asList(val);
        return (B) this;
    }


    @Override
    public PropertyDescriptor<List<V>> build() {
        return new MultiValuePropertyDescriptor<>(
                name,
                description,
                uiOrder,
                defaultValues,
                validators,
                componentValidators,
                parser,
                type
        );
    }
}
