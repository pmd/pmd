/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import java.util.ArrayDeque;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTSuperExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThisExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;

/**
 * A matcher for an expression like {@code a}, {@code a.b}, {@code a.getFoo()}.
 * Those are expressions we assume to be pure, and to be referring to
 * the same reference when they're called repeatedly if no side effect
 * occurs between calls.
 *
 * <p>Note that this is not relocatable: you must use a matcher in the
 * same scope it has been created in, to avoid bugs with accessibility/shadowing/etc.
 * You must take care yourself that no side-effect occurs.
 */
public final class StablePathMatcher {

    // if owner == null, then the owner is `this`.
    private final @Nullable JVariableSymbol owner;
    private final ArrayDeque<Segment> path;

    private StablePathMatcher(@Nullable JVariableSymbol owner, ArrayDeque<Segment> path) {
        this.owner = owner;
        this.path = path;
    }

    /**
     * Returns true if the expression matches the path.
     */
    public boolean matches(ASTExpression e) {
        if (e == null) {
            return false;
        }
        for (Segment segment : path) {
            boolean isField = segment.isField;
            String name = segment.name;
            if (isField) {
                if (!(e instanceof ASTFieldAccess)) {
                    return false;
                }
                ASTFieldAccess access = (ASTFieldAccess) e;
                if (!access.getName().equals(name)) {
                    return false;
                }
                e = access.getQualifier();
            } else {
                if (!(e instanceof ASTMethodCall)) {
                    return false;
                }

                ASTMethodCall call = (ASTMethodCall) e;
                if (!call.getMethodName().equals(name) || call.getArguments().size() != 0) {
                    return false;
                }
                e = call.getQualifier();
            }
        }


        if (e instanceof ASTVariableAccess) {
            return Objects.equals(((ASTVariableAccess) e).getReferencedSym(), owner);
        } else if (e instanceof ASTFieldAccess) {
            ASTFieldAccess fieldAccess = (ASTFieldAccess) e;
            if (!JavaRuleUtil.isUnqualifiedThis(fieldAccess.getQualifier())) {
                return false;
            }
            return Objects.equals(fieldAccess.getReferencedSym(), owner);
        }
        return false;
    }

    /**
     * Returns a matcher matching the given expression if it is stable.
     * Otherwise returns null.
     */
    public static @Nullable StablePathMatcher matching(ASTExpression e) {
        JVariableSymbol owner = null;
        ArrayDeque<Segment> segments = new ArrayDeque<>();

        while (e != null) {
            if (e instanceof ASTFieldAccess) {
                ASTFieldAccess access = (ASTFieldAccess) e;
                segments.addLast(new Segment(access.getName(), true));
                e = access.getQualifier();
            } else if (e instanceof ASTMethodCall) {
                ASTMethodCall call = (ASTMethodCall) e;
                if (JavaRuleUtil.isGetterCall(call)) {
                    segments.addLast(new Segment(call.getMethodName(), false));
                    e = call.getQualifier();
                } else {
                    return null;
                }
            } else if (e instanceof ASTVariableAccess) {
                owner = ((ASTVariableAccess) e).getReferencedSym();
                if (owner == null) {
                    return null; // unresolved
                }
                break;
            } else if (e instanceof ASTThisExpression) {
                if (((ASTThisExpression) e).getQualifier() != null) {
                    return null;
                }
                break;
            } else if (e instanceof ASTSuperExpression) {
                if (((ASTSuperExpression) e).getQualifier() != null) {
                    return null;
                }
                break;
            } else {
                return null;
            }
        }

        return new StablePathMatcher(owner, segments);
    }

    private static final class Segment {

        final String name;
        final boolean isField;

        Segment(String name, boolean isField) {
            this.name = name;
            this.isField = isField;
        }

        @Override
        public String toString() {
            return isField ? "." + name
                           : "." + name + "()";
        }
    }
}
