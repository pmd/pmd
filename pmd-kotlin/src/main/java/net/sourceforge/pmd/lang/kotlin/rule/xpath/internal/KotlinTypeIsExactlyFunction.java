/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;

/**
 * XPath function {@code pmd-kotlin:typeIsExactly(typeName)}.
 *
 * <p>Returns {@code true} when the context node's declared type (for property/variable
 * declarations) or return type (for function declarations) is <em>exactly</em> equivalent
 * to {@code typeName} -- no subtype checking is performed.
 * Both Java FQCNs and Kotlin FQNs are accepted (e.g. {@code java.lang.String} <->
 * {@code kotlin.String}).
 *
 * <p>Use {@code pmd-kotlin:typeIs(typeName)} when subtype matches should also be included.
 *
 * <p>Example XPath:
 * <pre>{@code
 * //PropertyDeclaration[pmd-kotlin:typeIsExactly('java.util.Calendar')]
 * //FunctionDeclaration[pmd-kotlin:typeIsExactly('kotlin.String')]
 * }</pre>
 */
public final class KotlinTypeIsExactlyFunction extends BaseKotlinXPathFunction {

    public static final KotlinTypeIsExactlyFunction INSTANCE = new KotlinTypeIsExactlyFunction();

    private KotlinTypeIsExactlyFunction() {
        super("typeIsExactly");
    }

    @Override
    public Type[] getArgumentTypes() {
        return new Type[]{Type.SINGLE_STRING};
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
        return new TypeIsExactlyFunctionCall();
    }

    private static final class TypeIsExactlyFunctionCall extends AbstractKotlinTypeIsFunctionCall {
        @Override
        protected boolean matchesNodeAttribute(KotlinNode node, String typeName, KotlinTypeAnalysisContext ctx) {
            String nodeType = node.getTypeName();
            if (nodeType != null) {
                return ctx.isTypeEquivalent(typeName, nodeType);
            }
            String returnType = node.getReturnTypeName();
            return returnType != null && ctx.isTypeEquivalent(typeName, returnType);
        }

        @Override
        protected boolean matchesType(String expectedType, String actualType, KotlinTypeAnalysisContext ctx) {
            return ctx.isTypeEquivalent(expectedType, actualType);
        }
    }
}
