/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.newframework;

import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.properties.ValueParser;


/**
 * @author Clément Fournier
 * @since 6.7.0
 */
final class GenericPropertyDescriptor<T> extends AbstractGenericPropertyDescriptor<T> {


    private final ValueParser<T> parser;


    GenericPropertyDescriptor(String name,
                              String description,
                              float uiOrder,
                              T defaultValue,
                              Set<PropertyValidator<T>> validators,
                              ValueParser<T> parser,
                              Class<T> type) {
        super(name, description, uiOrder, defaultValue, validators, type);
        this.parser = parser;
    }


    @Override
    public boolean isMultiValue() {
        return false;
    }


    @Override
    public T valueFrom(List<String> valuesList) throws IllegalArgumentException {
        if (valuesList.size() != 1) {
            throw new IllegalArgumentException("This property can only handle a single value, but " + valuesList.size() + " was supplied");
        }

        return parser.valueOf(valuesList.get(0));
    }
}
