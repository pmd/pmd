/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;

/**
 * XPath function {@code pmd-kotlin:typeIs(typeName)}.
 *
 * <p>Returns {@code true} when the context node's declared type is equivalent to, or a
 * <em>subtype</em> of, {@code typeName}. Both Java FQCNs and Kotlin FQNs are accepted.
 *
 * <p>Supported node types:
 * <ul>
 *   <li>{@code PropertyDeclaration} -- property / local variable type</li>
 *   <li>{@code ClassParameter} -- primary constructor {@code val}/{@code var} parameter type</li>
 *   <li>{@code FunctionDeclaration} -- return type</li>
 *   <li>{@code FunctionValueParameter} -- function / constructor parameter type</li>
 *   <li>{@code CatchBlock} -- caught exception type</li>
 *   <li>{@code ForStatement} -- loop variable type</li>
 * </ul>
 *
 * <p>Subtype checking uses the type hierarchy built by kotlin-type-mapper via reflection.
 * This requires the project's compiled classes (and their dependencies) to be on the
 * {@code auxClasspath}. Without classpath, the function falls back to exact-name matching.
 *
 * <p>For exact-type matching only, use {@code pmd-kotlin:typeIsExactly(typeName)}.
 *
 * <p>Example XPath:
 * <pre>{@code
 * //PropertyDeclaration[pmd-kotlin:typeIs('java.io.Serializable')]          -- matches subtypes too
 * //FunctionDeclaration[pmd-kotlin:typeIs('kotlin.String')]                  -- return type
 * //FunctionValueParameter[pmd-kotlin:typeIs('java.util.Map')]               -- parameter type
 * //CatchBlock[pmd-kotlin:typeIs('java.io.IOException')]                     -- exception type
 * //ForStatement[pmd-kotlin:typeIs('kotlin.String')]                         -- loop variable type
 * }</pre>
 */
public final class KotlinTypeIsFunction extends BaseKotlinXPathFunction {

    public static final KotlinTypeIsFunction INSTANCE = new KotlinTypeIsFunction();

    private KotlinTypeIsFunction() {
        super("typeIs");
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
        return new TypeIsFunctionCall();
    }

    private static final class TypeIsFunctionCall extends AbstractKotlinTypeIsFunctionCall {
        @Override
        protected boolean matchesNodeAttribute(KotlinNode node, String typeName, KotlinTypeAnalysisContext ctx) {
            String nodeType = node.getTypeName();
            if (nodeType != null) {
                return ctx.isSubtypeOf(typeName, nodeType);
            }
            String returnType = node.getReturnTypeName();
            return returnType != null && ctx.isSubtypeOf(typeName, returnType);
        }

        @Override
        protected boolean matchesType(String expectedType, String actualType, KotlinTypeAnalysisContext ctx) {
            return ctx.isSubtypeOf(expectedType, actualType);
        }
    }
}
