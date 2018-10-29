/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.newframework;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author Clément Fournier
 * @since 6.7.0
 */
final class GenericMultiValuePropertyDescriptor<V> extends AbstractGenericPropertyDescriptor<List<V>> {


    private final Set<PropertyValidator<V>> componentValidators;
    private final Function<String, V> parser;


    GenericMultiValuePropertyDescriptor(String name, String description, float uiOrder,
                                        List<V> defaultValue,
                                        Set<PropertyValidator<List<V>>> listValidators,
                                        Set<PropertyValidator<V>> componentValidators,
                                        Function<String, V> parser,
                                        Class<V> type) {
        super(name, description, uiOrder, defaultValue, listValidators, type);
        this.componentValidators = componentValidators;
        this.parser = parser;
    }


    @Override
    public boolean isMultiValue() {
        return true;
    }


    @Override
    public List<V> getDefaultValue() {
        return Collections.unmodifiableList(defaultValue);
    }


    @Override
    public List<V> valueFrom(List<String> propertyString) throws IllegalArgumentException {
        return propertyString.stream().map(parser).collect(Collectors.toList());
    }


    @Override
    public List<String> getErrorMessagesFor(List<V> value) {
        List<String> listErrorMessages = super.getErrorMessagesFor(value);
        return listErrorMessages.isEmpty() ? listErrorMessages
                                           : componentValidators.stream()
                                                                .flatMap(validator -> value.stream().map(validator::validate))
                                                                .filter(Optional::isPresent)
                                                                .map(Optional::get)
                                                                .collect(Collectors.toList());
    }
}
