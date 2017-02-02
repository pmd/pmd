/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.xpath;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.saxon.ElementNode;

import net.sf.saxon.expr.XPathContext;

/**
 * Exposes all Java Language specific functions for Saxon use.
 */
public final class JavaFunctions {

    private JavaFunctions() {
        // utility class
    }

    public static boolean typeof(XPathContext context, String nodeTypeName, String fullTypeName) {
        return typeof(context, nodeTypeName, fullTypeName, null);
    }

    public static boolean typeof(XPathContext context, String nodeTypeName, String fullTypeName, String shortTypeName) {
        return TypeOfFunction.typeof((Node) ((ElementNode) context.getContextItem()).getUnderlyingNode(), nodeTypeName,
                fullTypeName, shortTypeName);
    }
}
