/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.xpath;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.saxon.ElementNode;

import net.sf.saxon.expr.XPathContext;

/**
 * Exposes all Java Language specific functions for Saxon use.
 */
@InternalApi
@Deprecated
public final class JavaFunctions {

    private JavaFunctions() {
        // utility class
    }

    @Deprecated
    public static boolean typeof(final XPathContext context, final String nodeTypeName, final String fullTypeName) {
        return typeof(context, nodeTypeName, fullTypeName, null);
    }

    @Deprecated
    public static boolean typeof(final XPathContext context, final String nodeTypeName,
            final String fullTypeName, final String shortTypeName) {
        return TypeOfFunction.typeof((Node) ((ElementNode) context.getContextItem()).getUnderlyingNode(), nodeTypeName,
                fullTypeName, shortTypeName);
    }

    public static double metric(final XPathContext context, final String metricKeyName) {
        return MetricFunction.getMetric((Node) ((ElementNode) context.getContextItem()).getUnderlyingNode(), metricKeyName);
    }

    public static boolean typeIs(final XPathContext context, final String fullTypeName) {
        return TypeIsFunction.typeIs((Node) ((ElementNode) context.getContextItem()).getUnderlyingNode(), fullTypeName);
    }

    public static boolean typeIsExactly(final XPathContext context, final String fullTypeName) {
        return TypeIsExactlyFunction.typeIsExactly((Node) ((ElementNode) context.getContextItem()).getUnderlyingNode(), fullTypeName);
    }
}
