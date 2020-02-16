/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import apex.jorje.semantic.ast.compilation.Compilation;

public class ASTUserClassTest extends ApexParserTestBase {

    @Test
    public void testClassName() {
        ApexNode<Compilation> node = parse("class Foo { }");
        Assert.assertSame(ASTUserClass.class, node.getClass());
        Assert.assertEquals("Foo", node.getImage());
    }

    @Test
    public void testInnerClassName() {
        ApexNode<Compilation> node = parse("class Foo { class Bar { } }");
        Assert.assertSame(ASTUserClass.class, node.getClass());
        ASTUserClass innerNode = node.getFirstDescendantOfType(ASTUserClass.class);
        Assert.assertNotNull(innerNode);
        Assert.assertEquals("Bar", innerNode.getImage());
    }

    @Test
    public void testSuperClassName() {
        ApexNode<?> node = parse("public class AccountTriggerHandler extends TriggerHandler {}");
        Assert.assertSame(ASTUserClass.class, node.getClass());
        ASTUserClass toplevel = (ASTUserClass) node;
        Assert.assertEquals("TriggerHandler", toplevel.getSuperClassName());
    }

    @Test
    public void testSuperClassName2() {
        ApexNode<?> node = parse("public class AccountTriggerHandler extends Other.TriggerHandler {}");
        Assert.assertSame(ASTUserClass.class, node.getClass());
        ASTUserClass toplevel = (ASTUserClass) node;
        Assert.assertEquals("Other.TriggerHandler", toplevel.getSuperClassName());
    }

    @Test
    public void testInterfaces() {
        ApexNode<?> node = parse("public class AccountTriggerHandler implements TriggerHandler, Other.Interface2 {}");
        Assert.assertSame(ASTUserClass.class, node.getClass());
        ASTUserClass toplevel = (ASTUserClass) node;
        Assert.assertEquals(Arrays.asList("TriggerHandler", "Other.Interface2"), toplevel.getInterfaceNames());
    }
}
