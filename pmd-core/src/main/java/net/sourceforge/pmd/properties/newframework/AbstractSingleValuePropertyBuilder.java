/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.newframework;

import java.util.function.Function;


/**
 * @author Clément Fournier
 * @since 6.7.0
 */
public abstract class AbstractSingleValuePropertyBuilder<B extends AbstractSingleValuePropertyBuilder<B, T>, T> extends AbstractPropertyBuilder<B, T> {


    private final Function<String, T> parser;
    private final Class<T> type;

    private T defaultValue;


    AbstractSingleValuePropertyBuilder(String name, Function<String, T> parser, Class<T> type) {
        super(name);
        this.parser = parser;
        this.type = type;
    }


    @Override
    public PropertyDescriptor<T> build() {
        return new SingleValuePropertyDescriptor<>(
                name,
                description,
                uiOrder,
                defaultValue,
                validators,
                parser,
                type
        );
    }


    /**
     * Specify a default value.
     *
     * @param val Value
     *
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public B defaultValue(T val) {
        this.defaultValue = val;
        return (B) this;
    }


    /**
     * Returns the name of the property to be built.
     */
    @Override
    public String getName() {
        return name;
    }
}
