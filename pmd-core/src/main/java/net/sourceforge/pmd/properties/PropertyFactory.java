/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Map;

import net.sourceforge.pmd.properties.PropertyBuilder.AbstractGenericMultiPropertyBuilder;
import net.sourceforge.pmd.properties.PropertyBuilder.GenericPropertyBuilder;
import net.sourceforge.pmd.properties.validators.PropertyValidator;
import net.sourceforge.pmd.properties.validators.ValidatorFactory;
import net.sourceforge.pmd.properties.validators.ValidatorFactory.Predicate;


/**
 * @author Clément Fournier
 * @since 6.7.0
 */
public final class PropertyFactory {

    private PropertyFactory() {

    }


    public static GenericPBuilder<Integer> intProperty(String name) {
        return new GenericPBuilder<>(name, ValueParserConstants.INTEGER_PARSER, Integer.class);
    }


    public static GenericMultiPBuilder<Integer> intListProperty(String name) {
        return new GenericMultiPBuilder<>(name, ValueParserConstants.INTEGER_PARSER, Integer.class);
    }


    public static GenericPBuilder<Double> doubleProperty(String name) {
        return new GenericPBuilder<>(name, ValueParserConstants.DOUBLE_PARSER, Double.class);
    }


    public static GenericMultiPBuilder<Double> doubleListProperty(String name) {
        return new GenericMultiPBuilder<>(name, ValueParserConstants.DOUBLE_PARSER, Double.class);
    }


    public static <T> GenericPBuilder<T> enumProperty(String name, Map<String, T> nameToValue) {
        return new GenericPBuilder<>(name, ValueParserConstants.enumerationParser(nameToValue))
                .addValidator(documentConstraint("Should be in the set " + nameToValue.keySet()));
    }


    // FIXME this is a workaround to document a constraint that occurs while parsing
    // With java 8 we could devise a better scheme
    private static <T> PropertyValidator<T> documentConstraint(String description) {
        return ValidatorFactory.fromPredicate(new Predicate<T>() {
            @Override
            public boolean test(T t) {
                return true;
            }
        }, description);
    }

    // removes the other setType parameter
    public static class GenericPBuilder<T> extends GenericPropertyBuilder<GenericPBuilder<T>, T> {

        @SuppressWarnings("unchecked")
        GenericPBuilder(String name, ValueParser<T> parser) {
            this(name, parser, (Class<T>) Object.class);
        }


        GenericPBuilder(String name, ValueParser<T> parser, Class<T> type) {
            super(name, parser, type);
        }


    }

    // removes the other setType parameter
    public static class GenericMultiPBuilder<T> extends AbstractGenericMultiPropertyBuilder<GenericMultiPBuilder<T>, T> {

        GenericMultiPBuilder(String name, ValueParser<T> parser, Class<T> type) {
            super(name, parser, type);
        }
    }

}
