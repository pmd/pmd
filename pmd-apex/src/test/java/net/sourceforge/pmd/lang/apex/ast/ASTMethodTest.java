/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static net.sourceforge.pmd.lang.apex.ast.ApexParserTestHelpers.parse;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import apex.jorje.semantic.ast.compilation.Compilation;

public class ASTMethodTest {

    @Test
    public void testConstructorName() {
        ApexNode<Compilation> node = parse("public class Foo { public Foo() {} public void bar() {} }");
        Assert.assertSame(ASTUserClass.class, node.getClass());
        List<ASTMethod> methods = node.findChildrenOfType(ASTMethod.class);
        Assert.assertEquals("Foo", methods.get(0).getImage()); // constructor
        Assert.assertEquals("bar", methods.get(1).getImage()); // normal method
    }
}
