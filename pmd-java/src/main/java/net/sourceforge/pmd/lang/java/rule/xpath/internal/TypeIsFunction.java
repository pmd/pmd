/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.internal.AstNodeWrapper;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.SequenceType;


/**
 * XPath function {@code pmd-java:typeIs(typeName as xs:string) as xs:boolean}.
 *
 * <p>Example XPath 2.0: {@code //ClassOrInterfaceType[pmd-java:typeIs('java.lang.String')]}
 *
 * <p>Returns true if the type of the node matches, false otherwise.
 */
public class TypeIsFunction extends BaseJavaXPathFunction {

    public static final TypeIsFunction INSTANCE = new TypeIsFunction();

    private TypeIsFunction() {
        super("typeIs");
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[]{SequenceType.SINGLE_STRING};
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
                Node contextNode = ((AstNodeWrapper) context.getContextItem()).getUnderlyingNode();
                String fullTypeName = arguments[0].head().getStringValue();

                if (contextNode instanceof TypeNode) {
                    return BooleanValue.get(TypeHelper.isA((TypeNode) contextNode, fullTypeName));
                } else {
                    throw new IllegalArgumentException("typeIs function may only be called on a TypeNode.");
                }
            }
        };
    }
}
