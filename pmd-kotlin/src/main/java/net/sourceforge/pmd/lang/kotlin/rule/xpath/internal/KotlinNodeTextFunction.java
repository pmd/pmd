/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;

/**
 * XPath function {@code pmd-kotlin:nodeText()}.
 *
 * <p>Returns the raw source text of the context node as a string.
 * Useful in XPath predicates that need to inspect literal argument values --
 * for example, checking whether a string argument is exactly one character,
 * or whether a numeric argument is {@code 0}, {@code 1}, or {@code 10}.
 *
 * <p>Uses {@link net.sourceforge.pmd.lang.document.TextDocument#sliceOriginalText}
 * to read the exact source text at the node's position.
 *
 * <p>Example -- flag {@code StringBuilder.append("x")} where the argument is a
 * single-character string literal (context node is the {@code ValueArgument}):
 * <pre>{@code
 * PostfixUnarySuffix/CallSuffix/ValueArguments/ValueArgument[
 *     string-length(pmd-kotlin:nodeText()) = 3
 *     and starts-with(pmd-kotlin:nodeText(), '"')
 *     and ends-with(pmd-kotlin:nodeText(), '"')
 * ]
 * }</pre>
 */
public final class KotlinNodeTextFunction extends BaseKotlinXPathFunction {

    public static final KotlinNodeTextFunction INSTANCE = new KotlinNodeTextFunction();

    private KotlinNodeTextFunction() {
        super("nodeText");
    }

    @Override
    public Type[] getArgumentTypes() {
        return new Type[0];
    }

    @Override
    public Type getResultType() {
        return Type.SINGLE_STRING;
    }

    @Override
    public boolean dependsOnContext() {
        return true;
    }

    @Override
    public FunctionCall makeCallExpression() {
        return new NodeTextFunctionCall();
    }

    private static final class NodeTextFunctionCall implements FunctionCall {
        @Override
        public Object call(@Nullable Node contextNode, Object[] arguments) throws XPathFunctionException {
            if (contextNode == null) {
                return "";
            }
            try {
                return contextNode.getTextDocument()
                        .sliceOriginalText(contextNode.getTextRegion())
                        .toString();
            } catch (Exception e) {
                return "";
            }
        }
    }
}
