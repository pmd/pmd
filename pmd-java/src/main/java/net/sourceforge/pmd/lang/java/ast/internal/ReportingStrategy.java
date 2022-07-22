/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast.internal;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;

/**
 * Strategy for reporting language-feature violations, for use by a
 * {@link LanguageLevelChecker}. For example, {@link ReportingStrategy#reporterThatThrows()}
 * produces a checker that throws a parse exception.  It would be trivial
 * to make eg a checker that eg collects all warnings instead of failing
 * on the first one.
 *
 * @param <T> Type of object accumulating violations
 */
public interface ReportingStrategy<T> {

    /** Create a blank accumulator before performing the check. */
    T createAccumulator();


    /** Consume the accumulator, after all violations have been reported. */
    void done(T accumulator);


    /**
     * Report that a node violates a language feature. This doesn't have
     * to throw an exception, we could also just warn, or accumulate into
     * the parameter.
     */
    void report(Node node, String message, T acc);


    /**
     * Creates a reporter that throws a {@link ParseException} when the
     * first error is reported.
     */
    static ReportingStrategy<Void> reporterThatThrows() {
        return new ReportingStrategy<Void>() {
            @Override
            public Void createAccumulator() {
                return null;
            }

            @Override
            public void done(Void accumulator) {
                // do nothing
            }

            @Override
            public void report(Node node, String message, Void acc) {
                throw new ParseException(node.getReportLocation().startPosToString() + ": " + message);
            }
        };
    }

}
