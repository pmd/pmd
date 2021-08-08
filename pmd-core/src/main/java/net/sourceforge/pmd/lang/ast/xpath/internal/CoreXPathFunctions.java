/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.internal;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.saxon.ElementNode;

import net.sf.saxon.expr.XPathContext;

/**
 * @author Clément Fournier
 */
public final class CoreXPathFunctions {

    public static String fileName(final XPathContext context) {
        Node ctxNode = ((ElementNode) context.getContextItem()).getUnderlyingNode();
        return FileNameXPathFunction.getFileName(ctxNode);
    }
}
