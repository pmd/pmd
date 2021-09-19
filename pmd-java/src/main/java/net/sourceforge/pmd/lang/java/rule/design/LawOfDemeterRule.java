/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.Collection;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.AssignmentEntry;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.DataflowResult;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.ReachingDefinitionSet;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.OptionalBool;

/**
 * This rule can detect possible violations of the Law of Demeter. The Law of
 * Demeter is a simple rule, that says "only talk to friends". It helps to
 * reduce coupling between classes or objects.
 * <p>
 * See:
 * <ul>
 * <li>Andrew Hunt, David Thomas, and Ward Cunningham. The Pragmatic Programmer.
 * From Journeyman to Master. Addison-Wesley Longman, Amsterdam, October
 * 1999.</li>
 * <li>K.J. Lieberherr and I.M. Holland. Assuring good style for object-oriented
 * programs. Software, IEEE, 6(5):38–48, 1989.</li>
 * </ul>
 *
 * @since 5.0
 *
 */
public class LawOfDemeterRule extends AbstractJavaRulechainRule {

    private static final InvocationMatcher ITERATOR_NEXT = InvocationMatcher.parse("java.util.Iterator#next()");

    public LawOfDemeterRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {

        String reason = getViolationReason(node);
        if (reason != null) {
            addViolation(data, node, reason);
        }

        return null;
    }

    private @Nullable String getViolationReason(ASTMethodCall node) {
        ASTExpression qualifier = node.getQualifier();
        if (qualifier == null || isBuilderPattern(qualifier)) {
            return null;
        }
        if (qualifier instanceof ASTMethodCall) {
            return "method chaining";
        } else if (qualifier instanceof ASTNamedReferenceExpr) {
            if (isForeign((ASTNamedReferenceExpr) qualifier).isTrue()) {
                return "call on foreign value";
            }
        }
        return null;
    }

    private boolean isBuilderPattern(ASTExpression qualifier) {
        return qualifier.getTypeMirror() instanceof JClassType
            && qualifier.getTypeMirror().getSymbol().getSimpleName().endsWith("Builder");
    }

    private OptionalBool isForeign(ASTNamedReferenceExpr acc) {
        ReachingDefinitionSet reaching = DataflowPass.getDataflowResult(acc.getRoot()).getReachingDefinitions(acc);
        if (reaching.isNotFullyKnown()) {
            return OptionalBool.UNKNOWN;
        }

        return OptionalBool.definitely(CollectionUtil.any(reaching.getReaching(), this::isForeign));
    }

    private boolean isForeign(AssignmentEntry def) {
        if (def.isForeachVar()) {
            ASTForeachStatement foreach = def.getVarId().ancestors(ASTForeachStatement.class).firstOrThrow();
            return isForeign(foreach.getIterableExpr());
        }
        // formal parameters are not foreign otherwise we couldn't call any methods on them
        // if (def.getVarId().isFormalParameter()) {
        //     return true;
        // }
        return isForeign(def.getRhsAsExpression());
    }

    private boolean isForeign(@Nullable ASTExpression expr) {
        if (expr == null) {
            return false;
        }
        if (expr instanceof ASTMethodCall) {
            if (ITERATOR_NEXT.matchesCall(expr)
                || TypeTestUtil.isA(Collection.class, ((ASTMethodCall) expr).getQualifier())) {
                // an iterator.next() is as foreign as the qualifier
                // a list call is as foreign as the list
                // ie, pure data containers are "transparent"
                return isForeign(((ASTMethodCall) expr).getQualifier());
            }
            // static methods are taken to be construction methods.
            return !((ASTMethodCall) expr).getMethodType().isStatic();
        } else if (expr instanceof ASTNamedReferenceExpr) {
            DataflowResult dataflow = DataflowPass.getDataflowResult(expr.getRoot());
            ReachingDefinitionSet reaching = dataflow.getReachingDefinitions((ASTNamedReferenceExpr) expr);
            if (reaching.isNotFullyKnown()) {
                // fields of this instance are not foreign
                return !JavaRuleUtil.isRefToFieldOfThisInstance(expr);
            }

            return CollectionUtil.any(reaching.getReaching(), this::isForeign);
        }
        return false;
    }

}
