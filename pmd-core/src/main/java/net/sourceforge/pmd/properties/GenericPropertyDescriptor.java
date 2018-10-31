/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Set;

import net.sourceforge.pmd.properties.validators.PropertyValidator;


/**
 * @author Clément Fournier
 * @since 6.7.0
 */
final class GenericPropertyDescriptor<T> extends AbstractSingleValueProperty<T> {


    private final ValueParser<T> parser;
    private final Class<T> type;
    private final Set<PropertyValidator<? super T>> validators;


    GenericPropertyDescriptor(String name,
                              String description,
                              float uiOrder,
                              T defaultValue,
                              Set<PropertyValidator<? super T>> validators,
                              ValueParser<T> parser,
                              boolean isDefinedExternally,
                              Class<T> type) {

        super(name, description, defaultValue, uiOrder, isDefinedExternally);
        this.validators = validators;
        this.parser = parser;
        this.type = type;
    }


    @Override
    public String errorFor(T value) {
        for (PropertyValidator<? super T> validator : validators) {
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
