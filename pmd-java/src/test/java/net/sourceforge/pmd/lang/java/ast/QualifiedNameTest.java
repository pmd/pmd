/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Set;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.ParserTst;

/**
 * @author Clément Fournier
 */
public class QualifiedNameTest extends ParserTst {


    @Test
    public void testEmptyPackage() {
        final String TEST = "class Foo {}";
        Set<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class,
                                                             TEST);
        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            JavaQualifiedName qname = coid.getQualifiedName();
            assertEquals(".Foo", qname.toString());
            assertNull(qname.getPackages());
            assertEquals(1, qname.getClasses().length);
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
            assertEquals(2, qname.getPackages().length);
            assertEquals(1, qname.getClasses().length);
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
                assertEquals(3, qname.getClasses().length);
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
            assertEquals(2, qname.getPackages().length);
            assertEquals(2, qname.getClasses().length);
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
            assertEquals(2, qname.getPackages().length);
            assertEquals(1, qname.getClasses().length);
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
            assertEquals(2, qname.getPackages().length);
            assertEquals(1, qname.getClasses().length);
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
                assertEquals(".Bzaz$Bor$Foo",
                             qname.toString());
                assertNull(qname.getPackages());
                assertEquals(3, qname.getClasses().length);
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

        assertEquals(1, outer.getClasses().length);
        assertEquals("Bzaz", outer.getClasses()[0]);

        assertEquals(2, nested.getClasses().length);
        assertEquals("Bzaz", nested.getClasses()[0]);
        assertEquals("Bolg", nested.getClasses()[1]);
    }


    @Test
    public void testParsePackages() {
        JavaQualifiedName packs = JavaQualifiedName.ofString("foo.bar.Bzaz$Bolg");
        JavaQualifiedName nopacks = JavaQualifiedName.ofString(".Bzaz");

        assertNotNull(packs.getPackages());
        assertEquals("foo", packs.getPackages()[0]);
        assertEquals("bar", packs.getPackages()[1]);

        assertNull(nopacks.getPackages());
    }

    @Test
    public void testParseOperation() {
        JavaQualifiedName noparams = JavaQualifiedName.ofString("foo.bar.Bzaz$Bolg#bar()");
        JavaQualifiedName params = JavaQualifiedName.ofString("foo.bar.Bzaz#bar(String, int)");

        assertEquals("bar()", noparams.getOperation());
        assertEquals("bar(String, int)", params.getOperation());
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


}

