/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import java.util.function.BiPredicate;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.rule.xpath.internal.AstElementNode;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.SequenceType;


/**
 * XPath function {@code pmd-java:typeIs(typeName as xs:string) as xs:boolean}
 * and {@code typeIsExactly}.
 *
 * <p>Example XPath 2.0: {@code //ClassOrInterfaceType[pmd-java:typeIs('java.lang.String')]}
 *
 * <p>Returns true if the type of the node matches, false otherwise.
 */
public final class TypeIsFunction extends BaseJavaXPathFunction {

    public static final TypeIsFunction TYPE_IS_EXACTLY = new TypeIsFunction("typeIsExactly", TypeTestUtil::isExactlyA);
    public static final TypeIsFunction TYPE_IS = new TypeIsFunction("typeIs", TypeTestUtil::isA);

    private final BiPredicate<String, TypeNode> checker;

    private TypeIsFunction(String localName, BiPredicate<String, TypeNode> checker) {
        super(localName);
        this.checker = checker;
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[] {SequenceType.SINGLE_STRING};
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
                String fullTypeName = arguments[0].head().getStringValue();

                if (contextNode instanceof TypeNode) {
                    return BooleanValue.get(checker.test(fullTypeName, (TypeNode) contextNode));
                } else {
                    throw new IllegalArgumentException("typeIs function may only be called on a TypeNode.");
                }
            }
        };
    }
}
