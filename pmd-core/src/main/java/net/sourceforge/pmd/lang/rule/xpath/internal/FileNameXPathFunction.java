/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.nio.file.Paths;
import java.util.Objects;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.xpath.impl.AbstractXPathFunctionDef;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

/**
 * A function that returns the current file name.
 *
 * @author Clément Fournier
 */
public final class FileNameXPathFunction extends AbstractXPathFunctionDef {

    public static final FileNameXPathFunction INSTANCE = new FileNameXPathFunction();

    private FileNameXPathFunction() {
        super("fileName");
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[0];
    }

    @Override
    public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return SequenceType.STRING_SEQUENCE;
    }

    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {

            @Override
            public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                Node node = ((AstElementNode) context.getContextItem()).getUnderlyingNode();
                RootNode root = node.getRoot();
                Objects.requireNonNull(root, "No root node in tree?");

                String fileName = root.getTextDocument().getDisplayName();
                Objects.requireNonNull(fileName, "File name was not set");
                String simpleFilename = Paths.get(fileName).getFileName().toString();

                return new StringValue(simpleFilename);
            }
        };
    }
}
