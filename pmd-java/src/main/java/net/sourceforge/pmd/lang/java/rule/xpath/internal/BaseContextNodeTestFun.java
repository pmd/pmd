/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import java.util.function.BiPredicate;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
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
public class BaseContextNodeTestFun<T extends JavaNode> extends BaseJavaXPathFunction {

    static final SequenceType[] SINGLE_STRING_SEQ = {SequenceType.SINGLE_STRING};
    private final Class<T> klass;
    private final BiPredicate<String, T> checker;

    public static final BaseJavaXPathFunction TYPE_IS_EXACTLY = new BaseContextNodeTestFun<>(TypeNode.class, "typeIsExactly", TypeTestUtil::isExactlyA);
    public static final BaseJavaXPathFunction TYPE_IS = new BaseContextNodeTestFun<>(TypeNode.class, "typeIs", TypeTestUtil::isA);
    public static final BaseJavaXPathFunction HAS_ANNOTATION = new BaseContextNodeTestFun<>(Annotatable.class, "hasAnnotation", (name, node) -> node.isAnnotationPresent(name));

    protected BaseContextNodeTestFun(Class<T> klass, String localName, BiPredicate<String, T> checker) {
        super(localName);
        this.klass = klass;
        this.checker = checker;
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
            @Override
            public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                Node contextNode = ((AstElementNode) context.getContextItem()).getUnderlyingNode();
                String fullTypeName = arguments[0].head().getStringValue();


                return BooleanValue.get(klass.isInstance(contextNode) && checker.test(fullTypeName, (T) contextNode));
            }
        };
    }
}
