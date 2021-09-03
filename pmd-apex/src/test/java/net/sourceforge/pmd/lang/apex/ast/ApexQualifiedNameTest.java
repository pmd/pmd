/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;


/**
 * @author Clément Fournier
 */
public class ApexQualifiedNameTest extends ApexParserTestBase {

    @Test
    public void testClass() {
        ASTUserClass root = (ASTUserClass) parse("public class Foo {}");

        ApexQualifiedName qname = root.getQualifiedName();
        assertEquals("c__Foo", qname.toString());
        assertEquals(1, qname.getClasses().length);
        assertNotNull(qname.getNameSpace());
        assertNull(qname.getOperation());
    }


    @Test
    public void testNestedClass() {
        ASTUserClass root = (ASTUserClass) parse("public class Foo { class Bar {}}");

        ASTUserClass inner = root.descendants(ASTUserClass.class).firstOrThrow();
        ApexQualifiedName qname = inner.getQualifiedName();
        assertEquals("c__Foo.Bar", qname.toString());
        assertEquals(2, qname.getClasses().length);
        assertNotNull(qname.getNameSpace());
        assertNull(qname.getOperation());
    }


    @Test
    public void testSimpleMethod() {
        ASTUserClass root = (ASTUserClass) parse("public class Foo { String foo() {}}");
        ApexQualifiedName qname = root.descendants(ASTMethod.class).firstOrThrow().getQualifiedName();
        assertEquals("c__Foo#foo()", qname.toString());
        assertEquals(1, qname.getClasses().length);
        assertNotNull(qname.getNameSpace());
        assertEquals("foo()", qname.getOperation());
    }


    @Test
    public void testMethodWithArguments() {
        ASTUserClass root = (ASTUserClass) parse("public class Foo { String foo(String h, Foo g) {}}");
        ApexQualifiedName qname = root.descendants(ASTMethod.class).firstOrThrow().getQualifiedName();
        assertEquals("c__Foo#foo(String, Foo)", qname.toString());
        assertEquals(1, qname.getClasses().length);
        assertNotNull(qname.getNameSpace());
        assertEquals("foo(String, Foo)", qname.getOperation());
    }


    @Test
    public void testOverLoads() {
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
    public void testTrigger() {
        ASTUserTrigger root = (ASTUserTrigger) parse("trigger myAccountTrigger on Account (before insert, before update) {}");


        ASTMethod m = root.descendants(ASTMethod.class).firstOrThrow();
        assertEquals("c__trigger.Account#myAccountTrigger", m.getQualifiedName().toString());
    }
}
