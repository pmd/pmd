/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class ASTUserClassTest extends ApexParserTestBase {

    @Test
    public void testClassName() {
        ASTUserClass node = (ASTUserClass) parse("class Foo { }");
        Assert.assertEquals("Foo", node.getSimpleName());
    }

    @Test
    public void testInnerClassName() {
        ASTUserClass foo = (ASTUserClass) parse("class Foo { class Bar { } }");
        ASTUserClass innerNode = foo.descendants(ASTUserClass.class).firstOrThrow();
        Assert.assertEquals("Bar", innerNode.getSimpleName());
    }

    @Test
    public void testSuperClassName() {
        ASTUserClass toplevel = (ASTUserClass) parse("public class AccountTriggerHandler extends TriggerHandler {}");
        Assert.assertEquals("TriggerHandler", toplevel.getSuperClassName());
    }

    @Test
    public void testSuperClassName2() {
        ASTUserClass toplevel = (ASTUserClass) parse("public class AccountTriggerHandler extends Other.TriggerHandler {}");
        Assert.assertEquals("Other.TriggerHandler", toplevel.getSuperClassName());
    }

    @Test
    public void testInterfaces() {
        ASTUserClass toplevel = (ASTUserClass) parse("public class AccountTriggerHandler implements TriggerHandler, Other.Interface2 {}");
        Assert.assertEquals(Arrays.asList("TriggerHandler", "Other.Interface2"), toplevel.getInterfaceNames());
    }
}
