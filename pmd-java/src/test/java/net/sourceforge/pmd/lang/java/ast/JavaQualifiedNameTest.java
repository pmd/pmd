/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ParserTstUtil.getNodes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.ParserTstUtil;


/**
 * @author Clément Fournier
 */
public class JavaQualifiedNameTest {


    /** Provides a hook into the package-private reset method for the local indices counter. */
    public static void resetLocalIndicesCounterHook() {
        JavaQualifiedName.resetGlobalIndexCounters();
    }


    @Before
    public void setUp() {
        resetLocalIndicesCounterHook();
    }


    @Test
    public void testEmptyPackage() {
        final String TEST = "class Foo {}";
        Set<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class,
                                                             TEST);
        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            JavaQualifiedName qname = coid.getQualifiedName();
            assertEquals("Foo", qname.toString());
            assertTrue(qname.getPackages().isEmpty());
            assertTrue(qname.isUnnamedPackage());
            assertEquals(1, qname.getClasses().size());
            assertNull(qname.getOperation());
        }
    }


    @Test
    public void testPackage() {
        final String TEST = "package foo.bar; class Bzaz{}";

        Set<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class,
                                                             TEST);
        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            JavaQualifiedName qname = coid.getQualifiedName();
            assertEquals("foo.bar.Bzaz", qname.toString());
            assertEquals(2, qname.getPackages().size());
            assertEquals(1, qname.getClasses().size());
            assertNull(qname.getOperation());
        }
    }


    @Test
    public void testNestedClass() {
        final String TEST = "package foo.bar; class Bzaz{ class Bor{ class Foo{}}}";

        Set<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class,
                                                             TEST);

        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            JavaQualifiedName qname = coid.getQualifiedName();
            switch (coid.getImage()) {
            case "Foo":
                assertEquals("foo.bar.Bzaz$Bor$Foo",
                             qname.toString());
                assertEquals(3, qname.getClasses().size());
                break;
            default:
                break;
            }
        }
    }


    @Test
    public void testNestedEnum() {
        final String TEST = "package foo.bar; class Foo { enum Bzaz{HOO;}}";

        Set<ASTEnumDeclaration> nodes = getNodes(ASTEnumDeclaration.class, TEST);

        for (ASTEnumDeclaration coid : nodes) {
            JavaQualifiedName qname = coid.getQualifiedName();
            assertEquals("foo.bar.Foo$Bzaz", qname.toString());
            assertEquals(2, qname.getPackages().size());
            assertEquals(2, qname.getClasses().size());
            assertNull(qname.getOperation());
        }
    }


    @Test
    public void testEnum() {
        final String TEST = "package foo.bar; enum Bzaz{HOO;}";

        Set<ASTEnumDeclaration> nodes = getNodes(ASTEnumDeclaration.class, TEST);

        for (ASTEnumDeclaration coid : nodes) {
            JavaQualifiedName qname = coid.getQualifiedName();
            assertEquals("foo.bar.Bzaz", qname.toString());
            assertEquals(2, qname.getPackages().size());
            assertEquals(1, qname.getClasses().size());
            assertNull(qname.getOperation());
        }
    }


    @Test
    public void testEnumMethodMember() {
        final String TEST = "package foo.bar; enum Bzaz{HOO; void foo(){}}";

        Set<ASTMethodDeclaration> nodes = getNodes(ASTMethodDeclaration.class, TEST);

        for (ASTMethodDeclaration coid : nodes) {
            JavaQualifiedName qname = coid.getQualifiedName();
            assertEquals("foo.bar.Bzaz#foo()", qname.toString());
            assertEquals(2, qname.getPackages().size());
            assertEquals(1, qname.getClasses().size());
            assertEquals("foo()", qname.getOperation());
        }
    }


    @Test
    public void testNestedEmptyPackage() {
        final String TEST = "class Bzaz{ class Bor{ class Foo{}}}";

        Set<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class,
                                                             TEST);

        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            JavaQualifiedName qname = coid.getQualifiedName();
            switch (coid.getImage()) {
            case "Foo":
                assertEquals("Bzaz$Bor$Foo",
                             qname.toString());
                assertTrue(qname.getPackages().isEmpty());
                assertTrue(qname.isUnnamedPackage());
                assertEquals(3, qname.getClasses().size());
                break;
            default:
                break;
            }
        }
    }


    @Test
    public void testMethod() {
        final String TEST = "package bar; class Bzaz{ public void foo(){}}";

        Set<ASTMethodDeclaration> nodes = getNodes(ASTMethodDeclaration.class,
                                                   TEST);

        for (ASTMethodDeclaration declaration : nodes) {
            JavaQualifiedName qname = declaration.getQualifiedName();
            assertEquals("bar.Bzaz#foo()", qname.toString());
            assertNotNull(qname.getOperation());
            assertEquals("foo()", qname.getOperation());

        }
    }


    @Test
    public void testConstructor() {
        final String TEST = "package bar; class Bzaz{ public Bzaz(){}}";

        Set<ASTConstructorDeclaration> nodes = getNodes(ASTConstructorDeclaration.class,
                                                        TEST);

        for (ASTConstructorDeclaration declaration : nodes) {
            JavaQualifiedName qname = declaration.getQualifiedName();
            assertEquals("bar.Bzaz#Bzaz()",
                         qname.toString());
            assertNotNull(qname.getOperation());
            assertEquals("Bzaz()", qname.getOperation());

        }
    }


    @Test
    public void testConstructorWithParams() {
        final String TEST = "package bar; class Bzaz{ public Bzaz(int j, String k){}}";

        Set<ASTConstructorDeclaration> nodes = getNodes(ASTConstructorDeclaration.class,
                                                        TEST);

        for (ASTConstructorDeclaration declaration : nodes) {
            JavaQualifiedName qname = declaration.getQualifiedName();
            assertEquals("bar.Bzaz#Bzaz(int, String)", qname.toString());
            assertNotNull(qname.getOperation());
            assertEquals("Bzaz(int, String)", qname.getOperation());

        }
    }


    @Test
    public void testConstructorOverload() {
        final String TEST = "package bar; class Bzaz{ public Bzaz(int j) {} public Bzaz(int j, String k){}}";

        Set<ASTConstructorDeclaration> nodes = getNodes(ASTConstructorDeclaration.class,
                                                        TEST);

        ASTConstructorDeclaration[] arr = nodes.toArray(new ASTConstructorDeclaration[2]);
        assertNotEquals(arr[0].getQualifiedName(), arr[1].getQualifiedName());
    }


    @Test
    public void testMethodOverload() {
        final String TEST = "package bar; class Bzaz{ public void foo(String j) {} "
                + "public void foo(int j){} public void foo(double k){}}";

        Set<ASTMethodDeclaration> nodes = getNodes(ASTMethodDeclaration.class, TEST);

        ASTMethodDeclaration[] arr = nodes.toArray(new ASTMethodDeclaration[3]);
        assertNotEquals(arr[0].getQualifiedName(), arr[1].getQualifiedName());
        assertNotEquals(arr[1].getQualifiedName(), arr[2].getQualifiedName());
    }


    @Test
    public void testParseClass() {
        JavaQualifiedName outer = JavaQualifiedName.ofString("foo.bar.Bzaz");
        JavaQualifiedName nested = JavaQualifiedName.ofString("foo.bar.Bzaz$Bolg");

        assertEquals(1, outer.getClasses().size());
        assertEquals("Bzaz", outer.getClasses().head());

        assertEquals(2, nested.getClasses().size());
        assertEquals("Bzaz", nested.getClasses().head());
        assertEquals("Bolg", nested.getClasses().get(1));
    }


    @Test
    public void testParsePackages() {
        JavaQualifiedName packs = JavaQualifiedName.ofString("foo.bar.Bzaz$Bolg");
        JavaQualifiedName nopacks = JavaQualifiedName.ofString("Bzaz");

        assertNotNull(packs.getPackages());
        assertEquals("foo", packs.getPackages().get(0));
        assertEquals("bar", packs.getPackages().get(1));

        assertTrue(nopacks.getPackages().isEmpty());
    }


    @Test
    public void testParseOperation() {
        JavaQualifiedName noparams = JavaQualifiedName.ofString("foo.bar.Bzaz$Bolg#bar()");
        JavaQualifiedName params = JavaQualifiedName.ofString("foo.bar.Bzaz#bar(String, int)");

        assertEquals("bar()", noparams.getOperation());
        assertEquals("bar(String, int)", params.getOperation());
    }


    @Test
    public void testParseLocalClasses() {
        final String SIMPLE = "foo.bar.Bzaz$1Local";
        final String NESTED = "foo.Bar$1Local$Nested";
        JavaQualifiedName simple = JavaQualifiedName.ofString(SIMPLE);
        JavaQualifiedName nested = JavaQualifiedName.ofString(NESTED);

        assertNotNull(simple);
        assertTrue(simple.isLocalClass());
        assertFalse(simple.isAnonymousClass());
        assertNotNull(nested);
        assertFalse(nested.isLocalClass());
        assertFalse(simple.isAnonymousClass());

        assertEquals(SIMPLE, simple.toString());
        assertEquals(NESTED, nested.toString());

    }


    @Test
    public void testParseAnonymousClass() {
        final String SIMPLE = "Bzaz$12$13";

        JavaQualifiedName simple = JavaQualifiedName.ofString(SIMPLE);

        assertNotNull(simple);
        assertTrue(simple.isAnonymousClass());
        assertFalse(simple.isLocalClass());

        assertEquals("12", simple.getClasses().get(1));
        assertEquals("13", simple.getClasses().get(2));

        assertEquals(SIMPLE, simple.toString());
    }


    @Test
    public void testParseMalformed() {
        assertNull(JavaQualifiedName.ofString(".foo.bar.Bzaz"));
        assertNull(JavaQualifiedName.ofString("foo.bar."));
        assertNull(JavaQualifiedName.ofString("foo.bar.Bzaz#foo"));
        assertNull(JavaQualifiedName.ofString("foo.bar.Bzaz()"));
        assertNull(JavaQualifiedName.ofString("foo.bar.Bzaz#foo(String,)"));
        assertNull(JavaQualifiedName.ofString("foo.bar.Bzaz#foo(String , int)"));
    }


    @Test
    public void testSimpleLocalClass() {
        final String TEST = "package bar; class Boron { public void foo(String j) { class Local {} } }";

        List<ASTClassOrInterfaceDeclaration> classes
                = ParserTstUtil.getOrderedNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        JavaQualifiedName qname = JavaQualifiedName.ofString("bar.Boron$1Local");

        assertEquals(qname, classes.get(1).getQualifiedName());
    }


    @Test
    public void testLocalClassNameClash() {
        final String TEST = "package bar; class Bzaz{ void foo() { class Local {} } {// initializer\n class Local {}}}";

        List<ASTClassOrInterfaceDeclaration> classes
                = ParserTstUtil.getOrderedNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        assertNotEquals(classes.get(1).getQualifiedName(), classes.get(2).getQualifiedName());

        assertEquals(JavaQualifiedName.ofString("bar.Bzaz$1Local"), classes.get(1).getQualifiedName());
        assertEquals(JavaQualifiedName.ofString("bar.Bzaz$2Local"), classes.get(2).getQualifiedName());
    }


    @Test
    public void testLocalClassDeepNesting() {
        final String TEST
                = "class Bzaz{ void foo() { "
                + "  class Local { "
                + "    class Nested {"
                + "      {"
                + "        class InnerLocal{}"
                + "      }"
                + "    }"
                + "  }"
                + "}}";

        List<ASTClassOrInterfaceDeclaration> classes
                = ParserTstUtil.getOrderedNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        assertNotEquals(classes.get(1).getQualifiedName(), classes.get(2).getQualifiedName());

        assertEquals(JavaQualifiedName.ofString("Bzaz$1Local"), classes.get(1).getQualifiedName());
        assertEquals(JavaQualifiedName.ofString("Bzaz$1Local$Nested"), classes.get(2).getQualifiedName());
        assertEquals(JavaQualifiedName.ofString("Bzaz$1Local$Nested$1InnerLocal"), classes.get(3).getQualifiedName());
    }


    @Test
    public void testAnonymousClass() {
        final String TEST
                = "class Bzaz{ void foo() { "
                + "  new Runnable() {"
                + "      public void run() {}"
                + "  };"
                + "}}";

        List<ASTAllocationExpression> classes = ParserTstUtil.getOrderedNodes(ASTAllocationExpression.class, TEST);

        assertEquals(JavaQualifiedName.ofString("Bzaz$1"), JavaQualifiedName.ofAnonymousClass(classes.get(0)));
        assertFalse(JavaQualifiedName.ofAnonymousClass(classes.get(0)).isLocalClass());
        assertTrue(JavaQualifiedName.ofAnonymousClass(classes.get(0)).isAnonymousClass());
        assertTrue("1".equals(JavaQualifiedName.ofAnonymousClass(classes.get(0)).getClassSimpleName()));
    }


    @Test
    public void testMultipleAnonymousClasses() {
        final String TEST
                = "class Bzaz{ void foo() { "
                + "  new Runnable() {"
                + "      public void run() {}"
                + "  };"
                + "  new Runnable() {"
                + "      public void run() {}"
                + "  };"
                + "}}";

        List<ASTAllocationExpression> classes = ParserTstUtil.getOrderedNodes(ASTAllocationExpression.class, TEST);

        assertNotEquals(classes.get(0), classes.get(1));
        assertEquals(JavaQualifiedName.ofString("Bzaz$1"), JavaQualifiedName.ofAnonymousClass(classes.get(0)));
        assertEquals(JavaQualifiedName.ofString("Bzaz$2"), JavaQualifiedName.ofAnonymousClass(classes.get(1)));
    }


    @Test
    public void testNestedAnonymousClass() {
        final String TEST
                = "class Bzaz{ void foo() {"
                + "  new Runnable() {"
                + "    public void run() {"
                + "      new Runnable() {"
                + "        public void run() {}"
                + "      };"
                + "    }"
                + "  };"
                + "}}";

        List<ASTAllocationExpression> classes = ParserTstUtil.getOrderedNodes(ASTAllocationExpression.class, TEST);

        assertNotEquals(classes.get(0), classes.get(1));
        assertEquals(JavaQualifiedName.ofString("Bzaz$1"), JavaQualifiedName.ofAnonymousClass(classes.get(0)));
        assertEquals(JavaQualifiedName.ofString("Bzaz$1$1"), JavaQualifiedName.ofAnonymousClass(classes.get(1)));
    }


    @Test
    public void testLocalInAnonymousClass() {
        final String TEST
                = "class Bzaz{ void foo() {"
                + "  new Runnable() {"
                + "    public void run() {"
                + "      class FooRunnable {}"
                + "    }"
                + "  };"
                + "}}";

        List<ASTClassOrInterfaceDeclaration> classes = ParserTstUtil.getOrderedNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        assertTrue(classes.get(1).isLocal());
        assertEquals(JavaQualifiedName.ofString("Bzaz$1$1FooRunnable"), classes.get(1).getQualifiedName());
    }
}

