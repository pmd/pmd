/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.validators;


import net.sourceforge.pmd.properties.validators.ValidatorFactory.Predicate;


/**
 * Common validators for properties dealing with numbers.
 *
 * @author Clément Fournier
 * @since 6.7.0
 */
public class NumericValidators {

    private NumericValidators() {

    }


    public static <N extends Number> PropertyValidator<N> inRange(final N min, final N max) {
        return ValidatorFactory.fromPredicate(new Predicate<N>() {
                                                  @Override
                                                  public boolean test(N t) {
                                                      return min.doubleValue() < t.doubleValue() && max.doubleValue() > t.doubleValue();
                                                  }
                                              },
                                              "Should be between " + min + " and " + max
        );

    }


}
