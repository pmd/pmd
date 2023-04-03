/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.function.ToIntFunction;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.impl.AbstractXPathFunctionDef;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.Type;
import net.sf.saxon.value.Int64Value;
import net.sf.saxon.value.SequenceType;

/**
 * A function that returns the current file name.
 *
 * @author Clément Fournier
 */
public final class CoordinateXPathFunction extends AbstractXPathFunctionDef {

    public static final CoordinateXPathFunction START_LINE =
        new CoordinateXPathFunction("startLine", Node::getBeginLine);
    public static final CoordinateXPathFunction END_LINE =
        new CoordinateXPathFunction("endLine", Node::getEndLine);
    public static final CoordinateXPathFunction START_COLUMN =
        new CoordinateXPathFunction("startColumn", Node::getBeginColumn);
    public static final CoordinateXPathFunction END_COLUMN =
        new CoordinateXPathFunction("endColumn", Node::getEndColumn);

    private static final SequenceType[] A_SINGLE_ELEMENT = {
        NodeKindTest.makeNodeKindTest(Type.ELEMENT).one(),
    };
    public static final String PMD_NODE_USER_DATA = "pmd.node";
    private final ToIntFunction<Node> getter;

    private CoordinateXPathFunction(String localName, ToIntFunction<Node> getter) {
        super(localName);
        this.getter = getter;
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return A_SINGLE_ELEMENT;
    }

    @Override
    public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return SequenceType.SINGLE_INTEGER;
    }

    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {

            @Override
            public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                Node node = XPathElementToNodeHelper.itemToNode(arguments[0]);
                if (node == null) {
                    throw new XPathException(
                        "Cannot call function '" + getFunctionQName().getLocalPart()
                            + "' on argument " + arguments[0]
                    );
                }
                return Int64Value.makeIntegerValue(getter.applyAsInt(node));
            }
        };
    }


}
