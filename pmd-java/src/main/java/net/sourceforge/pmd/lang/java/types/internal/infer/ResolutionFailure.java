/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.types.JMethodSig;

/**
 * An exception occurring during overload resolution. Some of those
 * are completely normal and prune incompatible overloads. There is
 * a compile-time error if no compile-time declaration is identified
 * though (all potentially applicable methods fail), or if after the
 * CTdecl is selected, its invocation fails (eg a param that was not
 * pertinent to applicability is incompatible with the declared formal).
 */
public class ResolutionFailure {

    static final ResolutionFailure UNKNOWN = new ResolutionFailure(null, "log is disabled");

    private JMethodSig failedMethod;
    private PolySite callSite;
    private MethodResolutionPhase phase;
    private final String reason;

    private @Nullable
    final JavaNode location;


    ResolutionFailure(@Nullable JavaNode location, String reason) {
        this.location = location;
        this.reason = reason;
    }

    void addContext(JMethodSig m, PolySite callSite, MethodResolutionPhase phase) {
        this.failedMethod = m;
        this.callSite = callSite;
        this.phase = phase;
    }

    /**
     * Returns the location on which the failure should be reported.
     */
    public @Nullable JavaNode getLocation() {
        return location != null ? location
                                : callSite != null ? callSite.getExpr().getLocation()
                                                   : null;
    }

    /**
     * Returns the method type that was being checked against the call site.
     */
    public JMethodSig getFailedMethod() {
        return failedMethod;
    }

    /**
     * Returns the phase in which the failure occurred. Failures in invocation
     * phase should be compile-time errors.
     */
    public MethodResolutionPhase getPhase() {
        return phase;
    }

    /** Returns the reason for the failure. */
    public String getReason() {
        return reason;
    }

    /** Returns the call site for the failure. */
    public PolySite getCallSite() {
        return callSite;
    }

    @Override
    public String toString() {
        return "ResolutionFailure{" +
            "failedMethod=" + failedMethod +
            ", callSite=" + callSite +
            ", phase=" + phase +
            ", reason='" + reason + '\'' +
            ", location=" + location +
            '}';
    }

}
