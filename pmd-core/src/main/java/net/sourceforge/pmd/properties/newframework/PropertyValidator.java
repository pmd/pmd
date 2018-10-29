/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.newframework;

import java.util.Optional;
import java.util.function.Predicate;


/**
 * @author Clément Fournier
 * @since 6.7.0
 */
@FunctionalInterface
public interface PropertyValidator<T> {

    Optional<String> validate(T value);


    static <U> PropertyValidator<U> fromPredicate(Predicate<U> pred, String failureMessage) {
        return u -> pred.test(u) ? Optional.empty() : Optional.of(failureMessage);
    }


}
