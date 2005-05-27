/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTInitializer;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ASTCatchStatement;
import net.sourceforge.pmd.symboltable.Scope;
import net.sourceforge.pmd.symboltable.SymbolFacade;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.io.StringReader;
import java.util.Map;
import java.util.List;

public class AcceptanceTest extends STBBaseTst {

    public void testClashingSymbols() {
        parseCode(TEST1);
    }

    public void testInitializer() {
        parseCode(TEST_INITIALIZERS);
        ASTInitializer a = (ASTInitializer)(acu.findChildrenOfType(ASTInitializer.class)).get(0);
        assertFalse(a.isStatic());
        a = (ASTInitializer)(acu.findChildrenOfType(ASTInitializer.class)).get(1);
        assertTrue(a.isStatic());
    }

    public void testCatchBlocks() {
        parseCode(TEST_CATCH_BLOCKS);
        ASTCatchStatement c = (ASTCatchStatement)(acu.findChildrenOfType(ASTCatchStatement.class)).get(0);
        ASTBlock a = (ASTBlock)(c.findChildrenOfType(ASTBlock.class)).get(0);
        Scope s = a.getScope();
        Map vars = s.getParent().getVariableDeclarations();
        assertEquals(1, vars.size());
        VariableNameDeclaration v = (VariableNameDeclaration)vars.keySet().iterator().next();
        assertEquals("e", v.getImage());
        assertEquals(1, ((List)vars.get(v)).size());
    }

    private static final String TEST1 =
    "import java.io.*;" + PMD.EOL +
    "public class Foo  {" + PMD.EOL +
    " void buz( ) {" + PMD.EOL +
    "  Object o = new Serializable() { int x; };" + PMD.EOL +
    "  Object o1 = new Serializable() { int x; };" + PMD.EOL +
    " }" + PMD.EOL  +
    "}" + PMD.EOL;

    private static final String TEST_INITIALIZERS =
    "public class Foo  {" + PMD.EOL +
    " {} " + PMD.EOL +
    " static {} " + PMD.EOL +
    "}" + PMD.EOL;

    private static final String TEST_CATCH_BLOCKS =
    "public class Foo  {" + PMD.EOL +
    " void foo() { " + PMD.EOL +
    "  try { " + PMD.EOL +
    "  } catch (Exception e) { " + PMD.EOL +
    "   e.printStackTrace(); " + PMD.EOL +
    "  } " + PMD.EOL +
    " } " + PMD.EOL +
    "}" + PMD.EOL;

}
