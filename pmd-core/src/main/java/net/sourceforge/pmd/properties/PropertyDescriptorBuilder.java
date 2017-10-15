/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;


/**
 * Base class for property builders.
 *
 * @param <E> Value type of the built descriptor
 * @param <T> Concrete type of this builder instance. Removes code duplication at the expense of a few unchecked casts.
 *            Everything goes well if this parameter's value is correctly set.
 * @author Clément Fournier
 * @since 6.0.0
 */
public abstract class PropertyDescriptorBuilder<E, T extends PropertyDescriptorBuilder<E, T>> {

    protected String name;
    protected String description;
    protected float uiOrder = 0f;


    /**
     * Specify the name of the property.
     *
     * @param name The name
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    T name(String name) {
        this.name = name;
        return (T) this;
    }


    /**
     * Specify the description of the property.
     *
     * @param desc The description
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public T desc(String desc) {
        this.description = description;
        return (T) this;
    }


    /**
     * Specify the UI order of the property.
     *
     * @param f The UI order
     * @return The same builder
     */
    @SuppressWarnings("unchecked")
    public T uiOrder(float f) {
        this.uiOrder = f;
        return (T) this;
    }


    /**
     * Builds the descriptor and returns it.
     *
     * @return The built descriptor
     */
    public abstract PropertyDescriptor<E> build();


    /**
     * For multi-value properties.
     *
     * @param <V> Element type of the list
     * @param <T> Concrete type of the underlying builder
     */
    public abstract static class MultiValue<V, T extends MultiValue<V, T>>
            extends PropertyDescriptorBuilder<List<V>, T> {

        protected List<V> defaultValues;
        protected char multiValueDelimiter = MultiValuePropertyDescriptor.DEFAULT_DELIMITER;


        /**
         * Specify a default value.
         *
         * @param val List of values
         * @return The same builder
         */
        @SuppressWarnings("unchecked")
        public T deft(List<V> val) {
            this.defaultValues = val;
            return (T) this;
        }


        /**
         * Specify a delimiter character. By default it's {@link MultiValuePropertyDescriptor#DEFAULT_DELIMITER}, or
         * {@link MultiValuePropertyDescriptor#DEFAULT_NUMERIC_DELIMITER} for numeric properties.
         *
         * @param delim Delimiter
         * @return The same builder
         */
        @SuppressWarnings("unchecked")
        public T delim(char delim) {
            this.multiValueDelimiter = delim;
            return (T) this;
        }


        /**
         * For multi-value numeric properties.
         *
         * @param <V> Element type of the list
         * @param <T> Concrete type of the underlying builder
         */
        public abstract static class Numeric<V, T extends Numeric<V, T>>
                extends MultiValue<V, T> {


            protected V lowerLimit;
            protected V upperLimit;


            protected Numeric() {
                multiValueDelimiter = MultiValuePropertyDescriptor.DEFAULT_NUMERIC_DELIMITER;
            }


            /**
             * Specify a minimum value.
             *
             * @param val Value
             * @return The same builder
             */
            @SuppressWarnings("unchecked")
            public T min(V val) {
                this.lowerLimit = val;
                return (T) this;
            }


            /**
             * Specify a maximum value.
             *
             * @param val Value
             * @return The same builder
             */
            @SuppressWarnings("unchecked")
            public T max(V val) {
                this.upperLimit = val;
                return (T) this;
            }

        }

    }


    /**
     * For single-value property descriptors.
     *
     * @param <E> Value type of the built descriptor
     * @param <T> Concrete type of this builder instance.
     */
    public abstract static class SingleValue<E, T extends SingleValue<E, T>>
            extends PropertyDescriptorBuilder<E, T> {

        protected E defaultValue;


        /**
         * Specify a default value.
         *
         * @param val Value
         * @return The same builder
         */
        @SuppressWarnings("unchecked")
        public T deft(E val) {
            this.defaultValue = val;
            return (T) this;
        }


    }


}
