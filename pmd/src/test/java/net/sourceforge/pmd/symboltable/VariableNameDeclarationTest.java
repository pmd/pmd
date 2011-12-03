/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symboltable.Scope;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;

import org.junit.Test;

import java.util.List;
public class VariableNameDeclarationTest extends STBBaseTst {

    @Test
    public void testConstructor() {
        parseCode(TEST1);
        List nodes = acu.findDescendantsOfType(ASTVariableDeclaratorId.class);
        Scope s = ((ASTVariableDeclaratorId) nodes.get(0)).getScope();
        VariableNameDeclaration decl = s.getVariableDeclarations().keySet().iterator().next();
        assertEquals("bar", decl.getImage());
        assertEquals(3, decl.getNode().getBeginLine());
    }

    @Test
    public void testExceptionBlkParam() {
        ASTVariableDeclaratorId id = new ASTVariableDeclaratorId(3);
        id.testingOnly__setBeginLine(10);
        id.setImage("foo");
        ASTFormalParameter param = new ASTFormalParameter(2);
        id.jjtSetParent(param);
        param.jjtSetParent(new ASTTryStatement(1));
        VariableNameDeclaration decl = new VariableNameDeclaration(id);
        assertTrue(decl.isExceptionBlockParameter());
    }

    @Test
    public void testIsArray() {
        parseCode(TEST3);
        VariableNameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope().getVariableDeclarations().keySet().iterator().next();
        assertTrue(decl.isArray());
    }

    @Test
    public void testPrimitiveType() {
        parseCode(TEST1);
        VariableNameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope().getVariableDeclarations().keySet().iterator().next();
        assertTrue(decl.isPrimitiveType());
    }

    @Test
    public void testArrayIsReferenceType() {
        parseCode(TEST3);
        VariableNameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope().getVariableDeclarations().keySet().iterator().next();
        assertTrue(decl.isReferenceType());
    }

    @Test
    public void testPrimitiveTypeImage() {
        parseCode(TEST3);
        VariableNameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope().getVariableDeclarations().keySet().iterator().next();
        assertEquals("int", decl.getTypeImage());
    }

    @Test
    public void testRefTypeImage() {
        parseCode(TEST4);
        VariableNameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope().getVariableDeclarations().keySet().iterator().next();
        assertEquals("String", decl.getTypeImage());
    }

    @Test
    public void testParamTypeImage() {
        parseCode(TEST5);
        VariableNameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope().getVariableDeclarations().keySet().iterator().next();
        assertEquals("String", decl.getTypeImage());
    }

    public static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  int bar = 42;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    public static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  try {} catch(Exception e) {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    public static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  int[] x;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    public static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  String x;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";
    public static final String TEST5 =
            "public class Foo {" + PMD.EOL +
            " void foo(String x) {}" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(VariableNameDeclarationTest.class);
    }
}
