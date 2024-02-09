/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.Objects;

import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionDefinition;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;

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
    public Type getResultType() {
        return Type.SINGLE_STRING;
    }

    @Override
    public boolean dependsOnContext() {
        return true;
    }

    @Override
    public FunctionCall makeCallExpression() {
        return (node, arguments) -> {
            if (node == null) {
                throw new XPathFunctionException(
                    "Cannot call function '" + getQName().getLocalPart()
                        + "' without context item"
                );
            }
            RootNode root = node.getRoot();
            Objects.requireNonNull(root, "No root node in tree?");

            String fileName = root.getTextDocument().getFileId().getFileName();
            Objects.requireNonNull(fileName, "File name was not set");

            return fileName;
        };
    }
}
