/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Set;

import net.sourceforge.pmd.lang.java.ParserTst;
import org.junit.Test;

import static net.sourceforge.pmd.lang.java.ast.QualifiableNode.LEFT_CLASS_SEP;
import static net.sourceforge.pmd.lang.java.ast.QualifiableNode.LEFT_PARAM_SEP;
import static net.sourceforge.pmd.lang.java.ast.QualifiableNode.METHOD_SEP;
import static net.sourceforge.pmd.lang.java.ast.QualifiableNode.NESTED_CLASS_SEP;
import static net.sourceforge.pmd.lang.java.ast.QualifiableNode.PACKAGE_SEP;
import static net.sourceforge.pmd.lang.java.ast.QualifiableNode.PARAMLIST_SEP;
import static net.sourceforge.pmd.lang.java.ast.QualifiableNode.RIGHT_PARAM_SEP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
            assertEquals(LEFT_CLASS_SEP + "Foo", qname.toString());
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
            assertEquals("foo" + PACKAGE_SEP + "bar" + LEFT_CLASS_SEP + "Bzaz", qname.toString());
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
                    assertEquals("foo" + PACKAGE_SEP + "bar" + LEFT_CLASS_SEP
                                    + "Bzaz" + NESTED_CLASS_SEP + "Bor" + NESTED_CLASS_SEP + "Foo",
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
                    assertEquals(LEFT_CLASS_SEP
                                    + "Bzaz" + NESTED_CLASS_SEP + "Bor" + NESTED_CLASS_SEP + "Foo",
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
            assertEquals("bar" + LEFT_CLASS_SEP
                            + "Bzaz" + METHOD_SEP + "foo" + LEFT_PARAM_SEP + RIGHT_PARAM_SEP,
                    qname.toString());
            assertNotNull(qname.getOperation());
            assertEquals("foo" + LEFT_PARAM_SEP + RIGHT_PARAM_SEP, qname.getOperation());

        }
    }

    @Test
    public void testConstructor() {
        final String TEST = "package bar; class Bzaz{ public Bzaz(){}}";


        Set<ASTConstructorDeclaration> nodes = getNodes(ASTConstructorDeclaration.class,
                TEST);

        for (ASTConstructorDeclaration declaration : nodes) {
            QualifiableNode.QualifiedName qname = declaration.getQualifiedName();
            assertEquals("bar" + LEFT_CLASS_SEP
                            + "Bzaz" + METHOD_SEP + "Bzaz" + LEFT_PARAM_SEP + RIGHT_PARAM_SEP,
                    qname.toString());
            assertNotNull(qname.getOperation());
            assertEquals("Bzaz" + LEFT_PARAM_SEP + RIGHT_PARAM_SEP, qname.getOperation());

        }
    }

    @Test
    public void testConstructorWithParams() {
        final String TEST = "package bar; class Bzaz{ public Bzaz(int j, String k){}}";


        Set<ASTConstructorDeclaration> nodes = getNodes(ASTConstructorDeclaration.class,
                TEST);

        for (ASTConstructorDeclaration declaration : nodes) {
            QualifiableNode.QualifiedName qname = declaration.getQualifiedName();
            assertEquals("bar" + LEFT_CLASS_SEP
                            + "Bzaz" + METHOD_SEP + "Bzaz" + LEFT_PARAM_SEP + "int" + PARAMLIST_SEP + "String" + RIGHT_PARAM_SEP,
                    qname.toString());
            assertNotNull(qname.getOperation());
            assertEquals("Bzaz" + LEFT_PARAM_SEP + "int" + PARAMLIST_SEP + "String" + RIGHT_PARAM_SEP, qname.getOperation());

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

        Set<ASTMethodDeclaration> nodes = getNodes(ASTMethodDeclaration.class,
                TEST);

        ASTMethodDeclaration[] arr = nodes.toArray(new ASTMethodDeclaration[3]);
        assertNotEquals(arr[0].getQualifiedName(), arr[1].getQualifiedName());
        assertNotEquals(arr[1].getQualifiedName(), arr[2].getQualifiedName());
    }
}

