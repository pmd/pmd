/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
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

    private final Map<ASTExpression, Integer> degreeCache = new LinkedHashMap<>();

    @Override
    public Object visit(ASTFieldAccess node, Object data) {
        if (isTooHighDegree(foreignDegree(node.getQualifier()))) {
            addViolationWithMessage(data, node, "Field access on foreign value");
        }
        return null;
    }

    private boolean isTooHighDegree(int degree) {
        return degree > 1;
    }


    private boolean isMethodCall(JavaNode node) {
        return node instanceof ASTMethodCall;
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        if (isMethodCall(node.getParent())) {
            // in a call chain, the outermost will be reported
            return null;
        }
        String reason = getViolationReason(node);
        if (reason != null) {
            addViolation(data, node, reason);
        }

        return null;
    }

    private @Nullable String getViolationReason(ASTMethodCall call) {
        ASTExpression qualifier = call.getQualifier();
        if (qualifier == null || isBuilderPattern(qualifier)) {
            return null;
        }
        if (isTooHighDegree(foreignDegree(call))) {
            if (isTooHighDegree(foreignDegree(call.getQualifier()))) {
                // qualifier will be reported
                return null;
            }

            return "call on foreign value";
        }
        return null;
    }

    private boolean isBuilderPattern(ASTExpression qualifier) {
        return qualifier.getTypeMirror() instanceof JClassType
            && qualifier.getTypeMirror().getSymbol().getSimpleName().endsWith("Builder");
    }
    //
    //    private int foreignDegree(ASTNamedReferenceExpr acc, RecursionGuard guard) {
    //        if (acc instanceof ASTFieldAccess) {
    //            // those are not tracked except if they're on this instance.
    //            // If they're not on this instance then they're foreign!
    //            return unless(isAllowedFieldAccess((ASTFieldAccess) acc));
    //        }
    //        ReachingDefinitionSet reaching = DataflowPass.getDataflowResult(acc.getRoot()).getReachingDefinitions(acc);
    //        if (reaching.isNotFullyKnown()) {
    //            return UNKNOWN;
    //        }
    //
    //        return definitely(CollectionUtil.any(reaching.getReaching(), it -> foreignDegree(it, guard)));
    //    }

    private boolean isLocalFieldAccess(ASTFieldAccess access) {
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


    private int foreignDegree(AssignmentEntry def) {

        if (def.isForeachVar()) {
            ASTForeachStatement foreach = def.getVarId().ancestors(ASTForeachStatement.class).firstOrThrow();
            // same degree as the list
            return foreignDegree(foreach.getIterableExpr());
        }
        // formal parameters are not foreign otherwise we couldn't call any methods on them
        if (def.getVarId().isFormalParameter()) {
            return 0;
        }
        return foreignDegree(def.getRhsAsExpression());
    }

    private int foreignDegree(@Nullable ASTExpression expr) {
        if (expr == null) {
            return 0;
        }
        Integer cachedValue = degreeCache.get(expr);
        if (cachedValue == null) {
            degreeCache.put(expr, -1); // recursion guard
            int computed = foreignDegreeImpl(expr);
            degreeCache.put(expr, computed);
            return computed;
        } else if (cachedValue == -1) {
            return 0; // recursion
        } else {
            return cachedValue;
        }
    }

    private int foreignDegreeImpl(ASTExpression expr) {
        if (expr instanceof ASTMethodCall) {
            if (isAsForeignAsQualifier((ASTMethodCall) expr)) {
                // an iterator.next() is as foreign as the qualifier
                // a list call is as foreign as the list
                // ie, pure data containers are "transparent"
                return foreignDegree(((ASTMethodCall) expr).getQualifier());
            }
            return 1 + foreignDegree(((ASTMethodCall) expr).getQualifier());
        } else if (expr instanceof ASTNamedReferenceExpr) {
            if (expr instanceof ASTFieldAccess) {
                if (isLocalFieldAccess((ASTFieldAccess) expr)) {
                    return 0;
                } else if (JavaRuleUtil.isArrayLengthFieldAccess(expr)) {
                    foreignDegree(((ASTFieldAccess) expr).getQualifier());
                }
                return 1 + foreignDegree(((ASTFieldAccess) expr).getQualifier());
            }
            // a variable access

            DataflowResult dataflow = DataflowPass.getDataflowResult(expr.getRoot());
            ReachingDefinitionSet reaching = dataflow.getReachingDefinitions((ASTNamedReferenceExpr) expr);
            if (reaching.isNotFullyKnown()) {
                return 0;
            }

            return reaching.getReaching().stream().mapToInt(this::foreignDegree).max().orElse(0);
        }
        return 0;
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
    private boolean isAsForeignAsQualifier(@NonNull ASTMethodCall expr) {
        return ITERATOR_NEXT.matchesCall(expr)
            || TypeTestUtil.isA(Collection.class, expr.getQualifier())
            || isBuilderPattern(expr);
    }


}
