/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtCallSuffix;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassParameter;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassParameters;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtLambdaLiteral;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtPostfixUnaryExpression;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtPostfixUnarySuffix;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtPrimaryConstructor;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtSimpleIdentifier;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtValueArgument;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtValueArguments;
import net.sourceforge.pmd.lang.kotlin.ast.internal.KotlinAstUtil;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;

/**
 * XPath function {@code pmd-kotlin:hasVariableArgument()}.
 *
 * <p>Returns {@code true} when at least one argument of the call expression
 * (the context {@code PostfixUnaryExpression}) is a reference to a function
 * parameter, constructor parameter, or lambda parameter declared in an
 * enclosing scope.
 *
 * <p>Only the call's own direct arguments are examined — nested call expressions
 * inside those arguments are not considered. This prevents the false-positive
 * where an outer call (e.g. {@code LocalDate.parse(raw, ...)}) captures a
 * parameter used by a sibling argument.
 *
 * <p>Example usage in XPath:
 * <pre>{@code
 * //PostfixUnaryExpression[
 *     pmd-kotlin:matchesSig('java.time.format.DateTimeFormatter#ofPattern(*)')
 *     and not(pmd-kotlin:hasVariableArgument())
 * ]
 * }</pre>
 */
public final class KotlinHasVariableArgumentFunction extends BaseKotlinXPathFunction {

    public static final KotlinHasVariableArgumentFunction INSTANCE = new KotlinHasVariableArgumentFunction();

    private KotlinHasVariableArgumentFunction() {
        super("hasVariableArgument");
    }

    @Override
    public Type[] getArgumentTypes() {
        return new Type[0];
    }

    @Override
    public Type getResultType() {
        return Type.SINGLE_BOOLEAN;
    }

    @Override
    public boolean dependsOnContext() {
        return true;
    }

    @Override
    public FunctionCall makeCallExpression() {
        return new HasVariableArgumentFunctionCall();
    }

    private static final class HasVariableArgumentFunctionCall implements FunctionCall {
        @Override
        public Object call(@Nullable Node contextNode, Object[] arguments) throws XPathFunctionException {
            if (!(contextNode instanceof KtPostfixUnaryExpression)) {
                return false;
            }
            KtPostfixUnaryExpression postfixExpr = (KtPostfixUnaryExpression) contextNode;
            Set<String> paramNames = collectEnclosingParamNames(postfixExpr);
            if (paramNames.isEmpty()) {
                return false;
            }

            for (KtPostfixUnarySuffix suffix : postfixExpr.postfixUnarySuffix()) {
                KtCallSuffix callSuffix = suffix.callSuffix();
                if (callSuffix == null) {
                    continue;
                }
                KtValueArguments valueArguments = callSuffix.valueArguments();
                if (valueArguments == null) {
                    continue;
                }
                for (KtValueArgument arg : valueArguments.valueArgument()) {
                    if (argumentContainsParamRef(arg, paramNames)) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Collects parameter names from all enclosing function declarations, class
         * primary constructors, and lambda literals.
         */
        private static Set<String> collectEnclosingParamNames(KtPostfixUnaryExpression node) {
            Set<String> names = new HashSet<>();
            Node ancestor = node.getParent();
            while (ancestor != null) {
                if (ancestor instanceof KtFunctionDeclaration) {
                    names.addAll(KotlinAstUtil.collectParamNames((KtFunctionDeclaration) ancestor));
                } else if (ancestor instanceof KtClassDeclaration) {
                    names.addAll(collectConstructorParamNames((KtClassDeclaration) ancestor));
                } else if (ancestor instanceof KtLambdaLiteral) {
                    names.addAll(KotlinAstUtil.collectLambdaParamNames((KtLambdaLiteral) ancestor));
                }
                ancestor = ancestor.getParent();
            }
            return names;
        }

        /**
         * Collects primary constructor parameter names from a class declaration.
         */
        private static Set<String> collectConstructorParamNames(KtClassDeclaration classDecl) {
            Set<String> names = new HashSet<>();
            KtPrimaryConstructor ctor = classDecl.primaryConstructor();
            if (ctor == null) {
                return names;
            }
            KtClassParameters classParams = ctor.classParameters();
            if (classParams == null) {
                return names;
            }
            List<KtClassParameter> params = classParams.classParameter();
            for (KtClassParameter param : params) {
                String name = KotlinAstUtil.textOf(param.simpleIdentifier());
                if (name != null) {
                    names.add(name);
                }
            }
            return names;
        }

        /**
         * Returns {@code true} when any {@link KtSimpleIdentifier} descendant of
         * the argument has text matching one of the given parameter names.
         */
        private static boolean argumentContainsParamRef(KtValueArgument arg, Set<String> paramNames) {
            for (KtSimpleIdentifier simpleId : arg.descendants(KtSimpleIdentifier.class).toList()) {
                String text = KotlinAstUtil.textOf(simpleId);
                if (text != null && paramNames.contains(text)) {
                    return true;
                }
            }
            return false;
        }
    }
}
