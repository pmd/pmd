/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

/**
 * Transitional class to make up for the absence of lambdas until we move to Java 8.
 *
 * @author Clément Fournier
 * @since 6.7.0
 */
final class Validators {

    private Validators() {

    }


    public static <T extends Number> PropertyValidator<T> rangeValidator(T min, T max) {
        return fromPredicate(new Predicate<T>() {
                                 @Override
                                 public boolean test(T t) {
                                     return min.doubleValue() < t.doubleValue() && max.doubleValue() > t.doubleValue();
                                 }
                             },
                             "Should be between " + min + " and " + max
        );
    }


    /**
     * Builds a new validator from a predicate,
     * and documentation messages.
     *
     * @param pred                  The predicate. If it returns
     *                              false on a value, then the
     *                              value is deemed to have a
     *                              problem and the failureMessage
     *                              will be transmitted.
     * @param constraintDescription Description of the constraint,
     *                              see {@link PropertyValidator#getConstraintDescription()}.
     * @param <U>                   Type of value to validate
     *
     * @return A new validator
     */
    private static <U> PropertyValidator<U> fromPredicate(Predicate<U> pred, String constraintDescription) {
        return new PropertyValidator<U>() {
            @Override
            public String validate(U value) {
                return pred.test(value) ? null : "Constraint violated on value '" + value + "' (" + constraintDescription + ")";
            }


            @Override
            public String getConstraintDescription() {
                return constraintDescription;
            }
        };
    }


    // Until we have Java 8
    interface Predicate<T> {
        boolean test(T t);
    }
}
