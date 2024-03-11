/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * @author Clément Fournier
 */
class ApexQualifiedNameTest extends ApexParserTestBase {

    @Test
    void testClass() {
        ASTUserClass root = (ASTUserClass) parse("public class Foo {}");

        ApexQualifiedName qname = root.getQualifiedName();
        assertEquals("Foo", qname.toString());
        assertEquals(1, qname.getClasses().length);
        assertNull(qname.getOperation());
    }


    @Test
    void testNestedClass() {
        ASTUserClass root = (ASTUserClass) parse("public class Foo { class Bar {}}");

        ASTUserClass inner = root.descendants(ASTUserClass.class).firstOrThrow();
        ApexQualifiedName qname = inner.getQualifiedName();
        assertEquals("Foo.Bar", qname.toString());
        assertEquals(2, qname.getClasses().length);
        assertNull(qname.getOperation());
    }


    @Test
    void testSimpleMethod() {
        ASTUserClass root = (ASTUserClass) parse("public class Foo { String foo() {}}");
        ApexQualifiedName qname = root.descendants(ASTMethod.class).firstOrThrow().getQualifiedName();
        assertEquals("Foo#foo()", qname.toString());
        assertEquals(1, qname.getClasses().length);
        assertEquals("foo()", qname.getOperation());
    }


    @Test
    void testMethodWithArguments() {
        ASTUserClass root = (ASTUserClass) parse("public class Foo { String foo(String h, Foo g) {}}");
        ApexQualifiedName qname = root.descendants(ASTMethod.class).firstOrThrow().getQualifiedName();
        assertEquals("Foo#foo(String, Foo)", qname.toString());
        assertEquals(1, qname.getClasses().length);
        assertEquals("foo(String, Foo)", qname.getOperation());
    }


    @Test
    void testOverLoads() {
        ASTUserClass root = (ASTUserClass) parse("public class Foo { "
                                                                 + "String foo(String h) {} "
                                                                 + "String foo(int c) {}"
                                                                 + "String foo(Foo c) {}}");

        for (ASTMethod m1 : root.descendants(ASTMethod.class)) {
            for (ASTMethod m2 : root.descendants(ASTMethod.class)) {
                if (m1 != m2) {
                    assertNotEquals(m1.getQualifiedName(), m2.getQualifiedName());
                }
            }
        }
    }


    @Test
    void testTrigger() {
        ASTUserTrigger root = (ASTUserTrigger) parse("trigger myAccountTrigger on Account (before insert, before update) {}");


        ASTMethod m = root.descendants(ASTMethod.class).firstOrThrow();
        assertEquals("trigger.Account#myAccountTrigger", m.getQualifiedName().toString());
    }


    @Test
    void testUnqualifiedEnum() {
        ASTUserEnum root = (ASTUserEnum) parse("public enum primaryColor { RED, YELLOW, BLUE }");

        ApexQualifiedName enumQName = root.getQualifiedName();
        List<ASTMethod> methods = root.descendants(ASTMethod.class).toList();

        assertEquals("primaryColor", enumQName.toString());
        for (ASTMethod m : methods) {
            assertTrue(m.getQualifiedName().toString().startsWith("primaryColor#"));
        }
    }

    @Test
    void testQualifiedEnum() {
        ASTUserClass root = (ASTUserClass) parse("public class Outer { public enum Inner { OK } }");

        ASTUserEnum enumNode = root.descendants(ASTUserEnum.class).firstOrThrow();
        ApexQualifiedName enumQName = enumNode.getQualifiedName();
        List<ASTMethod> methods = enumNode.descendants(ASTMethod.class).toList();

        assertEquals("Outer.Inner", enumQName.toString());
        for (ASTMethod m : methods) {
            assertTrue(m.getQualifiedName().toString().startsWith("Outer.Inner#"));
        }
    }

    @Test
    void testOfString() {
        assertQualifiedName(new String[] { "MyClass" }, true, null, ApexQualifiedName.ofString("MyClass"));
        assertQualifiedName(new String[] { "Outer", "MyClass" }, true, null, ApexQualifiedName.ofString("Outer.MyClass"));
        assertQualifiedName(new String[] { "Foo" }, false, "foo(String, Foo)", ApexQualifiedName.ofString("Foo#foo(String, Foo)"));
    }

    private static void assertQualifiedName(String[] expectedClasses, boolean isClass, String expectedOperation, ApexQualifiedName name) {
        assertArrayEquals(expectedClasses, name.getClasses());
        assertEquals(isClass, name.isClass());
        assertEquals(!isClass, name.isOperation());
        assertEquals(expectedOperation, name.getOperation());

        if (isClass) {
            assertSame(name, name.getClassName());
        } else {
            assertArrayEquals(expectedClasses, name.getClassName().getClasses());
        }
    }
}
