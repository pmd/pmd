/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;


import static net.sourceforge.pmd.properties.ValueParserConstants.CHARACTER_PARSER;

import java.util.List;
import java.util.Map;


/**
 * Wraps a property builder and converts its inputs from strings to the target types of the descriptor.
 *
 * @param <E> Value type of the descriptor
 * @param <T> Concrete type of the underlying builder
 * @author Clément Fournier
 * @since 6.0.0
 */
public abstract class PropertyBuilderConversionWrapper<E, T extends PropertyDescriptorBuilder<E, T>> {


    protected final Map<PropertyDescriptorField, String> fields;
    protected final T underlying;


    protected PropertyBuilderConversionWrapper(Map<PropertyDescriptorField, String> fields, T builder) {
        this.fields = fields;
        this.underlying = builder;
    }


    /** Populates the builder with extracted fields. To be overridden. */
    protected void populateBuilder() {
        underlying.desc(fields.get(PropertyDescriptorField.DESCRIPTION));
        underlying.uiOrder(Float.parseFloat(fields.get(PropertyDescriptorField.UI_ORDER)));
    }


    public PropertyDescriptor<E> build() {
        return underlying.build();
    }


    /**
     * For multi-value properties.
     *
     * @param <V> Element type of the list
     * @param <T> Concrete type of the underlying builder
     */
    public static class MultiValue<V, T extends PropertyDescriptorBuilder.MultiValue<V, T>>
            extends PropertyBuilderConversionWrapper<List<V>, T> {

        protected final ValueParser<V> parser;


        protected MultiValue(Map<PropertyDescriptorField, String> fields,
                             ValueParser<V> parser, T builder) {
            super(fields, builder);
            this.parser = parser;
        }


        @Override
        protected void populateBuilder() {
            super.populateBuilder();
            char delim = CHARACTER_PARSER.valueOf(fields.get(PropertyDescriptorField.DELIMITER));
            underlying.delim(delim);
            underlying.deft(ValueParserConstants.multi(parser, delim)
                                                .valueOf(fields.get(PropertyDescriptorField.DEFAULT_VALUE)));
        }


        /**
         * For multi-value numeric properties.
         *
         * @param <V> Element type of the list
         * @param <T> Concrete type of the underlying builder
         */
        public static class Numeric<V, T extends PropertyDescriptorBuilder.MultiValue.Numeric<V, T>>
                extends MultiValue<V, T> {

            protected Numeric(Map<PropertyDescriptorField, String> fields, ValueParser<V> parser, T builder) {
                super(fields, parser, builder);
            }


            @Override
            protected void populateBuilder() {
                super.populateBuilder();
                underlying.min(parser.valueOf(fields.get(PropertyDescriptorField.MIN)));
                underlying.max(parser.valueOf(fields.get(PropertyDescriptorField.MAX)));
            }
        }

    }
}
