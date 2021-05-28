/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.Iterator;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.ReachingDefinitionSet;
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

        return OptionalBool.definitely(CollectionUtil.any(
            reaching.getReaching(),
            def -> def.getRhsAsExpression() instanceof ASTMethodCall
                && !TypeTestUtil.isA(Iterator.class, def.getRhsAsExpression())
                && !TypeTestUtil.isA(List.class, ((ASTMethodCall) def.getRhsAsExpression()).getQualifier())
        ));
    }
}
