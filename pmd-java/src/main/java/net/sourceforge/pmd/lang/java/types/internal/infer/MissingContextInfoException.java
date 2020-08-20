/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

/**
 * Thrown when some functional expression ({@link ExprMirror.FunctionalExprMirror})
 * has an inference variable as a target type, and this ivar does not
 * have enough bounds to be resolved to a functional interface type yet.
 *
 * <p>This should not prevent ctdecl resolution to proceed. The additional
 * bounds may be contributed by the invocation constraints of an enclosing
 * inference process.
 */
class MissingContextInfoException extends RuntimeException {

    static final MissingContextInfoException INSTANCE = new MissingContextInfoException();

    private MissingContextInfoException() {

    }

}
