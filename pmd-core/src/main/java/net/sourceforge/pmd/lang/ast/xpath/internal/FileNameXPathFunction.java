/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.internal;

import java.util.List;
import java.util.Objects;

import org.jaxen.Context;
import org.jaxen.FunctionCallException;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.XPathFunctionContext;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;

/**
 * A function that returns the current file name.
 *
 * @author Clément Fournier
 */
public class FileNameXPathFunction implements org.jaxen.Function {

    public static void registerSelfInSimpleContext() {
        ((SimpleFunctionContext) XPathFunctionContext.getInstance()).registerFunction(null, "fileName",
                                                                                      new FileNameXPathFunction("fileName"));
    }

    private final String name;

    public FileNameXPathFunction(String name) {
        this.name = name;
    }

    @Override
    public Object call(Context context, List args) throws FunctionCallException {
        if (!args.isEmpty()) {
            throw new IllegalArgumentException(name + " function takes no arguments.");
        }
        Node n = (Node) context.getNodeSet().get(0);

        return getFileName(n);
    }

    public static String getFileName(Node n) {
        // todo pmd7: replace with Node.getRoot()
        while (n.getParent() != null) {
            n = n.getParent();
        }
        Objects.requireNonNull(n, "No root node in tree?");

        String fileName = n.getUserMap().get(RootNode.FILE_NAME_KEY);
        return Objects.requireNonNull(fileName, "File name was not set");
    }
}
