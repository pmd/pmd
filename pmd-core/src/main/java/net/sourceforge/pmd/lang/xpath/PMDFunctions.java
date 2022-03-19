/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xpath;

import java.util.logging.Logger;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.internal.FileNameXPathFunction;
import net.sourceforge.pmd.lang.ast.xpath.saxon.ElementNode;

import net.sf.saxon.dom.NodeWrapper;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;


@InternalApi
@Deprecated
public final class PMDFunctions {

    private static final Logger LOG = Logger.getLogger(PMDFunctions.class.getName());

    private PMDFunctions() { }

    public static boolean matches(String s, String pattern1) {
        return MatchesFunction.matches(s, pattern1);
    }

    public static boolean matches(String s, String pattern1, String pattern2) {
        return MatchesFunction.matches(s, pattern1, pattern2);
    }

    public static boolean matches(String s, String pattern1, String pattern2, String pattern3) {
        return MatchesFunction.matches(s, pattern1, pattern2, pattern3);
    }

    public static boolean matches(String s, String pattern1, String pattern2, String pattern3, String pattern4) {
        return MatchesFunction.matches(s, pattern1, pattern2, pattern3, pattern4);
    }

    public static boolean matches(String s, String pattern1, String pattern2, String pattern3, String pattern4,
                                  String pattern5) {
        return MatchesFunction.matches(s, pattern1, pattern2, pattern3, pattern4, pattern5);
    }

    public static boolean matches(String s, String pattern1, String pattern2, String pattern3, String pattern4,
                                  String pattern5, String pattern6) {
        return MatchesFunction.matches(s, pattern1, pattern2, pattern3, pattern4, pattern5, pattern6);
    }

    public static String fileName(final XPathContext context) {
        Item contextItem = context.getContextItem();
        Node node = itemToNode(contextItem);
        return node == null ? null : FileNameXPathFunction.getFileName(node);
    }

    private static Node itemToNode(Object item) {
        if (item instanceof Node) {
            return (Node) item;
        } else if (item instanceof ElementNode) { // todo pmd 7 remove this branch as ElementNode extends NodeWrapper
            return itemToNode(((ElementNode) item).getUnderlyingNode());
        } else if (item instanceof NodeWrapper) {
            return itemToNode(((NodeWrapper) item).getUnderlyingNode());
        } else if (item instanceof org.w3c.dom.Node) {
            return itemToNode(((org.w3c.dom.Node) item).getUserData("pmd.node"));
        }
        LOG.fine("Cannot call pmd:filename on " + item);
        return null;
    }
}
