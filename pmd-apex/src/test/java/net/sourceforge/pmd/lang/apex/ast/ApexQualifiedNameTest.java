/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
        assertEquals("c__Foo", qname.toString());
        assertEquals(1, qname.getClasses().length);
        assertNotNull(qname.getNameSpace());
        assertNull(qname.getOperation());
    }


    @Test
    void testNestedClass() {
        ASTUserClass root = (ASTUserClass) parse("public class Foo { class Bar {}}");

        ASTUserClass inner = root.descendants(ASTUserClass.class).firstOrThrow();
        ApexQualifiedName qname = inner.getQualifiedName();
        assertEquals("c__Foo.Bar", qname.toString());
        assertEquals(2, qname.getClasses().length);
        assertNotNull(qname.getNameSpace());
        assertNull(qname.getOperation());
    }


    @Test
    void testSimpleMethod() {
        ASTUserClass root = (ASTUserClass) parse("public class Foo { String foo() {}}");
        ApexQualifiedName qname = root.descendants(ASTMethod.class).firstOrThrow().getQualifiedName();
        assertEquals("c__Foo#foo()", qname.toString());
        assertEquals(1, qname.getClasses().length);
        assertNotNull(qname.getNameSpace());
        assertEquals("foo()", qname.getOperation());
    }


    @Test
    void testMethodWithArguments() {
        ASTUserClass root = (ASTUserClass) parse("public class Foo { String foo(String h, Foo g) {}}");
        ApexQualifiedName qname = root.descendants(ASTMethod.class).firstOrThrow().getQualifiedName();
        assertEquals("c__Foo#foo(String, Foo)", qname.toString());
        assertEquals(1, qname.getClasses().length);
        assertNotNull(qname.getNameSpace());
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
        assertEquals("c__trigger.Account#myAccountTrigger", m.getQualifiedName().toString());
    }


    @Test
    void testUnqualifiedEnum() {
        ASTUserEnum root = (ASTUserEnum) parse("public enum primaryColor { RED, YELLOW, BLUE }");

        ApexQualifiedName enumQName = root.getQualifiedName();
        List<ASTMethod> methods = root.descendants(ASTMethod.class).toList();

        assertEquals("c__primaryColor", enumQName.toString());
        for (ASTMethod m : methods) {
            assertTrue(m.getQualifiedName().toString().startsWith("c__primaryColor#"));
        }
    }

    @Test
    void testQualifiedEnum() {
        ASTUserClass root = (ASTUserClass) parse("public class Outer { public enum Inner { OK } }");

        ASTUserEnum enumNode = root.descendants(ASTUserEnum.class).firstOrThrow();
        ApexQualifiedName enumQName = enumNode.getQualifiedName();
        List<ASTMethod> methods = enumNode.descendants(ASTMethod.class).toList();

        assertEquals("c__Outer.Inner", enumQName.toString());
        for (ASTMethod m : methods) {
            assertTrue(m.getQualifiedName().toString().startsWith("c__Outer.Inner#"));
        }
    }
}
