/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.newframework;

import java.util.function.Function;

import org.apache.commons.lang3.EnumUtils;


/**
 * @author Clément Fournier
 * @since 6.7.0
 */
public final class PropertyFactory {

    private PropertyFactory() {

    }


    public static NumericPropertyBuilder<Integer> intProperty(String name) {
        return new NumericPropertyBuilder<>(name, Integer::valueOf, Integer.class);
    }

    // TODO need a way to document the possible values
    public static <T extends Enum<T>> GenericPBuilder<T> enumProperty(String name, Class<T> enumClass) {
        return new GenericPBuilder<>(name, s ->
                EnumUtils.getEnumList(enumClass).stream()
                         .filter(e -> e.name().equalsIgnoreCase(s))
                         .findFirst()
                         .orElseThrow(() -> new IllegalArgumentException("The name '" + s + "' doesn't correspond to any constant in the enum '" + enumClass.getName() + "'"))
                , enumClass);
    }


    // removes the other type parameter
    public static class GenericPBuilder<T> extends AbstractSingleValuePropertyBuilder<GenericPBuilder<T>, T> {

        GenericPBuilder(String name, Function<String, T> parser, Class<T> type) {
            super(name, parser, type);
        }
    }

    public static class NumericPropertyBuilder<N extends Number> extends AbstractSingleValuePropertyBuilder<NumericPropertyBuilder<N>, N> {

        NumericPropertyBuilder(String name, Function<String, N> parser, Class<N> type) {
            super(name, parser, type);
        }


        public NumericPropertyBuilder<N> requirePositive() {
            return addValidator(n -> n.intValue() > 0, "Expected a positive number");
        }
    }


}
