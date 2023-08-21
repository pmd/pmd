/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class ASTUserClassTest extends ApexParserTestBase {

    @Test
    void testClassName() {
        ASTUserClass node = (ASTUserClass) parse("class Foo { }");
        assertEquals("Foo", node.getSimpleName());
    }

    @Test
    void testInnerClassName() {
        ASTUserClass foo = (ASTUserClass) parse("class Foo { class Bar { } }");
        ASTUserClass innerNode = foo.descendants(ASTUserClass.class).firstOrThrow();
        assertEquals("Bar", innerNode.getSimpleName());
    }

    @Test
    void testSuperClassName() {
        ASTUserClass toplevel = (ASTUserClass) parse("public class AccountTriggerHandler extends TriggerHandler {}");
        assertEquals("TriggerHandler", toplevel.getSuperClassName());
    }

    @Test
    void testSuperClassName2() {
        ASTUserClass toplevel = (ASTUserClass) parse("public class AccountTriggerHandler extends Other.TriggerHandler {}");
        assertEquals("Other.TriggerHandler", toplevel.getSuperClassName());
    }

    @Test
    void testInterfaces() {
        ASTUserClass toplevel = (ASTUserClass) parse("public class AccountTriggerHandler implements TriggerHandler, Other.Interface2 {}");
        assertEquals(Arrays.asList("TriggerHandler", "Other.Interface2"), toplevel.getInterfaceNames());
    }
}
