/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.rule.xpath.internal.AstElementNode;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.SequenceType;

/**
 * XPath function {@code pmd-kotlin:hasChildren(count as xs:decimal) as xs:boolean}
 *
 * <p>Example XPath 3.1: {@code //Identifier[pmd-kotlin:hasChildren(3)]}
 *
 * <p>Returns true if the node has children, false otherwise.
 */
public class BaseContextNodeTestFun<T extends KotlinNode> extends BaseKotlinXPathFunction {

    static final SequenceType[] NO_ARGUMENTS = { SequenceType.SINGLE_INTEGER };
    private final Class<T> klass;

    public static final BaseKotlinXPathFunction HAS_CHILDREN = new BaseContextNodeTestFun<>(KotlinNode.class, "hasChildren");

    protected BaseContextNodeTestFun(Class<T> klass, String localName) {
        super(localName);
        this.klass = klass;
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return NO_ARGUMENTS;
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
            @Override
            public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                Node contextNode = ((AstElementNode) context.getContextItem()).getUnderlyingNode();
                boolean hasChildren = contextNode.getNumChildren() == Integer.parseInt(arguments[0].head().getStringValue());
                return BooleanValue.get(klass.isInstance(contextNode) && hasChildren);
            }
        };
    }

}
