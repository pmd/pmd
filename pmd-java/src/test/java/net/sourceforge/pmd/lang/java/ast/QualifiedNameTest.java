/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Set;

import net.sourceforge.pmd.lang.java.ParserTst;
import org.junit.Test;

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
            QualifiableNode.QualifiedName qname = coid.getQualifiedName();
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
            QualifiableNode.QualifiedName qname = coid.getQualifiedName();
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
            QualifiableNode.QualifiedName qname = coid.getQualifiedName();
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
    public void testNestedEmptyPackage() {
        final String TEST = "class Bzaz{ class Bor{ class Foo{}}}";


        Set<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class,
                TEST);

        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            QualifiableNode.QualifiedName qname = coid.getQualifiedName();
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
            QualifiableNode.QualifiedName qname = declaration.getQualifiedName();
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
            QualifiableNode.QualifiedName qname = declaration.getQualifiedName();
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
            QualifiableNode.QualifiedName qname = declaration.getQualifiedName();
            assertEquals("bar.Bzaz#Bzaz(int,String)", qname.toString());
            assertNotNull(qname.getOperation());
            assertEquals("Bzaz(int,String)", qname.getOperation());

        }
    }

    @Test
    public void testEquals() {
        final String TEST = "package bar; class Bzaz{ public foo() {} public Bzaz(int j, String k){}}";

        Set<ASTConstructorDeclaration> nodes = getNodes(ASTConstructorDeclaration.class,
                TEST);

        for (ASTConstructorDeclaration declaration : nodes) {
            QualifiableNode.QualifiedName qname = declaration.getQualifiedName();
            assertEquals(qname, qname.toString());
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
        final String TEST = "package bar; class Bzaz{ public void foo(String j) {} " +
                "public void foo(int j){} public void foo(double k){}}";

        Set<ASTMethodDeclaration> nodes = getNodes(ASTMethodDeclaration.class, TEST);

        ASTMethodDeclaration[] arr = nodes.toArray(new ASTMethodDeclaration[3]);
        assertNotEquals(arr[0].getQualifiedName(), arr[1].getQualifiedName());
        assertNotEquals(arr[1].getQualifiedName(), arr[2].getQualifiedName());
    }
}

