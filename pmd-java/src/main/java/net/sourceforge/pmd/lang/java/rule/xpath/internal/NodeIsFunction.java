/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;

public final class NodeIsFunction extends BaseRewrittenFunction<Class<?>, JavaNode> {

    public static final NodeIsFunction INSTANCE = new NodeIsFunction();

    private NodeIsFunction() {
        super("nodeIs", JavaNode.class);
    }

    @Override
    protected Class<?> parseArgument(String arg) throws XPathFunctionException {
        try {
            return Class.forName("net.sourceforge.pmd.lang.java.ast.AST" + arg);
        } catch (ClassNotFoundException e) {
            throw new XPathFunctionException("No class named AST" + arg, e);
        }
    }

    @Override
    protected boolean matches(JavaNode contextNode, String arg, Class<?> parsedArg, boolean isConstant) {
        return parsedArg.isInstance(contextNode);
    }

}
