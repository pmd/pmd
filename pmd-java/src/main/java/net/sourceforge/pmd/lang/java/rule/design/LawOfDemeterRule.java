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

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAccess;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
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
    public void end(RuleContext ctx) {
        degreeCache.clear();
    }

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

    @Override
    public Object visit(ASTMethodCall node, Object data) {
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
        return typeEndsWith(qualifier, "Builder");
    }

    private boolean isFactoryMethod(ASTMethodCall expr) {
        ASTExpression qualifier = expr.getQualifier();
        if (qualifier != null) { // NOPMD SimplifyBooleanReturns https://github.com/pmd/pmd/issues/3786
            return typeEndsWith(qualifier, "Factory")
                || nameEndsWith(qualifier, "Factory")
                || nameIs(qualifier, "factory");
        }
        return false;
    }

    private boolean nameEndsWith(ASTExpression expr, String suffix) {
        return expr instanceof ASTNamedReferenceExpr
            && ((ASTNamedReferenceExpr) expr).getName().endsWith(suffix);
    }

    private boolean nameIs(ASTExpression expr, String name) {
        return expr instanceof ASTNamedReferenceExpr
            && ((ASTNamedReferenceExpr) expr).getName().equals(name);
    }

    private boolean typeEndsWith(ASTExpression qualifier, String suffix) {
        return qualifier.getTypeMirror() instanceof JClassType
            && qualifier.getTypeMirror().getSymbol().getSimpleName().endsWith(suffix);
    }

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
            return methodForeignDegreeImpl((ASTMethodCall) expr);
        } else if (expr instanceof ASTNamedReferenceExpr) {
            if (expr instanceof ASTFieldAccess) {
                return fieldForeignDegreeImpl(expr);
            }
            // a variable access

            DataflowResult dataflow = DataflowPass.getDataflowResult(expr.getRoot());
            ReachingDefinitionSet reaching = dataflow.getReachingDefinitions((ASTNamedReferenceExpr) expr);
            if (reaching.isNotFullyKnown()) {
                return 0;
            }

            // note this max could be changed to min to get a more conservative
            // strategy, trading recall for precision.
            return reaching.getReaching().stream().mapToInt(this::foreignDegree).max().orElse(0);
        } else if (expr instanceof ASTArrayAccess) {
            return foreignDegree(((ASTArrayAccess) expr).getQualifier());
        }
        return 0;
    }

    private int fieldForeignDegreeImpl(ASTExpression expr) {
        if (isLocalFieldAccess((ASTFieldAccess) expr)) {
            return 0;
        } else if (JavaRuleUtil.isArrayLengthFieldAccess(expr)) {
            return foreignDegree(((ASTFieldAccess) expr).getQualifier());
        }
        return 1 + foreignDegree(((ASTFieldAccess) expr).getQualifier());
    }

    private int methodForeignDegreeImpl(ASTMethodCall expr) {
        if (isLocalMethod(expr)) {
            return 0;
        } else if (increasesDegree(expr)) {
            return 1 + foreignDegree(expr.getQualifier());
        }
        return foreignDegree(expr.getQualifier());
    }

    /**
     * Method that produces trusted data.
     */
    private boolean isLocalMethod(ASTMethodCall expr) {
        // static methods are taken to be construction methods.
        return expr.getMethodType().isStatic()
            || JavaRuleUtil.isCallOnThisInstance(expr)
            || isFactoryMethod(expr);
    }

    /**
     * Method that produces untrusted data.
     */
    private boolean increasesDegree(ASTMethodCall expr) {
        return isGetterLike(expr)
            && !isNeverForeignMethod(expr);
    }

    private boolean isGetterLike(ASTMethodCall expr) {
        return JavaRuleUtil.isGetterCall(expr)
            || expr.getArguments().isEmpty();
    }

    /**
     * Method that is as trusted as its receiver.
     */
    private boolean isNeverForeignMethod(@NonNull ASTMethodCall expr) {
        return ITERATOR_NEXT.matchesCall(expr)
            || TypeTestUtil.isA(Collection.class, expr.getQualifier())
            || TypeTestUtil.isA(StringBuilder.class, expr.getQualifier())
            || TypeTestUtil.isA(StringBuffer.class, expr.getQualifier())
            || isBuilderPattern(expr)
            || isFactoryMethod(expr);
    }


}
