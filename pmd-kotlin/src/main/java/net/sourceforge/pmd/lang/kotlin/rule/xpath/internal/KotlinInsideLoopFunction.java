/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;

/**
 * XPath function {@code pmd-kotlin:insideLoop()}.
 *
 * <p>Returns {@code true} when the context node is nested (at any depth) inside a
 * {@code ForStatement}, {@code WhileStatement}, or {@code DoWhileStatement}.
 *
 * <p>Replaces the verbose three-way {@code ancestor::} check in loop rules:
 * <pre>{@code
 * // Before
 * ancestor::ForStatement or ancestor::WhileStatement or ancestor::DoWhileStatement
 *
 * // After
 * pmd-kotlin:insideLoop()
 * }</pre>
 *
 * <p>Example -- flag a property declaration of any type created inside a loop:
 * <pre>{@code
 * //PropertyDeclaration[pmd-kotlin:insideLoop()]
 * }</pre>
 */
public final class KotlinInsideLoopFunction extends BaseKotlinXPathFunction {

    public static final KotlinInsideLoopFunction INSTANCE = new KotlinInsideLoopFunction();

    private KotlinInsideLoopFunction() {
        super("insideLoop");
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
        return new InsideLoopFunctionCall();
    }

    private static final class InsideLoopFunctionCall implements FunctionCall {
        @Override
        public Object call(@Nullable Node contextNode, Object[] arguments) throws XPathFunctionException {
            if (contextNode == null) {
                return false;
            }
            Node ancestor = contextNode.getParent();
            while (ancestor != null) {
                String name = ancestor.getXPathNodeName();
                if ("ForStatement".equals(name)
                        || "WhileStatement".equals(name)
                        || "DoWhileStatement".equals(name)) {
                    return true;
                }
                ancestor = ancestor.getParent();
            }
            return false;
        }
    }
}
