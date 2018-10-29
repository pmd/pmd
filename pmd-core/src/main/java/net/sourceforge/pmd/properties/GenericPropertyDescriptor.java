/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Set;


/**
 * @author Clément Fournier
 * @since 6.7.0
 */
final class GenericPropertyDescriptor<T> extends AbstractSingleValueProperty<T> {


    private final ValueParser<T> parser;
    private final Class<T> type;


    GenericPropertyDescriptor(String name,
                              String description,
                              float uiOrder,
                              T defaultValue,
                              Set<PropertyValidator<T>> validators,
                              ValueParser<T> parser,
                              Class<T> type,
                              boolean isDefinedExternally) {
        super(name, description, defaultValue, uiOrder, isDefinedExternally);
        this.parser = parser;
        this.type = type;
    }


    @Override
    protected T createFrom(String toParse) {
        return parser.valueOf(toParse);
    }


    @Override
    public Class<T> type() {
        return type;
    }
}
