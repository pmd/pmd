/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import static net.sourceforge.pmd.lang.java.types.internal.infer.ResolutionFailure.UNKNOWN;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.MethodRefMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.JInferenceVar.BoundKind;

/**
 * Carrier for {@link ResolutionFailure}. Throwing an exception is the
 * best way to abort resolution, but creating a new exception each time
 * is wasteful and unnecessary. There is one exception instance per thread
 * and the {@link ResolutionFailure} is set on it before it's thrown.
 */
final class ResolutionFailedException extends RuntimeException {

    private static final ThreadLocal<ResolutionFailedException> SHARED = ThreadLocal.withInitial(ResolutionFailedException::new);
    private static final boolean SHARE_EXCEPTION = true;

    private ResolutionFailure failure;

    private ResolutionFailedException() {

    }

    public ResolutionFailure getFailure() {
        return failure;
    }

    public void setFailure(ResolutionFailure location) {
        this.failure = location;
    }

    static ResolutionFailedException getSharedInstance() {
        return SHARED.get();
    }

    @Override
    public String toString() {
        return "ResolutionFailedException:failure=" + failure;
    }

    // If the logger is noop we don't even create the failure.
    // These failures are extremely frequent (and normal), and type pretty-printing is expensive

    static ResolutionFailedException incompatibleBound(TypeInferenceLogger logger, JInferenceVar ivar, BoundKind k1, JTypeMirror b1, BoundKind k2, JTypeMirror b2) {
        // in javac it's "no instance of type variables exist ..."
        return getShared(logger.isNoop() ? UNKNOWN : new ResolutionFailure(
            null,
            "Incompatible bounds: " + k1.format(ivar, b1) + " and " + k2.format(ivar, b2)
        ));
    }

    static ResolutionFailedException incompatibleBound(TypeInferenceLogger logger, JTypeMirror actual, JTypeMirror formal, JavaNode explicitTarg) {
        return getShared(logger.isNoop() ? UNKNOWN : new ResolutionFailure(
            explicitTarg,
            "Incompatible bounds: " + actual + " does not conform to " + formal
        ));
    }

    static ResolutionFailedException incompatibleTypeParamCount(TypeInferenceLogger logger, ExprMirror site, JMethodSig m, int found, int required) {
        return getShared(logger.isNoop() ? UNKNOWN : new ResolutionFailure(site.getLocation(), "Wrong number of type arguments"));
    }

    static ResolutionFailedException incompatibleFormal(TypeInferenceLogger logger, ExprMirror arg, JTypeMirror found, JTypeMirror required) {
        return getShared(logger.isNoop() ? UNKNOWN : new ResolutionFailure(
            // this constructor is pretty expensive due to the pretty printing when log is enabled
            arg.getLocation(),
            "Incompatible formals: " + found + " is not convertible to " + required
        ));
    }

    static ResolutionFailedException incompatibleReturn(TypeInferenceLogger logger, ExprMirror expr, JTypeMirror found, JTypeMirror required) {
        // in javac it's "no instance of type variables exist ..."
        return getShared(logger.isNoop() ? UNKNOWN : new ResolutionFailure(
            expr.getLocation(),
            "Incompatible return type: " + found + " is not convertible to " + required
        ));
    }

    static ResolutionFailedException unsolvableDependency(TypeInferenceLogger logger) {
        return getShared(logger.isNoop() ? UNKNOWN
                                         : new ResolutionFailure(null,
                                                                 "Unsolvable inference variable dependency"));
    }

    static ResolutionFailedException incompatibleArity(TypeInferenceLogger logger, int found, int required, JavaNode location) {
        return getShared(logger.isNoop() ? UNKNOWN
                                         : new ResolutionFailure(location,
                                                                 "Incompatible arity: " + found + " != " + required));
    }

    static ResolutionFailedException noCtDeclaration(TypeInferenceLogger logger, JMethodSig fun, MethodRefMirror mref) {
        return getShared(logger.isNoop() ? UNKNOWN
                                         : new ResolutionFailure(mref.getLocation(),
                                                                 "No compile time declaration found conforming to: " + fun));
    }


    @NonNull
    private static ResolutionFailedException getShared(ResolutionFailure failure) {
        ResolutionFailedException instance = SHARE_EXCEPTION ? getSharedInstance()
                                                             : new ResolutionFailedException();
        instance.setFailure(failure);
        return instance;
    }

}
