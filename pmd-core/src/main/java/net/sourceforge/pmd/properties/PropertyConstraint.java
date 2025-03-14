/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;


/**
 * Validates the value of a property.
 *
 * @param <T> Type of value to handle
 *
 * @see PropertyBuilder#require(PropertyConstraint)
 *
 * @author Clément Fournier
 * @since 6.10.0
 */
public interface PropertyConstraint<T> {

    /**
     * Checks that the value conforms to this constraint. Throws if that
     * is not the case.
     *
     * @param value The value to validate
     *
     * @throws ConstraintViolatedException If this constraint is violated
     */
    void validate(T value);


    /**
     * Returns a description of the constraint
     * imposed by this validator on the values.
     * E.g. "Should be positive", or "Should be one of A | B | C."
     *
     * <p>This is used to generate documentation.
     *
     * @return A description of the constraint
     */
    String getConstraintDescription();

    /**
     * Serializes this constraint as XML attributes, that are
     * part of the property element of a rule definition.
     *
     * <p>Note: This is only used for constraints, which can be defined
     * in a rule definition in a ruleset (e.g. for XPath rules).</p>
     *
     * @return a map with attribute name and attribute value, suitable to be used in XML.
     *
     * @see net.sourceforge.pmd.util.internal.xml.SchemaConstants#PROPERTY_MIN
     * @see net.sourceforge.pmd.util.internal.xml.SchemaConstants#PROPERTY_MAX
     */
    default Map<String, String> getXmlConstraint() {
        return Collections.emptyMap();
    }

    /**
     * Returns a constraint that validates an {@code Optional<T>}
     * by checking that the value conforms to this constraint if
     * it is non-empty.
     */
    default PropertyConstraint<Optional<? extends T>> toOptionalConstraint() {
        return new PropertyConstraint<Optional<? extends T>>() {
            @Override
            public void validate(Optional<? extends T> value) {
                value.ifPresent(PropertyConstraint.this::validate);
            }

            @Override
            public String getConstraintDescription() {
                return PropertyConstraint.this.getConstraintDescription();
            }
        };
    }


    /**
     * Returns a constraint that validates a collection of Ts
     * by checking each component conforms to this validator.
     *
     * @return A collection validator
     */
    default PropertyConstraint<Iterable<? extends T>> toCollectionConstraint() {
        return new PropertyConstraint<Iterable<? extends T>>() {
            @Override
            public void validate(Iterable<? extends T> value) {
                for (T t : value) {
                    PropertyConstraint.this.validate(t);
                }
            }

            @Override
            public String getConstraintDescription() {
                return "Components " + StringUtils.uncapitalize(PropertyConstraint.this.getConstraintDescription());
            }
        };
    }


    /**
     * Builds a new validator from a predicate, and description.
     *
     * @param pred                  The predicate. If it returns
     *                              false on a value, then the
     *                              value is deemed to have a
     *                              problem
     * @param constraintDescription Description of the constraint,
     *                              see {@link PropertyConstraint#getConstraintDescription()}.
     * @param <U>                   Type of value to validate
     *
     * @return A new validator
     */
    static <U> PropertyConstraint<U> fromPredicate(final Predicate<? super U> pred, final String constraintDescription) {
        return fromPredicate(pred, constraintDescription, Collections.emptyMap());
    }

    /**
     * Builds a new constraint from a predicate, a description and xml attributes to serialize the
     * constraint.
     *
     * @see #fromPredicate(Predicate, String)
     */
    static <U> PropertyConstraint<U> fromPredicate(final Predicate<? super U> pred, final String constraintDescription,
                                                   final Map<String, String> xmlConstraint) {
        return new PropertyConstraint<U>() {

            @Override
            public void validate(U value) {
                if (!pred.test(value)) {
                    throw new ConstraintViolatedException(this, value);
                }
            }

            @Override
            public String getConstraintDescription() {
                return StringUtils.capitalize(constraintDescription);
            }

            @Override
            public String toString() {
                return "PropertyConstraint(" + constraintDescription + ")";
            }

            @Override
            public Map<String, String> getXmlConstraint() {
                return xmlConstraint;
            }
        };
    }

}
