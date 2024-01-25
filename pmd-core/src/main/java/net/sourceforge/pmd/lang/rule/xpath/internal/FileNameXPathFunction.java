/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.Objects;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionDefinition;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.StringValue;

/**
 * A function that returns the current file name.
 *
 * @author Clément Fournier
 */
public final class FileNameXPathFunction extends XPathFunctionDefinition {

    public static final FileNameXPathFunction INSTANCE = new FileNameXPathFunction();

    private FileNameXPathFunction() {
        super("fileName");
    }

    @Override
    public Type getResultType(Type[] suppliedArgumentTypes) {
        return Type.STRING_SEQUENCE;
    }

    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {

            @Override
            public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                Node node = XPathElementToNodeHelper.itemToNode(context.getContextItem());
                if (node == null) {
                    throw new XPathException(
                        "Cannot call function '" + getQName().getLocalPart()
                            + "' with context item " + context.getContextItem()
                    );
                }
                RootNode root = node.getRoot();
                Objects.requireNonNull(root, "No root node in tree?");

                String fileName = root.getTextDocument().getFileId().getFileName();
                Objects.requireNonNull(fileName, "File name was not set");

                return new StringValue(fileName);
            }
        };
    }
}
