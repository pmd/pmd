/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import static net.sourceforge.pmd.lang.java.ParserTstUtil.getNodes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class VariableNameDeclarationTest extends STBBaseTst {

    @Test
    public void testConstructor() {
        parseCode(TEST1);
        List<ASTVariableDeclaratorId> nodes = acu.findDescendantsOfType(ASTVariableDeclaratorId.class);
        Scope s = nodes.get(0).getScope();
        NameDeclaration decl = s.getDeclarations().keySet().iterator().next();
        assertEquals("bar", decl.getImage());
        assertEquals(3, decl.getNode().getBeginLine());
    }

    @Test
    public void testExceptionBlkParam() {
        ASTCompilationUnit acu = getNodes(ASTCompilationUnit.class, EXCEPTION_PARAMETER).iterator().next();
        ASTVariableDeclaratorId id = acu.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        assertTrue(new VariableNameDeclaration(id).isExceptionBlockParameter());
    }

    @Test
    public void testIsArray() {
        parseCode(TEST3);
        VariableNameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope()
                .getDeclarations(VariableNameDeclaration.class).keySet().iterator().next();
        assertTrue(decl.isArray());
    }

    @Test
    public void testPrimitiveType() {
        parseCode(TEST1);
        VariableNameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope()
                .getDeclarations(VariableNameDeclaration.class).keySet().iterator().next();
        assertTrue(decl.isPrimitiveType());
    }

    @Test
    public void testArrayIsReferenceType() {
        parseCode(TEST3);
        VariableNameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope()
                .getDeclarations(VariableNameDeclaration.class).keySet().iterator().next();
        assertTrue(decl.isReferenceType());
    }

    @Test
    public void testPrimitiveTypeImage() {
        parseCode(TEST3);
        NameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope()
                .getDeclarations().keySet().iterator().next();
        assertEquals("int", ((TypedNameDeclaration) decl).getTypeImage());
    }

    @Test
    public void testRefTypeImage() {
        parseCode(TEST4);
        NameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope()
                .getDeclarations().keySet().iterator().next();
        assertEquals("String", ((TypedNameDeclaration) decl).getTypeImage());
    }

    @Test
    public void testParamTypeImage() {
        parseCode(TEST5);
        NameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope()
                .getDeclarations().keySet().iterator().next();
        assertEquals("String", ((TypedNameDeclaration) decl).getTypeImage());
    }


    private static final String EXCEPTION_PARAMETER = "public class Test { { try {} catch(Exception ie) {} } }";

    public static final String TEST1 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  int bar = 42;"
            + PMD.EOL + " }" + PMD.EOL + "}";

    public static final String TEST2 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL
            + "  try {} catch(Exception e) {}" + PMD.EOL + " }" + PMD.EOL + "}";

    public static final String TEST3 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  int[] x;"
            + PMD.EOL + " }" + PMD.EOL + "}";

    public static final String TEST4 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  String x;"
            + PMD.EOL + " }" + PMD.EOL + "}";
    public static final String TEST5 = "public class Foo {" + PMD.EOL + " void foo(String x) {}" + PMD.EOL + "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(VariableNameDeclarationTest.class);
    }
}
