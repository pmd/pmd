/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSignature;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSignature;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSignature.Role;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaSignature.Visibility;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;
import net.sourceforge.pmd.lang.metrics.SigMask;

/**
 * @author Clément Fournier
 */
public class SigMaskTest extends BaseNonParserTest {

    private static final String TEST_FIELDS = "class Bzaz{"
        + "public String x;"
        + "private int y;"
        + "protected String z;"
        + "int s;"
        + "public final int t;"
        + "private final int a;"
        + "protected final double u;"
        + "final long v;"
        + "static int aa;"
        + "static final int ab;"
        + "private static int ac;"
        + "protected static final int ad;"
        + "public static int ag;"
        + "}";

    private static final String TEST_OPERATIONS = "abstract class Bzaz{ "
        + "int x;"
        + "int y;"
        + "int z;"
        // constructors
        + "public Bzaz() {}"
        + "private Bzaz(int x){}"
        + "protected Bzaz(int x, String y){}"
        // static
        + "public static void main(String[] args){}"
        + "protected static void makeFoo(){}"
        + "private static void makeBar(){}"
        // getters and setters
        + "public int getX(){return 2;}"
        + "int getY(){return 0;}"
        + "protected void setY(int y){}"
        + "private void setX(int x){}"
        // methods
        + "public void foo(){} "
        + "void bar(){} "
        + "protected void foo(int x){} "
        + "private void rand(){}"
        // abstract
        + "protected abstract int getZ();"
        + "abstract int abs2();"
        + "public static abstract String abstr();"
        + "abstract void setZ(int x);"
        + "}";

    /**
     * Ensure any non-abstract method is covered by a newly created mask.
     */
    @Test
    public void testEmptyOperationMask() {
        List<ASTMethodOrConstructorDeclaration> nodes = getOrderedNodes(ASTMethodOrConstructorDeclaration.class, TEST_OPERATIONS);
        SigMask<JavaOperationSignature> mask = new JavaOperationSigMask();

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            if (node.isAbstract()) {
                assertFalse(mask.covers(JavaOperationSignature.buildFor(node)));
            } else {
                assertTrue(mask.covers(JavaOperationSignature.buildFor(node)));
            }
        }
    }

    /**
     * Ensure any field is covered by a newly created mask.
     */
    @Test
    public void testEmptyFieldMask() {
        List<ASTFieldDeclaration> nodes = getOrderedNodes(ASTFieldDeclaration.class, TEST_FIELDS);
        SigMask<JavaFieldSignature> mask = new JavaFieldSigMask();

        for (ASTFieldDeclaration node : nodes) {
            assertTrue(mask.covers(JavaFieldSignature.buildFor(node)));
        }
    }

    @Test
    public void testFinalFields() {
        List<ASTFieldDeclaration> nodes = getOrderedNodes(ASTFieldDeclaration.class, TEST_FIELDS);
        JavaFieldSigMask mask = new JavaFieldSigMask();
        mask.forbidFinal();

        for (ASTFieldDeclaration node : nodes) {
            if (node.isFinal()) {
                assertFalse(mask.covers(JavaFieldSignature.buildFor(node)));
            } else {
                assertTrue(mask.covers(JavaFieldSignature.buildFor(node)));
            }
        }
    }

    @Test
    public void testStaticFields() {
        List<ASTFieldDeclaration> nodes = getOrderedNodes(ASTFieldDeclaration.class, TEST_FIELDS);
        JavaFieldSigMask mask = new JavaFieldSigMask();
        mask.forbidStatic();

        for (ASTFieldDeclaration node : nodes) {
            if (node.isStatic()) {
                assertFalse(mask.covers(JavaFieldSignature.buildFor(node)));
            } else {
                assertTrue(mask.covers(JavaFieldSignature.buildFor(node)));
            }
        }
    }

    @Test
    public void testFieldvisibility() {
        List<ASTFieldDeclaration> nodes = getOrderedNodes(ASTFieldDeclaration.class, TEST_FIELDS);
        JavaFieldSigMask mask = new JavaFieldSigMask();

        mask.restrictVisibilitiesTo(Visibility.PUBLIC);

        for (ASTFieldDeclaration node : nodes) {
            if (node.isPublic()) {
                assertTrue(mask.covers(JavaFieldSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(JavaFieldSignature.buildFor(node)));
            }
        }

        mask.restrictVisibilitiesTo(Visibility.PRIVATE);

        for (ASTFieldDeclaration node : nodes) {
            if (node.isPrivate()) {
                assertTrue(mask.covers(JavaFieldSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(JavaFieldSignature.buildFor(node)));
            }
        }

        mask.restrictVisibilitiesTo(Visibility.PACKAGE);

        for (ASTFieldDeclaration node : nodes) {
            if (node.isPackagePrivate()) {
                assertTrue(mask.covers(JavaFieldSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(JavaFieldSignature.buildFor(node)));
            }
        }

        mask.restrictVisibilitiesTo(Visibility.PROTECTED);

        for (ASTFieldDeclaration node : nodes) {
            if (node.isProtected()) {
                assertTrue(mask.covers(JavaFieldSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(JavaFieldSignature.buildFor(node)));
            }
        }

    }


    @Test
    public void testOperationVisibility() {
        List<ASTMethodOrConstructorDeclaration> nodes = getOrderedNodes(ASTMethodOrConstructorDeclaration.class, TEST_OPERATIONS);

        JavaOperationSigMask mask = new JavaOperationSigMask();
        mask.coverAbstract();

        mask.restrictVisibilitiesTo(Visibility.PUBLIC);

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            if (node.isPublic()) {
                assertTrue(mask.covers(JavaOperationSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(JavaOperationSignature.buildFor(node)));
            }
        }

        mask.restrictVisibilitiesTo(Visibility.PRIVATE);

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            if (node.isPrivate()) {
                assertTrue(mask.covers(JavaOperationSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(JavaOperationSignature.buildFor(node)));
            }
        }

        mask.restrictVisibilitiesTo(Visibility.PACKAGE);

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            if (node.isPackagePrivate()) {
                assertTrue(mask.covers(JavaOperationSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(JavaOperationSignature.buildFor(node)));
            }
        }

        mask.restrictVisibilitiesTo(Visibility.PROTECTED);

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            if (node.isProtected()) {
                assertTrue(mask.covers(JavaOperationSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(JavaOperationSignature.buildFor(node)));
            }
        }
    }

    @Test
    public void testOperationRoles() {
        List<ASTMethodOrConstructorDeclaration> nodes = getOrderedNodes(ASTMethodOrConstructorDeclaration.class, TEST_OPERATIONS);
        JavaOperationSigMask mask = new JavaOperationSigMask();
        mask.restrictRolesTo(Role.STATIC);
        mask.coverAbstract();

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            if (node.isStatic()) {
                assertTrue(mask.covers(JavaOperationSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(JavaOperationSignature.buildFor(node)));
            }
        }

        mask.restrictRolesTo(Role.CONSTRUCTOR);

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            if (node instanceof ASTConstructorDeclaration) {
                assertTrue(mask.covers(JavaOperationSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(JavaOperationSignature.buildFor(node)));
            }
        }

        mask.restrictRolesTo(Role.GETTER_OR_SETTER);

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            if (node instanceof ASTMethodDeclaration
                && ((ASTMethodDeclaration) node).getName().matches("(get|set).*")) {
                assertTrue(mask.covers(JavaOperationSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(JavaOperationSignature.buildFor(node)));
            }
        }

        mask.restrictRolesTo(Role.METHOD);

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            if (node instanceof ASTMethodDeclaration
                && !node.isStatic()
                && !((ASTMethodDeclaration) node).getName().matches("(get|set).*")) {
                assertTrue(mask.covers(JavaOperationSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(JavaOperationSignature.buildFor(node)));
            }
        }

    }
}
