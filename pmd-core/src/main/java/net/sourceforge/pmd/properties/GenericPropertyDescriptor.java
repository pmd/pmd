/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.properties.constraints.PropertyConstraint;
import net.sourceforge.pmd.properties.internal.StringParser;


/**
 * Bound to be the single implementation for PropertyDescriptor in 7.0.0.
 *
 * @author Clément Fournier
 * @since 6.10.0
 */
final class GenericPropertyDescriptor<T> extends AbstractSingleValueProperty<T> {


    private final StringParser<T> parser;
    private final PropertyTypeId typeId;
    private final Class<T> type;
    private final Set<PropertyConstraint<? super T>> constraints;


    GenericPropertyDescriptor(String name,
                              String description,
                              float uiOrder,
                              T defaultValue,
                              Set<PropertyConstraint<? super T>> constraints,
                              StringParser<T> parser,
                              @Nullable PropertyTypeId typeId,
                              Class<T> type) {

        super(name, description, defaultValue, uiOrder, typeId != null);
        this.constraints = constraints;
        this.parser = parser;
        this.typeId = typeId;
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
