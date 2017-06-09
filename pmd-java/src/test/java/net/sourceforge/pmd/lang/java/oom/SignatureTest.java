/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.ParserTst;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.visitor.FieldSignature;
import net.sourceforge.pmd.lang.java.oom.visitor.OperationSignature;
import net.sourceforge.pmd.lang.java.oom.visitor.OperationSignature.Role;
import net.sourceforge.pmd.lang.java.oom.visitor.Signature;
import net.sourceforge.pmd.lang.java.oom.visitor.Signature.Visibility;

/**
 * Test class for {@link net.sourceforge.pmd.lang.java.oom.visitor.Signature} and its subclasses.
 *
 * @author Clément Fournier
 */
public class SignatureTest extends ParserTst {

    // common to operation and field signatures
    @Test
    public void visibilityTest() {
        final String TEST = "class Bzaz{ " +
            "public int bar;" +
            "String k;" +
            "protected double d;" +
            "private int i;" +
            "protected int x;" +
            "public Bzaz(){} " +
            "void bar(){} " +
            "protected void foo(int x){}" +
            "private Bzaz(int y){}" +
            "}";


        List<ASTMethodOrConstructorDeclaration> operationDeclarations = getOrderedNodes(ASTMethodOrConstructorDeclaration.class, TEST);
        List<ASTFieldDeclaration> fieldDeclarations = getOrderedNodes(ASTFieldDeclaration.class, TEST);
        List<Signature> sigs = new ArrayList<>();

        for (ASTMethodOrConstructorDeclaration node : operationDeclarations) {
            sigs.add(OperationSignature.buildFor(node));
        }

        // operations
        assertEquals(Visibility.PUBLIC, sigs.get(0).visibility);
        assertEquals(Visibility.PACKAGE, sigs.get(1).visibility);
        assertEquals(Visibility.PROTECTED, sigs.get(2).visibility);
        assertEquals(Visibility.PRIVATE, sigs.get(3).visibility);

        sigs.clear();
        for (ASTFieldDeclaration node : fieldDeclarations) {
            sigs.add(FieldSignature.buildFor(node));
        }

        // fields
        assertEquals(Visibility.PUBLIC, sigs.get(0).visibility);
        assertEquals(Visibility.PACKAGE, sigs.get(1).visibility);
        assertEquals(Visibility.PROTECTED, sigs.get(2).visibility);
        assertEquals(Visibility.PRIVATE, sigs.get(3).visibility);
    }

    @Test
    public void roleTest() {
        final String TEST = "class Bzaz{ int x; " +
            "public static void foo(){} " +
            "Bzaz(){} " +
            "int getX(){return x;}" +
            " void setX(int a){x=a;}" +
            " public void doSomething(){}}";


        List<ASTMethodOrConstructorDeclaration> nodes = getOrderedNodes(ASTMethodOrConstructorDeclaration
            .class, TEST);
        List<OperationSignature> sigs = new ArrayList<>();

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            sigs.add(OperationSignature.buildFor(node));
        }

        assertEquals(Role.STATIC, sigs.get(0).role);
        assertEquals(Role.CONSTRUCTOR, sigs.get(1).role);
        assertEquals(Role.GETTER_OR_SETTER, sigs.get(2).role);
        assertEquals(Role.GETTER_OR_SETTER, sigs.get(3).role);
        assertEquals(Role.METHOD, sigs.get(4).role);
    }

    @Test
    public void isAbstractTest() {
        final String TEST = "abstract class Bzaz{ int x; " +
            "public static abstract void foo();" +
            "protected abstract int bar(int x);" +
            "int getX(){return x;}" +
            "void setX(int a){x=a;}" +
            "public void doSomething(){}}";


        List<ASTMethodOrConstructorDeclaration> nodes = getOrderedNodes(ASTMethodOrConstructorDeclaration
            .class, TEST);
        List<OperationSignature> sigs = new ArrayList<>();

        for (ASTMethodOrConstructorDeclaration node : nodes) {
            sigs.add(OperationSignature.buildFor(node));
        }


        assertTrue(sigs.get(0).isAbstract);
        assertTrue(sigs.get(1).isAbstract);
        assertFalse(sigs.get(2).isAbstract);
        assertFalse(sigs.get(3).isAbstract);
        assertFalse(sigs.get(4).isAbstract);
    }
}
