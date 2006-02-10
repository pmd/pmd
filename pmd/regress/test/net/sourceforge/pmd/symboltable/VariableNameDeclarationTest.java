/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.symboltable.Scope;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.util.List;

public class VariableNameDeclarationTest extends STBBaseTst {

    public void testConstructor() {
        parseCode(TEST1);
        List nodes = acu.findChildrenOfType(ASTVariableDeclaratorId.class);
        Scope s = ((ASTVariableDeclaratorId) nodes.get(0)).getScope();
        VariableNameDeclaration decl = (VariableNameDeclaration) s.getVariableDeclarations().keySet().iterator().next();
        assertEquals("bar", decl.getImage());
        assertEquals(3, decl.getNode().getBeginLine());
    }

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

    public void testIsArray() {
        parseCode(TEST3);
        VariableNameDeclaration decl = (VariableNameDeclaration) ((ASTVariableDeclaratorId) acu.findChildrenOfType(ASTVariableDeclaratorId.class).get(0)).getScope().getVariableDeclarations().keySet().iterator().next();
        assertTrue(decl.isArray());
    }

    public void testPrimitiveType() {
        parseCode(TEST1);
        VariableNameDeclaration decl = (VariableNameDeclaration) ((ASTVariableDeclaratorId) acu.findChildrenOfType(ASTVariableDeclaratorId.class).get(0)).getScope().getVariableDeclarations().keySet().iterator().next();
        assertTrue(decl.isPrimitiveType());
    }

    public void testArrayIsReferenceType() {
        parseCode(TEST3);
        VariableNameDeclaration decl = (VariableNameDeclaration) ((ASTVariableDeclaratorId) acu.findChildrenOfType(ASTVariableDeclaratorId.class).get(0)).getScope().getVariableDeclarations().keySet().iterator().next();
        assertTrue(decl.isReferenceType());
    }

    public void testPrimitiveTypeImage() {
        parseCode(TEST3);
        VariableNameDeclaration decl = (VariableNameDeclaration) ((ASTVariableDeclaratorId) acu.findChildrenOfType(ASTVariableDeclaratorId.class).get(0)).getScope().getVariableDeclarations().keySet().iterator().next();
        assertEquals("int", decl.getTypeImage());
    }

    public void testRefTypeImage() {
        parseCode(TEST4);
        VariableNameDeclaration decl = (VariableNameDeclaration) ((ASTVariableDeclaratorId) acu.findChildrenOfType(ASTVariableDeclaratorId.class).get(0)).getScope().getVariableDeclarations().keySet().iterator().next();
        assertEquals("String", decl.getTypeImage());
    }

    public void testParamTypeImage() {
        parseCode(TEST5);
        VariableNameDeclaration decl = (VariableNameDeclaration) ((ASTVariableDeclaratorId) acu.findChildrenOfType(ASTVariableDeclaratorId.class).get(0)).getScope().getVariableDeclarations().keySet().iterator().next();
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
}
