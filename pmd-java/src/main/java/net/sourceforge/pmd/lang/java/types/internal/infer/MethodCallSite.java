/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger.SimpleLogger;

/**
 * Poly site for an invocation expression. Includes info about an ongoing
 * outer inference process if any, as well as an expected target type.
 * The target type might depend on inference of the outer context, by
 * mentioning free type variables. In that case they're resolved together.
 */
public class MethodCallSite extends PolySite {

    private final InferenceContext localInferenceContext;

    private boolean logEnabled = true;
    private final Map<MethodResolutionPhase, List<ResolutionFailure>> errors = new EnumMap<>(MethodResolutionPhase.class);

    public MethodCallSite(InvocationMirror expr,
                          @Nullable JTypeMirror expectedType,
                          @NonNull InferenceContext infCtx) {
        super(expr, expectedType);
        assert infCtx != null : "Null inference context";

        this.localInferenceContext = infCtx;
    }

    private boolean areAllArgsRelevant = true;

    void acceptFailure(ResolutionFailure exception) {
        if (logEnabled) {
            errors.computeIfAbsent(exception.getPhase(), k -> new ArrayList<>()).add(exception);
        }
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    void setLogging(boolean enabled) {
        logEnabled = enabled;
    }

    /**
     * Returns a list of error messages encountered during the inference.
     * For this list to be populated, the {@link Infer} must use
     * a {@link SimpleLogger}.
     *
     * <p>Failures in the invocation phase are compile-time errors.
     */
    public Map<MethodResolutionPhase, List<ResolutionFailure>> getResolutionFailures() {
        return Collections.unmodifiableMap(errors);
    }

    void clearFailures() {
        errors.clear();
    }

    boolean areAllArgsRelevantToApplicability() {
        return areAllArgsRelevant;
    }

    void setSomeArgsAreNotPertinent() {
        areAllArgsRelevant = false;
    }

    /**
     * Returns the inference context of the target site. This is relevant
     * in invocation contexts, in which the inference context of an argument
     * needs to be propagated to the outer context.
     */
    @NonNull
    InferenceContext getInferenceContext() {
        return localInferenceContext;
    }

    @Override
    InvocationMirror getExpr() {
        return (InvocationMirror) super.getExpr();
    }

    @Override
    public String toString() {
        return "CallSite:" + getExpr();
    }
}
