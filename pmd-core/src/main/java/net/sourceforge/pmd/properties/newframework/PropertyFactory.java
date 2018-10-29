/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.newframework;

import org.apache.commons.lang3.EnumUtils;

import net.sourceforge.pmd.properties.ValueParser;
import net.sourceforge.pmd.properties.newframework.AbstractPropertyBuilder.GenericPropertyBuilder;


/**
 * @author Clément Fournier
 * @since 6.7.0
 */
public final class PropertyFactory {

    private PropertyFactory() {

    }


    private static <T extends Enum<T>> T enumConstantFromEnum(Class<T> enumClass, String name) {
        return EnumUtils.getEnumList(enumClass).stream()
                        .filter(e -> e.name().equalsIgnoreCase(name))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("The name '" + name + "' doesn't correspond to any constant in the enum '" + enumClass.getName() + "'"));
    }


    public static NumericPropertyBuilder<Integer> intProperty(String name) {
        return new NumericPropertyBuilder<>(name, Integer::valueOf, Integer.class);
    }


    public static <T extends Enum<T>> GenericPBuilder<T> enumProperty(String name, Class<T> enumClass) {
        return new GenericPBuilder<>(name, s -> enumConstantFromEnum(enumClass, s), enumClass);
    }


    // removes the other type parameter
    public static class GenericPBuilder<T> extends GenericPropertyBuilder<GenericPBuilder<T>, T> {

        GenericPBuilder(String name, ValueParser<T> parser, Class<T> type) {
            super(name, parser, type);
        }
    }

    public static class NumericPropertyBuilder<N extends Number> extends GenericPropertyBuilder<NumericPropertyBuilder<N>, N> {

        NumericPropertyBuilder(String name, ValueParser<N> parser, Class<N> type) {
            super(name, parser, type);
        }


        // TODO rename
        public NumericPropertyBuilder<N> range(N min, N max) {
            return addValidator(Validators.rangeValidator(min, max));
        }
    }


}
