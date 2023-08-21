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


/**
 * A context node test function that may parse its string argument early
 * if it is a string literal.
 *
 * @param <S> Type of state into which the argument is parsed
 * @param <N> Type of node the function applies. The function will return
 *            false for other kinds of node.
 */
// TODO could move that up to pmd-core
abstract class BaseRewrittenFunction<S, N extends Node> extends BaseJavaXPathFunction {

    private final Class<N> contextNodeType;

    protected BaseRewrittenFunction(String localName, Class<N> contextNodeType) {
        super(localName);
        this.contextNodeType = contextNodeType;
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


    /**
     * Parse the argument into the state. This is called at build time
     * if the arg is constant, otherwise it's anyway called before {@link #matches(Node, String, Object, boolean)}
     * is called.
     */
    protected abstract S parseArgument(String arg) throws XPathException;

    /**
     * Compute the result of the function.
     *
     * @param contextNode Context node
     * @param arg         Value of the argument
     * @param parsedArg   Result of {@link #parseArgument(String)} on the argument
     * @param isConstant  Whether the argument is constant (it was parsed in all cases)
     *
     * @return Whether the function matches
     */
    protected abstract boolean matches(N contextNode, String arg, S parsedArg, boolean isConstant) throws XPathException;


    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {

            private S constantState;
            private boolean isConstant;

            @Override
            public Expression rewrite(StaticContext context, Expression[] arguments) throws XPathException {
                // If the argument is a string literal then we can preload
                // the class, and check that it's valid at expression build time

                Expression firstArg = arguments[0]; // this expression has been type checked so there is an argument
                if (firstArg instanceof StringLiteral) {
                    String name = ((StringLiteral) firstArg).getStringValue();
                    try {
                        constantState = parseArgument(name);
                    } catch (XPathException e) {
                        e.setIsStaticError(true);
                        throw e;
                    }
                    isConstant = true;
                }
                return null;
            }

            @Override
            public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                Node node = ((AstElementNode) context.getContextItem()).getUnderlyingNode();
                if (!contextNodeType.isInstance(node)) {
                    // we could report that as an error
                    return BooleanValue.FALSE;
                }

                String arg = arguments[0].head().getStringValue();
                S parsedArg = isConstant ? constantState
                                         : parseArgument(arg);

                return BooleanValue.get(matches((N) node, arg, parsedArg, isConstant));
            }
        };
    }
}
