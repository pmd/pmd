/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import static net.sourceforge.pmd.lang.java.rule.xpath.internal.BaseContextNodeTestFun.SINGLE_STRING_SEQ;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;


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
    public Type[] getArgumentTypes() {
        return SINGLE_STRING_SEQ;
    }

    @Override
    public Type getResultType() {
        return Type.SINGLE_BOOLEAN;
    }

    @Override
    public boolean dependsOnContext() {
        return true;
    }


    /**
     * Parse the argument into the state. This is called at build time
     * if the arg is constant, otherwise it's anyway called before {@link #matches(Node, String, Object, boolean)}
     * is called.
     */
    protected abstract S parseArgument(String arg) throws XPathFunctionException;

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
    protected abstract boolean matches(N contextNode, String arg, S parsedArg, boolean isConstant) throws XPathFunctionException;


    @Override
    public FunctionCall makeCallExpression() {
        return new FunctionCall() {

            private S constantState;
            private boolean isConstant;

            @Override
            public void staticInit(Object[] arguments) throws XPathFunctionException {
                if (arguments[0] instanceof String) {
                    // If the argument was a string literal then we can preload
                    // the class, and check that it's valid at expression build time

                    String name = (String) arguments[0]; // this expression has been type checked so there is an argument
                    constantState = parseArgument(name);
                    isConstant = true;
                }
            }

            @Override
            public Boolean call(Node node, Object[] arguments) throws XPathFunctionException {
                if (!contextNodeType.isInstance(node)) {
                    // we could report that as an error
                    return false;
                }

                String arg = arguments[0].toString();
                S parsedArg = isConstant ? constantState
                                         : parseArgument(arg);

                return matches((N) node, arg, parsedArg, isConstant);
            }
        };
    }
}
