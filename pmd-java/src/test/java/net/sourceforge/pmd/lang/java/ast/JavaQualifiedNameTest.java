/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.qname.JavaOperationQualifiedName;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.qname.QualifiedNameFactory;


/**
 * @author Clément Fournier
 */
public class JavaQualifiedNameTest {


    private <T extends Node> List<T> getNodes(Class<T> target, String code) {
        return JavaParsingHelper.WITH_PROCESSING.getNodes(target, code);
    }

    @Test
    public void testEmptyPackage() {
        final String TEST = "class Foo {}";
        List<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);
        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            JavaTypeQualifiedName qname = coid.getQualifiedName();
            assertEquals("Foo", qname.toString());
            assertTrue(qname.getPackageList().isEmpty());
            assertTrue(qname.isUnnamedPackage());
            assertEquals(1, qname.getClassList().size());
        }
    }


    @Test
    public void testPackage() {
        final String TEST = "package foo.bar; class Bzaz{}";

        List<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);
        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            JavaTypeQualifiedName qname = coid.getQualifiedName();
            assertEquals("foo.bar.Bzaz", qname.toString());
            assertEquals(2, qname.getPackageList().size());
            assertEquals(1, qname.getClassList().size());
        }
    }


    @Test
    public void testNestedClass() {
        final String TEST = "package foo.bar; class Bzaz{ class Bor{ class Foo{}}}";

        List<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            JavaTypeQualifiedName qname = coid.getQualifiedName();
            switch (coid.getImage()) {
            case "Foo":
                assertEquals("foo.bar.Bzaz$Bor$Foo",
                             qname.toString());
                assertEquals(3, qname.getClassList().size());
                break;
            default:
                break;
            }
        }
    }


    @Test
    public void testNestedEnum() {
        final String TEST = "package foo.bar; class Foo { enum Bzaz{HOO;}}";

        List<ASTEnumDeclaration> nodes = getNodes(ASTEnumDeclaration.class, TEST);

        for (ASTEnumDeclaration coid : nodes) {
            JavaTypeQualifiedName qname = coid.getQualifiedName();
            assertEquals("foo.bar.Foo$Bzaz", qname.toString());
            assertEquals(2, qname.getPackageList().size());
            assertEquals(2, qname.getClassList().size());
        }
    }


    @Test
    public void testEnum() {
        final String TEST = "package foo.bar; enum Bzaz{HOO;}";

        List<ASTEnumDeclaration> nodes = getNodes(ASTEnumDeclaration.class, TEST);

        for (ASTEnumDeclaration coid : nodes) {
            JavaTypeQualifiedName qname = coid.getQualifiedName();
            assertEquals("foo.bar.Bzaz", qname.toString());
            assertEquals(2, qname.getPackageList().size());
            assertEquals(1, qname.getClassList().size());
        }
    }


    @Test
    public void testEnumMethodMember() {
        final String TEST = "package foo.bar; enum Bzaz{HOO; void foo(){}}";

        List<ASTMethodDeclaration> nodes = getNodes(ASTMethodDeclaration.class, TEST);

        for (ASTMethodDeclaration coid : nodes) {
            JavaOperationQualifiedName qname = coid.getQualifiedName();
            assertEquals("foo.bar.Bzaz#foo()", qname.toString());
            assertEquals(2, qname.getClassName().getPackageList().size());
            assertEquals(1, qname.getClassName().getClassList().size());
            assertEquals("foo()", qname.getOperation());
        }
    }


    @Test
    public void testNestedEmptyPackage() {
        final String TEST = "class Bzaz{ class Bor{ class Foo{}}}";

        List<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            JavaTypeQualifiedName qname = coid.getQualifiedName();
            switch (coid.getImage()) {
            case "Foo":
                assertEquals("Bzaz$Bor$Foo",
                             qname.toString());
                assertTrue(qname.getPackageList().isEmpty());
                assertTrue(qname.isUnnamedPackage());
                assertEquals(3, qname.getClassList().size());
                break;
            default:
                break;
            }
        }
    }


    @Test
    public void testMethod() {
        final String TEST = "package bar; class Bzaz{ public void foo(){}}";

        List<ASTMethodDeclaration> nodes = getNodes(ASTMethodDeclaration.class, TEST);

        for (ASTMethodDeclaration declaration : nodes) {
            JavaOperationQualifiedName qname = declaration.getQualifiedName();
            assertEquals("bar.Bzaz#foo()", qname.toString());
            assertNotNull(qname.getOperation());
            assertEquals("foo()", qname.getOperation());

        }
    }


    @Test
    public void testConstructor() {
        final String TEST = "package bar; class Bzaz{ public Bzaz(){}}";

        List<ASTConstructorDeclaration> nodes = getNodes(ASTConstructorDeclaration.class, TEST);

        for (ASTConstructorDeclaration declaration : nodes) {
            JavaOperationQualifiedName qname = declaration.getQualifiedName();
            assertEquals("bar.Bzaz#Bzaz()",
                         qname.toString());
            assertNotNull(qname.getOperation());
            assertEquals("Bzaz()", qname.getOperation());

        }
    }


    @Test
    public void testConstructorWithParams() {
        final String TEST = "package bar; class Bzaz{ public Bzaz(int j, String k){}}";

        List<ASTConstructorDeclaration> nodes = getNodes(ASTConstructorDeclaration.class, TEST);

        for (ASTConstructorDeclaration declaration : nodes) {
            JavaOperationQualifiedName qname = declaration.getQualifiedName();
            assertEquals("bar.Bzaz#Bzaz(int, String)", qname.toString());
            assertNotNull(qname.getOperation());
            assertEquals("Bzaz(int, String)", qname.getOperation());

        }
    }


    @Test
    public void testConstructorOverload() {
        final String TEST = "package bar; class Bzaz{ public Bzaz(int j) {} public Bzaz(int j, String k){}}";

        List<ASTConstructorDeclaration> nodes = getNodes(ASTConstructorDeclaration.class, TEST);

        ASTConstructorDeclaration[] arr = nodes.toArray(new ASTConstructorDeclaration[2]);
        assertNotEquals(arr[0].getQualifiedName(), arr[1].getQualifiedName());
    }


    @Test
    public void testMethodOverload() {
        final String TEST = "package bar; class Bzaz{ public void foo(String j) {} "
                + "public void foo(int j){} public void foo(double k){}}";

        List<ASTMethodDeclaration> nodes = getNodes(ASTMethodDeclaration.class, TEST);

        ASTMethodDeclaration[] arr = nodes.toArray(new ASTMethodDeclaration[3]);
        assertNotEquals(arr[0].getQualifiedName(), arr[1].getQualifiedName());
        assertNotEquals(arr[1].getQualifiedName(), arr[2].getQualifiedName());
    }


    @Test
    public void testParseClass() {
        JavaTypeQualifiedName outer = (JavaTypeQualifiedName) QualifiedNameFactory.ofString("foo.bar.Bzaz");
        JavaTypeQualifiedName nested = (JavaTypeQualifiedName) QualifiedNameFactory.ofString("foo.bar.Bzaz$Bolg");

        assertEquals(1, outer.getClassList().size());
        assertEquals("Bzaz", outer.getClassList().get(0));

        assertEquals(2, nested.getClassList().size());
        assertEquals("Bzaz", nested.getClassList().get(0));
        assertEquals("Bolg", nested.getClassList().get(1));
    }


    @Test
    public void testParsePackages() {
        JavaTypeQualifiedName packs = (JavaTypeQualifiedName) QualifiedNameFactory.ofString("foo.bar.Bzaz$Bolg");
        JavaTypeQualifiedName nopacks = (JavaTypeQualifiedName) QualifiedNameFactory.ofString("Bzaz");

        assertNotNull(packs.getPackageList());
        assertEquals("foo", packs.getPackageList().get(0));
        assertEquals("bar", packs.getPackageList().get(1));

        assertTrue(nopacks.getPackageList().isEmpty());
    }


    @Test
    public void testParseOperation() {
        JavaOperationQualifiedName noparams = (JavaOperationQualifiedName) QualifiedNameFactory.ofString("foo.bar.Bzaz$Bolg#bar()");
        JavaOperationQualifiedName params = (JavaOperationQualifiedName) QualifiedNameFactory.ofString("foo.bar.Bzaz#bar(String, int)");

        assertEquals("bar()", noparams.getOperation());
        assertEquals("bar(String, int)", params.getOperation());
    }


    @Test
    public void testParseLocalClasses() {
        final String SIMPLE = "foo.bar.Bzaz$1Local";
        final String NESTED = "foo.Bar$1Local$Nested";
        JavaTypeQualifiedName simple = (JavaTypeQualifiedName) QualifiedNameFactory.ofString(SIMPLE);
        JavaTypeQualifiedName nested = (JavaTypeQualifiedName) QualifiedNameFactory.ofString(NESTED);

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

        JavaTypeQualifiedName simple = (JavaTypeQualifiedName) QualifiedNameFactory.ofString(SIMPLE);

        assertNotNull(simple);
        assertTrue(simple.isAnonymousClass());
        assertFalse(simple.isLocalClass());

        assertEquals("12", simple.getClassList().get(1));
        assertEquals("13", simple.getClassList().get(2));

        assertEquals(SIMPLE, simple.toString());
    }

    @Test
    public void testParseLambdaName() {
        final String IN_LAMBDA = "foo.bar.Bzaz$1Local#lambda$null$12";
        final String STATIC = "foo.bar.Bzaz#lambda$static$12";
        final String NEW = "foo.bar.Bzaz#lambda$new$1";
        final String IN_METHOD = "Bzaz#lambda$myMethod$4";

        for (String s : Arrays.asList(IN_LAMBDA, STATIC, NEW, IN_METHOD)) {
            JavaOperationQualifiedName qname = (JavaOperationQualifiedName) QualifiedNameFactory.ofString(s);
            assertNotNull(qname);
            assertTrue(qname.isLambda());
            assertEquals(s, qname.toString());
            assertEquals(qname, QualifiedNameFactory.ofString(qname.toString()));
        }
    }


    @Test
    public void testParseMalformed() {
        assertNull(QualifiedNameFactory.ofString(".foo.bar.Bzaz"));
        assertNull(QualifiedNameFactory.ofString("foo.bar."));
        assertNull(QualifiedNameFactory.ofString("foo.bar.Bzaz#foo"));
        assertNull(QualifiedNameFactory.ofString("foo.bar.Bzaz()"));
        assertNull(QualifiedNameFactory.ofString("foo.bar.Bzaz#foo(String,)"));
        assertNull(QualifiedNameFactory.ofString("foo.bar.Bzaz#foo(String , int)"));
        assertNull(QualifiedNameFactory.ofString("foo.bar.Bzaz#lambda$static$23(String)"));
        assertNull(QualifiedNameFactory.ofString("foo.bar.Bzaz#lambda$static$"));
    }


    @Test
    public void testSimpleLocalClass() {
        final String TEST = "package bar; class Boron { public void foo(String j) { class Local {} } }";

        List<ASTClassOrInterfaceDeclaration> classes
                = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        JavaQualifiedName qname = QualifiedNameFactory.ofString("bar.Boron$1Local");

        assertEquals(qname, classes.get(1).getQualifiedName());
    }


    @Test
    public void testLocalClassNameClash() {
        final String TEST = "package bar; class Bzaz{ void foo() { class Local {} } {// initializer\n class Local {}}}";

        List<ASTClassOrInterfaceDeclaration> classes
                = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        assertNotEquals(classes.get(1).getQualifiedName(), classes.get(2).getQualifiedName());

        assertEquals(QualifiedNameFactory.ofString("bar.Bzaz$1Local"), classes.get(1).getQualifiedName());
        assertEquals(QualifiedNameFactory.ofString("bar.Bzaz$2Local"), classes.get(2).getQualifiedName());
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
                = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        assertNotEquals(classes.get(1).getQualifiedName(), classes.get(2).getQualifiedName());

        assertEquals(QualifiedNameFactory.ofString("Bzaz$1Local"), classes.get(1).getQualifiedName());
        assertEquals(QualifiedNameFactory.ofString("Bzaz$1Local$Nested"), classes.get(2).getQualifiedName());
        assertEquals(QualifiedNameFactory.ofString("Bzaz$1Local$Nested$1InnerLocal"), classes.get(3).getQualifiedName());
    }


    @Test
    public void testAnonymousClass() {
        final String TEST
                = "class Bzaz{ void foo() { "
                + "  new Runnable() {"
                + "      public void run() {}"
                + "  };"
                + "}}";

        List<ASTAnonymousClassDeclaration> classes = getNodes(ASTAnonymousClassDeclaration.class, TEST);

        assertEquals(QualifiedNameFactory.ofString("Bzaz$1"), classes.get(0).getQualifiedName());
        assertFalse(classes.get(0).getQualifiedName().isLocalClass());
        assertTrue(classes.get(0).getQualifiedName().isAnonymousClass());
        assertTrue("1".equals(classes.get(0).getQualifiedName().getClassSimpleName()));
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

        List<ASTAnonymousClassDeclaration> classes = getNodes(ASTAnonymousClassDeclaration.class, TEST);

        assertNotEquals(classes.get(0), classes.get(1));
        assertEquals(QualifiedNameFactory.ofString("Bzaz$1"), classes.get(0).getQualifiedName());
        assertEquals(QualifiedNameFactory.ofString("Bzaz$2"), classes.get(1).getQualifiedName());
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

        List<ASTAnonymousClassDeclaration> classes = getNodes(ASTAnonymousClassDeclaration.class, TEST);

        assertNotEquals(classes.get(0), classes.get(1));
        assertEquals(QualifiedNameFactory.ofString("Bzaz$1"), classes.get(0).getQualifiedName());
        assertEquals(QualifiedNameFactory.ofString("Bzaz$1$1"), classes.get(1).getQualifiedName());
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

        List<ASTClassOrInterfaceDeclaration> classes = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        assertTrue(classes.get(1).isLocal());
        assertEquals(QualifiedNameFactory.ofString("Bzaz$1$1FooRunnable"), classes.get(1).getQualifiedName());
    }

    @Test
    public void testGetType() {
        JavaTypeQualifiedName qname = QualifiedNameFactory.ofClass(ASTAdditiveExpression.class);
        assertEquals(qname.getType(), ASTAdditiveExpression.class);
    }

}

