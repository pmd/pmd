/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.function.ToIntFunction;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionDefinition;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;

/**
 * A function that returns the current file name.
 *
 * @author Clément Fournier
 */
public final class CoordinateXPathFunction extends XPathFunctionDefinition {

    public static final CoordinateXPathFunction START_LINE =
        new CoordinateXPathFunction("startLine", Node::getBeginLine);
    public static final CoordinateXPathFunction END_LINE =
        new CoordinateXPathFunction("endLine", Node::getEndLine);
    public static final CoordinateXPathFunction START_COLUMN =
        new CoordinateXPathFunction("startColumn", Node::getBeginColumn);
    public static final CoordinateXPathFunction END_COLUMN =
        new CoordinateXPathFunction("endColumn", Node::getEndColumn);

    private static final Type[] A_SINGLE_ELEMENT = { Type.SINGLE_ELEMENT };
    public static final String PMD_NODE_USER_DATA = "pmd.node";
    private final ToIntFunction<Node> getter;

    private CoordinateXPathFunction(String localName, ToIntFunction<Node> getter) {
        super(localName);
        this.getter = getter;
    }

    @Override
    public Type[] getArgumentTypes() {
        return A_SINGLE_ELEMENT;
    }

    @Override
    public Type getResultType() {
        return Type.SINGLE_INTEGER;
    }

    @Override
    public FunctionCall makeCallExpression() {
        return (contextNode, arguments) -> {
            Node node = XPathElementToNodeHelper.itemToNode(arguments[0]);
            if (node == null) {
                throw new XPathFunctionException(
                    "Cannot call function '" + getQName().getLocalPart()
                        + "' on argument " + arguments[0]
                );
            }
            return getter.applyAsInt(node);
        };
    }
}
