/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.junit.Assert;
import org.junit.Test;

import apex.jorje.semantic.ast.compilation.Compilation;

public class ASTUserInterfaceTest extends ApexParserTestBase {

    @Test
    public void testInterfaceName() {
        ApexNode<Compilation> node = parse("interface Foo { }");
        Assert.assertSame(ASTUserInterface.class, node.getClass());
        Assert.assertEquals("Foo", node.getImage());
    }

    @Test
    public void testInnerInterfaceName() {
        ApexNode<Compilation> node = parse("class Foo { interface Bar { } }");
        Assert.assertSame(ASTUserClass.class, node.getClass());
        ASTUserInterface innerNode = node.getFirstDescendantOfType(ASTUserInterface.class);
        Assert.assertNotNull(innerNode);
        Assert.assertEquals("Bar", innerNode.getImage());
    }

    @Test
    public void testSuperInterface() {
        ApexNode<?> node = parse("public interface CustomInterface extends A {}");
        Assert.assertSame(ASTUserInterface.class, node.getClass());
        ASTUserInterface toplevel = (ASTUserInterface) node;
        Assert.assertEquals("A", toplevel.getSuperInterfaceName());
    }

    @Test
    public void testSuperInterface2() {
        ApexNode<?> node = parse("public interface CustomInterface extends Other.A {}");
        Assert.assertSame(ASTUserInterface.class, node.getClass());
        ASTUserInterface toplevel = (ASTUserInterface) node;
        Assert.assertEquals("Other.A", toplevel.getSuperInterfaceName());
    }
}
