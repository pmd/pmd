/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import java.util.function.BiPredicate;

import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * XPath function {@code pmd-java:typeIs(typeName as xs:string) as xs:boolean}
 * and {@code typeIsExactly}.
 *
 * <p>Example XPath 2.0: {@code //ClassType[pmd-java:typeIs('java.lang.String')]}
 *
 * <p>Returns true if the type of the node matches, false otherwise.
 */
public class BaseContextNodeTestFun<T extends JavaNode> extends BaseJavaXPathFunction {

    static final Type[] SINGLE_STRING_SEQ = {Type.SINGLE_STRING};
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

    @Override
    public FunctionCall makeCallExpression() {
        return (contextNode, arguments) -> {
            String fullTypeName = arguments[0].toString();
            return klass.isInstance(contextNode) && checker.test(fullTypeName, (T) contextNode);
        };
    }
}
