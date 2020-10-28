/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import static net.sourceforge.pmd.lang.java.rule.xpath.internal.BaseContextNodeTestFun.SINGLE_STRING_SEQ;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.internal.AstElementNode;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.StaticContext;
import net.sf.saxon.expr.StringLiteral;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.SequenceType;

public final class NodeIsFunction extends BaseJavaXPathFunction {

    public static final NodeIsFunction INSTANCE = new NodeIsFunction();

    private NodeIsFunction() {
        super("nodeIs");
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return SINGLE_STRING_SEQ;
    }

    @Override
    public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return SequenceType.SINGLE_BOOLEAN;
    }

    @Override
    public boolean dependsOnFocus() {
        return true;
    }

    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {

            private Class<?> constantTestType = null;

            @Override
            public Expression rewrite(StaticContext context, Expression[] arguments) throws XPathException {
                // If the argument is a string literal then we can preload
                // the class, and check that it's valid at expression build time

                Expression firstArg = arguments[0]; // this expression has been type checked so there is an argument
                if (firstArg instanceof StringLiteral) {
                    String name = ((StringLiteral) firstArg).getStringValue();
                    constantTestType = getClassFromArg(name);
                }
                return null;
            }

            @Override
            public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                Node node = ((AstElementNode) context.getContextItem()).getUnderlyingNode();
                Class<?> klass = constantTestType != null ? constantTestType
                                                          : getClassFromArg(arguments[0].head().getStringValue());
                return BooleanValue.get(klass.isInstance(node));
            }
        };
    }

    private static Class<?> getClassFromArg(String name) throws XPathException {
        Class<?> klass;
        try {
            klass = Class.forName("net.sourceforge.pmd.lang.java.ast.AST" + name);
        } catch (ClassNotFoundException e) {
            throw new XPathException("No class named AST" + name);
        }
        return klass;
    }

}
