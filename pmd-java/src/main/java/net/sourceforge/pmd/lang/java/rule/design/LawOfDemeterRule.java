/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.AssignmentEntry;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.DataflowResult;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.ReachingDefinitionSet;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
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

    private static final PropertyDescriptor<List<String>> ALLOWED_STATIC_CONTAINERS =
        PropertyFactory.stringListProperty("allowedStaticContainers")
                       .desc("List of binary names of types whose static fields are safe to access everywhere")
                       .defaultValue(listOf("java.lang.System"))
                       .build();

    public LawOfDemeterRule() {
        super(ASTMethodCall.class, ASTFieldAccess.class);
        definePropertyDescriptor(ALLOWED_STATIC_CONTAINERS);
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
            RecursionGuard guard = new RecursionGuard();
            if (isForeign((ASTNamedReferenceExpr) qualifier, guard).isTrue()) {
                return "call on foreign value";
            }
        }
        // the qualifier could be a TypeExpression, we don't count static
        // methods as foreign though
        return null;
    }

    private boolean isBuilderPattern(ASTExpression qualifier) {
        return qualifier.getTypeMirror() instanceof JClassType
            && qualifier.getTypeMirror().getSymbol().getSimpleName().endsWith("Builder");
    }

    private OptionalBool isForeign(ASTNamedReferenceExpr acc, RecursionGuard guard) {
        if (acc instanceof ASTFieldAccess) {
            // those are not tracked except if they're on this instance.
            // If they're not on this instance then they're foreign!
            return OptionalBool.unless(isAllowedFieldAccess((ASTFieldAccess) acc));
        }
        ReachingDefinitionSet reaching = DataflowPass.getDataflowResult(acc.getRoot()).getReachingDefinitions(acc);
        if (reaching.isNotFullyKnown()) {
            return OptionalBool.UNKNOWN;
        }

        return OptionalBool.definitely(CollectionUtil.any(reaching.getReaching(), it -> isForeign(it, guard)));
    }

    private boolean isAllowedFieldAccess(ASTFieldAccess access) {
        return JavaRuleUtil.isUnqualifiedThisOrSuper(access) // field of this instance
            || isAllowedStaticFieldAccess(access);
    }

    private boolean isAllowedStaticFieldAccess(ASTFieldAccess access) {
        JFieldSymbol sym = access.getReferencedSym();
        if (sym == null || !sym.isStatic()) {
            return false;
        }
        // the field is static

        String containerName = sym.getEnclosingClass().getBinaryName();
        return getProperty(ALLOWED_STATIC_CONTAINERS).contains(containerName);
    }


    private boolean isForeign(AssignmentEntry def, RecursionGuard guard) {
        if (!guard.explored.add(def.getVarId())) {
            // recursion!
            return false;
        }

        if (def.isForeachVar()) {
            ASTForeachStatement foreach = def.getVarId().ancestors(ASTForeachStatement.class).firstOrThrow();
            return isForeign(foreach.getIterableExpr(), guard);
        }
        // formal parameters are not foreign otherwise we couldn't call any methods on them
        // if (def.getVarId().isFormalParameter()) {
        //     return true;
        // }
        return isForeign(def.getRhsAsExpression(), guard);
    }

    private boolean isForeign(@Nullable ASTExpression expr, RecursionGuard guard) {
        if (expr == null) {
            return false;
        }
        if (expr instanceof ASTMethodCall) {
            if (isAsForeignAsQualifier(expr)) {
                // an iterator.next() is as foreign as the qualifier
                // a list call is as foreign as the list
                // ie, pure data containers are "transparent"
                return isForeign(((ASTMethodCall) expr).getQualifier(), guard);
            }
            return isDirectlyForeign((ASTMethodCall) expr);
        } else if (expr instanceof ASTNamedReferenceExpr) {
            DataflowResult dataflow = DataflowPass.getDataflowResult(expr.getRoot());
            ReachingDefinitionSet reaching = dataflow.getReachingDefinitions((ASTNamedReferenceExpr) expr);
            if (reaching.isNotFullyKnown()) {
                // fields of this instance are not foreign
                return !JavaRuleUtil.isRefToFieldOfThisInstance(expr);
            }

            return CollectionUtil.any(reaching.getReaching(), it -> isForeign(it, guard));
        }
        return false;
    }

    /**
     * Directly foreign methods "contaminate" other methods, while other
     * methods only transmit the infection.
     */
    private boolean isDirectlyForeign(ASTMethodCall expr) {
        // static methods are taken to be construction methods.
        return !expr.getMethodType().isStatic()
            && !JavaRuleUtil.isCallOnThisInstance(expr);
    }

    /**
     * Returns whether the infection can pass through the expression.
     * The expression is not directly foreign. These expressions should
     * be known useful on some types and not useful on others.
     */
    private boolean isAsForeignAsQualifier(@NonNull ASTExpression expr) {
        return ITERATOR_NEXT.matchesCall(expr)
            || TypeTestUtil.isA(Collection.class, ((ASTMethodCall) expr).getQualifier());
    }

    enum Foreignity {
        /** These are the ones we don't want to call. */
        FOREIGN,
        /** Forwards foreignity. */
        TRANSPARENT,
        /** Einheimisch. These ones are known good. */
        NATIVE
    }

    private static final class RecursionGuard {

        private final Set<ASTVariableDeclaratorId> explored = new HashSet<>();

    }

}
