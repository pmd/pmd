/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Set;

import net.sourceforge.pmd.properties.constraints.PropertyConstraint;


/**
 * Bound to be the single implementation for PropertyDescriptor in 7.0.0.
 *
 * @author Clément Fournier
 * @since 6.10.0
 */
final class GenericPropertyDescriptor<T> extends AbstractSingleValueProperty<T> {


    private final ValueParser<T> parser;
    private final Class<T> type;
    private final Set<PropertyConstraint<? super T>> constraints;


    GenericPropertyDescriptor(String name,
                              String description,
                              float uiOrder,
                              T defaultValue,
                              Set<PropertyConstraint<? super T>> constraints,
                              ValueParser<T> parser,
                              boolean isDefinedExternally,
                              Class<T> type) {

        super(name, description, defaultValue, uiOrder, isDefinedExternally);
        this.constraints = constraints;
        this.parser = parser;
        this.type = type;

        String dftValueError = errorFor(defaultValue);
        if (dftValueError != null) {
            throw new IllegalArgumentException(dftValueError);
        }
    }


    @Override
    public String errorFor(T value) {
        for (PropertyConstraint<? super T> validator : constraints) {
            String error = validator.validate(value);
            if (error != null) {
                return error;
            }

        }
        return null;
    }


    @Override
    public Class<T> type() {
        return type;
    }


    @Override
    protected T createFrom(String toParse) {
        return parser.valueOf(toParse);
    }
}
