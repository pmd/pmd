/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.ParserTst;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.oom.signature.FieldSignature;
import net.sourceforge.pmd.lang.java.oom.signature.OperationSignature;
import net.sourceforge.pmd.lang.java.oom.signature.OperationSignature.Role;
import net.sourceforge.pmd.lang.java.oom.signature.Signature;
import net.sourceforge.pmd.lang.java.oom.signature.Signature.Visibility;
import net.sourceforge.pmd.lang.java.oom.testdata.GetterDetection;
import net.sourceforge.pmd.lang.java.oom.testdata.SetterDetection;
import net.sourceforge.pmd.typeresolution.ClassTypeResolverTest;

/**
 * Test class for {@link Signature} and its subclasses.
 *
 * @author Clément Fournier
 */
public class SignatureTest extends ParserTst {

    // common to operation and field signatures
    @Test
    public void visibilityTest() {
        final String TEST = "class Bzaz{ "
            + "public int bar;"
            + "String k;"
            + "protected double d;"
            + "private int i;"
            + "protected int x;"
            + "public Bzaz(){} "
            + "void bar(){} "
            + "protected void foo(int x){}"
            + "private Bzaz(int y){}"
            + "}";


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
    public void operationRoleTest() {
        final String TEST = "class Bzaz{ int x; "
            + "public static void foo(){} "
            + "Bzaz(){} "
            + "int getX(){return x;}"
            + " void setX(int a){x=a;}"
            + " public void doSomething(){}}";


        List<ASTMethodOrConstructorDeclaration> nodes = getOrderedNodes(ASTMethodOrConstructorDeclaration.class, TEST);
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
    public void testGetterDetection() {
        ASTCompilationUnit compilationUnit = parseClass(GetterDetection.class);

        compilationUnit.jjtAccept(new JavaParserVisitorAdapter() {
            @Override
            public Object visit(ASTMethodDeclaration node, Object data) {
                assertEquals(Role.GETTER_OR_SETTER, Role.get(node));
                return data;
            }
        }, null);
    }

    @Test
    public void testSetterDetection() {
        ASTCompilationUnit compilationUnit = parseClass(SetterDetection.class);

        compilationUnit.jjtAccept(new JavaParserVisitorAdapter() {
            @Override
            public Object visit(ASTMethodDeclaration node, Object data) {
                System.err.println(node.getMethodName());
                assertEquals(Role.GETTER_OR_SETTER, Role.get(node));
                return data;
            }
        }, null);
    }


    @Test
    public void isAbstractOperationTest() {
        final String TEST = "abstract class Bzaz{ int x; "
            + "public static abstract void foo();"
            + "protected abstract int bar(int x);"
            + "int getX(){return x;}"
            + "void setX(int a){x=a;}"
            + "public void doSomething(){}}";


        List<ASTMethodOrConstructorDeclaration> nodes = getOrderedNodes(ASTMethodOrConstructorDeclaration.class, TEST);
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

    @Test
    public void isFinalFieldTest() {
        final String TEST = "class Bzaz{"
            + "public String x;"
            + "private int y;"
            + "private final int a;"
            + "protected final double u;"
            + "final long v;"
            + "}";

        List<ASTFieldDeclaration> nodes = getOrderedNodes(ASTFieldDeclaration.class, TEST);
        List<FieldSignature> sigs = new ArrayList<>();

        for (ASTFieldDeclaration node : nodes) {
            sigs.add(FieldSignature.buildFor(node));
        }

        assertFalse(sigs.get(0).isFinal);
        assertFalse(sigs.get(1).isFinal);
        assertTrue(sigs.get(2).isFinal);
        assertTrue(sigs.get(3).isFinal);
        assertTrue(sigs.get(4).isFinal);
    }

    @Test
    public void isStaticFieldTest() {
        final String TEST = "class Bzaz{"
            + "public final String x;"
            + "private int y;"
            + "private static int a;"
            + "protected static final double u;"
            + "static long v;"
            + "}";

        List<ASTFieldDeclaration> nodes = getOrderedNodes(ASTFieldDeclaration.class, TEST);
        List<FieldSignature> sigs = new ArrayList<>();

        for (ASTFieldDeclaration node : nodes) {
            sigs.add(FieldSignature.buildFor(node));
        }

        assertFalse(sigs.get(0).isStatic);
        assertFalse(sigs.get(1).isStatic);
        assertTrue(sigs.get(2).isStatic);
        assertTrue(sigs.get(3).isStatic);
        assertTrue(sigs.get(4).isStatic);
    }

    // Ensure only one instance of a signature is created.
    @Test
    public void operationPoolTest() {
        final String TEST = "class Bzaz{ "
            + "public static void foo(){} "
            + "public static void az(){} "
            + "public static int getX(){return x;}}";

        final String TEST2 = "class Bzaz{ "
            + "void foo(){} "
            + "void az(){} "
            + "int rand(){return x;}}";


        List<ASTMethodOrConstructorDeclaration> nodes = getOrderedNodes(ASTMethodOrConstructorDeclaration.class, TEST);
        List<ASTMethodOrConstructorDeclaration> nodes2 = getOrderedNodes(ASTMethodOrConstructorDeclaration.class, TEST2);

        List<OperationSignature> sigs = new ArrayList<>();
        List<OperationSignature> sigs2 = new ArrayList<>();

        for (int i = 0; i < sigs.size(); i++) {
            sigs.add(OperationSignature.buildFor(nodes.get(i)));
            sigs2.add(OperationSignature.buildFor(nodes2.get(i)));

        }

        for (int i = 0; i < sigs.size() - 1; i++) {
            assertTrue(sigs.get(i) == sigs.get(i + 1));
            assertTrue(sigs2.get(i) == sigs2.get(i + 1));
        }
    }

    // Ensure only one instance of a signature is created.
    @Test
    public void fieldPoolTest() {
        final String TEST = "class Bzaz {"
            + "public int bar;"
            + "public String k;"
            + "public double d;"
            + "}";

        final String TEST2 = "class Foo {"
            + "private final int i;"
            + "private final int x;"
            + "private final String k;"
            + "}";


        List<ASTFieldDeclaration> nodes = getOrderedNodes(ASTFieldDeclaration.class, TEST);
        List<ASTFieldDeclaration> nodes2 = getOrderedNodes(ASTFieldDeclaration.class, TEST2);

        List<FieldSignature> sigs = new ArrayList<>();
        List<FieldSignature> sigs2 = new ArrayList<>();

        for (int i = 0; i < sigs.size(); i++) {
            sigs.add(FieldSignature.buildFor(nodes.get(i)));
            sigs2.add(FieldSignature.buildFor(nodes2.get(i)));

        }

        for (int i = 0; i < sigs.size() - 1; i++) {
            assertTrue(sigs.get(i) == sigs.get(i + 1));
            assertTrue(sigs2.get(i) == sigs2.get(i + 1));
        }
    }

    private ASTCompilationUnit parseClass(Class<?> clazz) {
        String sourceFile = clazz.getName().replace('.', '/') + ".java";
        InputStream is = ClassTypeResolverTest.class.getClassLoader().getResourceAsStream(sourceFile);
        if (is == null) {
            throw new IllegalArgumentException(
                "Unable to find source file " + sourceFile + " for " + clazz);
        }
        String source;
        try {
            source = IOUtils.toString(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return parseJava17(source);
    }


}
