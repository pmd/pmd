/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.ast.BinaryOp.INSTANCEOF;
import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.isArrayLengthFieldAccess;
import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.isCallOnThisInstance;
import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.isInfixExprWithOperator;
import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.isRefToFieldOfThisClass;
import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.isThisOrSuper;
import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.isGetterCall;
import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.isNullChecked;
import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAccess;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.QualifiableExpression;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.AssignmentEntry;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.DataflowResult;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.ReachingDefinitionSet;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeOps;
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
 * @author Clément Fournier
 * @since 5.0
 *
 */
public class LawOfDemeterRule extends AbstractJavaRule {


    private static final PropertyDescriptor<Integer> TRUST_RADIUS =
        PropertyFactory.intProperty("trustRadius")
                       .desc("Maximum degree of trusted data. The default of 1 is the most restrictive.")
                       .require(positive())
                       .defaultValue(1)
                       .build();
    private static final String FIELD_ACCESS_ON_FOREIGN_VALUE = "Access to field `{0}` on foreign value `{1}` (degree {2})";
    private static final String METHOD_CALL_ON_FOREIGN_VALUE = "Call to `{0}` on foreign value `{1}` (degree {2})";

    public LawOfDemeterRule() {
        definePropertyDescriptor(TRUST_RADIUS);
    }

    /**
     * This cache is there to prevent recursion in case of cycles. It
     * also avoids recomputing the degree of too many nodes, as the degree
     * of a call chain depends on the degree of the qualifier. {@link #visit(ASTMethodCall, Object)}
     * is called on every part of the chain, so without memoization we
     * would run in O(n2).
     */
    private final Map<ASTExpression, Integer> degreeCache = new LinkedHashMap<>();

    @Override
    public void apply(Node target, RuleContext ctx) {
        degreeCache.clear();
        // reimplement our own traversal instead of using the rulechain,
        // so that we have a stable traversal order.
        ((ASTCompilationUnit) target)
            .descendants().crossFindBoundaries()
            .forEach(it -> {
                if (it instanceof ASTMethodCall) {
                    this.visit((ASTMethodCall) it, ctx);
                } else if (it instanceof ASTFieldAccess) {
                    this.visit((ASTFieldAccess) it, ctx);
                }
            });
        degreeCache.clear(); // avoid memory leak
    }

    /**
     * Only report the first occurrences of a breach of trust. Those are
     * the ones that need to be fixed.
     */
    private boolean isReportedDegree(int degree) {
        return degree == getProperty(TRUST_RADIUS) + 1;
    }

    @Override
    public Object visit(ASTFieldAccess node, Object data) {
        if (shouldReport(node)) {
            addViolationWithMessage(
                data, node,
                FIELD_ACCESS_ON_FOREIGN_VALUE,
                new Object[] {
                    node.getName(),
                    PrettyPrintingUtil.prettyPrint(node.getQualifier()),
                    foreignDegree(node.getQualifier()),
                });
        }
        return null;
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        if (shouldReport(node)) {
            addViolationWithMessage(
                data, node,
                METHOD_CALL_ON_FOREIGN_VALUE,
                new Object[] {
                    node.getMethodName(),
                    PrettyPrintingUtil.prettyPrint(node.getQualifier()),
                    foreignDegree(node.getQualifier()),
                });
        }
        return null;
    }

    private boolean shouldReport(QualifiableExpression expr) {
        ASTExpression qualifier = expr.getQualifier();
        if (qualifier == null) {
            return false;
        }
        int degree = foreignDegree(expr);
        if (isReportedDegree(degree) && isUsedAsGetter(expr)) {
            if (expr.getParent() instanceof ASTVariableDeclarator) { // NOPMD #3786
                // Stored in local var, don't report if some usages escape.
                // In that case, usage sites with non-escaping usage will be reported.
                return isAllowedStore(((ASTVariableDeclarator) expr.getParent()).getVarId());
            } else {
                return true;
            }
        }
        // Reported degree may be higher if LHS is a local var with the reported degree.
        // If some usages of that local escape, the local hasn't been reported. Those usages
        // that don't escape need to be reported.
        if (qualifier instanceof ASTVariableAccess
            && isReportedDegree(foreignDegree(qualifier))) {
            JVariableSymbol sym = ((ASTVariableAccess) qualifier).getReferencedSym();
            return sym != null && !isAllowedStore(sym.tryGetNode());
        }
        return false;
    }

    private boolean isAllowedStore(ASTVariableDeclaratorId varId) {
        return varId != null && varId.getLocalUsages().stream().noneMatch(this::escapesMethod);
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
            // System.out.println("Degree " + computed + ": " + expr);
            return computed;
        } else if (cachedValue == -1) {
            return cachedValue; // recursion
        } else {
            return cachedValue;
        }
    }

    private int foreignDegreeImpl(ASTExpression expr) {
        if (expr instanceof ASTMethodCall) {
            ASTMethodCall call = (ASTMethodCall) expr;
            return methodCallDegree(call);
        } else if (expr instanceof ASTFieldAccess) {
            ASTFieldAccess access = (ASTFieldAccess) expr;
            return fieldAccessDegree(access);
        } else if (expr instanceof ASTVariableAccess) {
            ASTVariableAccess access = (ASTVariableAccess) expr;
            return variableDegree(access);
        } else if (expr instanceof ASTArrayAccess) {
            return foreignDegree(((ASTArrayAccess) expr).getQualifier());
        } else if (expr instanceof ASTConstructorCall) {
            return ACCESSIBLE;
        } else if (expr instanceof ASTTypeExpression || isThisOrSuper(expr)) {
            return TRUSTED;
        }
        return ACCESSIBLE;
    }



    private int methodCallDegree(ASTMethodCall call) {
        if (call.getOverloadSelectionInfo().isFailed() // be conservative
            || call.getMethodType().isStatic() // static methods are taken to be construction methods.
            || isCallOnThisInstance(call)
            || call.getQualifier() == null // either static or call on this. Prevents NPE when unresolved
            || isFactoryMethod(call)
            || isBuilderPattern(call.getQualifier())
            || isPureData(call)) {
            return ACCESSIBLE;
        } else if (isPureDataContainer(call.getMethodType().getDeclaringType())
            || isPureDataContainer(call.getTypeMirror())
            || !isGetterCall(call)
            || isTransformationMethod(call)) {
            return asForeignAsQualifier(call);
        }
        return moreForeignThanQualifier(call);
    }

    private boolean isTransformationMethod(ASTMethodCall expr) {
        if (expr.getQualifier() == null) {
            return false;
        }
        JTypeMirror qualType = expr.getQualifier().getTypeMirror();
        JTypeMirror returnType = expr.getTypeMirror();
        // note: there is a possible optimization that only tests
        // if types are related when their names are similar, look into history.
        return TypeOps.areRelated(qualType, returnType);
    }

    private boolean isPureData(ASTExpression expr) {
        return TypeTestUtil.isA(String.class, expr)
            || TypeTestUtil.isA(StringBuilder.class, expr)
            || TypeTestUtil.isA(StringBuffer.class, expr)
            || expr.getTypeMirror().isPrimitive()
            || expr.getTypeMirror().isBoxedPrimitive()
            || isNullChecked(expr)
            || isInfixExprWithOperator(expr.getParent(), INSTANCEOF);
    }

    private boolean isPureDataContainer(JTypeMirror type) {
        JTypeDeclSymbol symbol = type.getSymbol();
        if (symbol instanceof JClassSymbol) { // NOPMD
            return "java.util".equals(symbol.getPackageName()) // collection, map, iterator, properties, etc
                || TypeTestUtil.isA(Stream.class, type)
                || TypeTestUtil.isA(Class.class, type)
                || TypeTestUtil.isA(org.w3c.dom.NodeList.class, type)
                || TypeTestUtil.isA(org.w3c.dom.NamedNodeMap.class, type)
                || type.isArray();
        }
        return false;
    }


    private boolean escapesMethod(ASTExpression expr) {
        return expr.getParent() instanceof ASTArgumentList
            || expr.getParent() instanceof ASTReturnStatement
            || expr.getParent() instanceof ASTThrowStatement;
    }

    private boolean isUsedAsGetter(ASTExpression expr) {
        return !escapesMethod(expr) && !(expr.getParent() instanceof ASTExpressionStatement);
    }


    private int variableDegree(ASTVariableAccess expr) {
        DataflowResult dataflow = DataflowPass.getDataflowResult(expr.getRoot());
        ReachingDefinitionSet reaching = dataflow.getReachingDefinitions(expr);
        if (reaching.isNotFullyKnown()) {
            // a field symbol, normally
            return expr.getReferencedSym() instanceof JFieldSymbol
                   ? fieldAccessDegree(expr)
                   : TRUSTED; // unresolved, or failure in data flow pass
        }

        // note this max could be changed to min to get a more conservative
        // strategy, trading recall for precision. maybe make that configurable
        return reaching.getReaching().stream().mapToInt(this::foreignDegree).max().orElse(TRUSTED);
    }

    private int fieldAccessDegree(ASTNamedReferenceExpr expr) {
        if (isRefToFieldOfThisClass(expr) || isPureData(expr)) {
            return ACCESSIBLE;
        } else if (isArrayLengthFieldAccess(expr)) {
            return asForeignAsQualifier((ASTFieldAccess) expr);
        } else if (expr instanceof ASTFieldAccess) {
            return moreForeignThanQualifier((ASTFieldAccess) expr);
        } else {
            return ACCESSIBLE;
        }
    }


    private int foreignDegree(AssignmentEntry def) {

        if (def.isForeachVar()) {
            ASTForeachStatement foreach = def.getVarId().ancestors(ASTForeachStatement.class).firstOrThrow();
            // same degree as the list
            return foreignDegree(foreach.getIterableExpr());
        }
        // formal parameters are not foreign otherwise we couldn't call any methods on them
        if (def.getVarId().isFormalParameter()) {
            return 1;
        }
        return foreignDegree(def.getRhsAsExpression());
    }


    private boolean isBuilderPattern(ASTExpression expr) {
        return typeEndsWith(expr, "Builder");
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

    private boolean typeEndsWith(ASTExpression expr, String suffix) {
        return expr != null
            && expr.getTypeMirror() instanceof JClassType
            && expr.getTypeMirror().getSymbol().getSimpleName().endsWith(suffix);
    }

    /**
     * Degree 0.
     * <ul>
     * <li>`this`
     * </ul>
     * You can use the object however you like.
     */
    private static final int TRUSTED = 0;

    /**
     * Degree 1.
     * <ul>
     * <li>Fields of this class, but not of `this` instance (we need to
     * access their fields to write equals, compareTo, etc.)
     * <li>Method parameters.
     * <li>Result of construction methods (including factories, builders,
     * ctors, etc).
     * </ul>
     * You can use any method, but you can't use yourself the result of
     * a getter, or field (though you can let it escape).
     */
    private static final int ACCESSIBLE = 1;

    private int asForeignAsQualifier(QualifiableExpression e) {
        return foreignDegree(Objects.requireNonNull(e.getQualifier()));
    }

    private int moreForeignThanQualifier(QualifiableExpression e) {
        return 1 + foreignDegree(Objects.requireNonNull(e.getQualifier()));
    }

}
