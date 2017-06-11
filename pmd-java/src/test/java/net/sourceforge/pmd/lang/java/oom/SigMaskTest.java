/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.ParserTst;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.signature.FieldSigMask;
import net.sourceforge.pmd.lang.java.oom.signature.FieldSignature;
import net.sourceforge.pmd.lang.java.oom.signature.OperationSigMask;
import net.sourceforge.pmd.lang.java.oom.signature.OperationSignature;
import net.sourceforge.pmd.lang.java.oom.signature.OperationSignature.Role;
import net.sourceforge.pmd.lang.java.oom.signature.SigMask;
import net.sourceforge.pmd.lang.java.oom.signature.Signature.Visibility;

/**
 * @author Clément Fournier
 */
public class SigMaskTest extends ParserTst {

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
        + "protected abstract int getXAbs();"
        + "abstract int abs2();"
        + "public static abstract String abstr();"
        + "abstract int setXAbs();"
        + "}";

    /**
     * Ensure any non-abstract method is covered by a newly created mask.
     */
    @Test
    public void testEmptyOperationMask() {
        List<ASTMethodOrConstructorDeclaration> nodes = getOrderedNodes(ASTMethodOrConstructorDeclaration.class, TEST_OPERATIONS);
        SigMask<OperationSignature> mask = new OperationSigMask();

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            if (node.isAbstract()) {
                assertFalse(mask.covers(OperationSignature.buildFor(node)));
            } else {
                assertTrue(mask.covers(OperationSignature.buildFor(node)));
            }
        }
    }

    /**
     * Ensure any field is covered by a newly created mask.
     */
    @Test
    public void testEmptyFieldMask() {
        List<ASTFieldDeclaration> nodes = getOrderedNodes(ASTFieldDeclaration.class, TEST_FIELDS);
        SigMask<FieldSignature> mask = new FieldSigMask();

        for (ASTFieldDeclaration node : nodes) {
            assertTrue(mask.covers(FieldSignature.buildFor(node)));
        }
    }

    @Test
    public void testFinalFields() {
        List<ASTFieldDeclaration> nodes = getOrderedNodes(ASTFieldDeclaration.class, TEST_FIELDS);
        FieldSigMask mask = new FieldSigMask();
        mask.coverFinal(false);

        for (ASTFieldDeclaration node : nodes) {
            if (node.isFinal()) {
                assertFalse(mask.covers(FieldSignature.buildFor(node)));
            } else {
                assertTrue(mask.covers(FieldSignature.buildFor(node)));
            }
        }
    }

    @Test
    public void testStaticFields() {
        List<ASTFieldDeclaration> nodes = getOrderedNodes(ASTFieldDeclaration.class, TEST_FIELDS);
        FieldSigMask mask = new FieldSigMask();
        mask.coverStatic(false);

        for (ASTFieldDeclaration node : nodes) {
            if (node.isStatic()) {
                assertFalse(mask.covers(FieldSignature.buildFor(node)));
            } else {
                assertTrue(mask.covers(FieldSignature.buildFor(node)));
            }
        }
    }

    @Test
    public void testFieldvisibility() {
        List<ASTFieldDeclaration> nodes = getOrderedNodes(ASTFieldDeclaration.class, TEST_FIELDS);
        FieldSigMask mask = new FieldSigMask();

        mask.restrictVisibilitiesTo(Visibility.PUBLIC);

        for (ASTFieldDeclaration node : nodes) {
            if (node.isPublic()) {
                assertTrue(mask.covers(FieldSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(FieldSignature.buildFor(node)));
            }
        }

        mask.restrictVisibilitiesTo(Visibility.PRIVATE);

        for (ASTFieldDeclaration node : nodes) {
            if (node.isPrivate()) {
                assertTrue(mask.covers(FieldSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(FieldSignature.buildFor(node)));
            }
        }

        mask.restrictVisibilitiesTo(Visibility.PACKAGE);

        for (ASTFieldDeclaration node : nodes) {
            if (node.isPackagePrivate()) {
                assertTrue(mask.covers(FieldSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(FieldSignature.buildFor(node)));
            }
        }

        mask.restrictVisibilitiesTo(Visibility.PROTECTED);

        for (ASTFieldDeclaration node : nodes) {
            if (node.isProtected()) {
                assertTrue(mask.covers(FieldSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(FieldSignature.buildFor(node)));
            }
        }
        
    }


    @Test
    public void testOperationVisibility() {
        List<ASTMethodOrConstructorDeclaration> nodes = getOrderedNodes(ASTMethodOrConstructorDeclaration.class,
                                                                        TEST_OPERATIONS);

        OperationSigMask mask = new OperationSigMask();
        mask.coverAbstract(true);

        mask.restrictVisibilitiesTo(Visibility.PUBLIC);

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            if (node.isPublic()) {
                assertTrue(mask.covers(OperationSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(OperationSignature.buildFor(node)));
            }
        }

        mask.restrictVisibilitiesTo(Visibility.PRIVATE);

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            if (node.isPrivate()) {
                assertTrue(mask.covers(OperationSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(OperationSignature.buildFor(node)));
            }
        }

        mask.restrictVisibilitiesTo(Visibility.PACKAGE);

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            if (node.isPackagePrivate()) {
                assertTrue(mask.covers(OperationSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(OperationSignature.buildFor(node)));
            }
        }

        mask.restrictVisibilitiesTo(Visibility.PROTECTED);

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            if (node.isProtected()) {
                assertTrue(mask.covers(OperationSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(OperationSignature.buildFor(node)));
            }
        }
    }

    @Test
    public void testOperationRoles() {
        List<ASTMethodOrConstructorDeclaration> nodes = getOrderedNodes(ASTMethodOrConstructorDeclaration.class,
                                                                        TEST_OPERATIONS);
        OperationSigMask mask = new OperationSigMask();
        mask.restrictRolesTo(Role.STATIC);
        mask.coverAbstract(true);

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            if (node.isStatic()) {
                assertTrue(mask.covers(OperationSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(OperationSignature.buildFor(node)));
            }
        }

        mask.restrictRolesTo(Role.CONSTRUCTOR);

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            if (node instanceof ASTConstructorDeclaration) {
                assertTrue(mask.covers(OperationSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(OperationSignature.buildFor(node)));
            }
        }

        mask.restrictRolesTo(Role.GETTER_OR_SETTER);

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            if (node instanceof ASTMethodDeclaration
                && ((ASTMethodDeclaration) node).getMethodName().matches("(get|set).*")) {
                assertTrue(mask.covers(OperationSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(OperationSignature.buildFor(node)));
            }
        }

        mask.restrictRolesTo(Role.METHOD);

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            if (node instanceof ASTMethodDeclaration
                && !node.isStatic()
                && !((ASTMethodDeclaration) node).getMethodName().matches("(get|set).*")) {
                assertTrue(mask.covers(OperationSignature.buildFor(node)));
            } else {
                assertFalse(mask.covers(OperationSignature.buildFor(node)));
            }
        }

    }
}
