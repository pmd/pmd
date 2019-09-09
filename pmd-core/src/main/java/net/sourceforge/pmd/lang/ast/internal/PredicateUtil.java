/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;


import java.util.function.Predicate;

final class PredicateUtil {

    private static final Predicate TRUE = new Predicate() {
        @Override
        public boolean test(Object t) {
            return true;
        }

        @Override
        public Predicate and(Predicate other) {
            return other;
        }

        @Override
        public Predicate or(Predicate other) {
            return this;
        }
    };

    @SuppressWarnings("unchecked")
    static <T> Predicate<T> truePredicate() {
        return TRUE;
    }

}
