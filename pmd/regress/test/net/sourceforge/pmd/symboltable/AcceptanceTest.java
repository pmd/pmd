/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTInitializer;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.symboltable.SymbolFacade;
import net.sourceforge.pmd.symboltable.Scope;

import java.io.StringReader;

public class AcceptanceTest extends TestCase {

    public void testClashingSymbols() {
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(TEST1));
        ASTCompilationUnit c = parser.CompilationUnit();
        SymbolFacade stb = new SymbolFacade();
        stb.initializeWith(c);
    }

    public void testInitializer() {
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(TEST_NON_STATIC_INITIALIZER));
        ASTCompilationUnit c = parser.CompilationUnit();
        ASTInitializer a = (ASTInitializer)(c.findChildrenOfType(ASTInitializer.class)).get(0);
        assertFalse(a.isStatic());
    }

    public void testStaticInitializer() {
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(TEST_STATIC_INITIALIZER));
        ASTCompilationUnit c = parser.CompilationUnit();
        ASTInitializer a = (ASTInitializer)(c.findChildrenOfType(ASTInitializer.class)).get(0);
        assertTrue(a.isStatic());
    }

    public void testCatchBlocks() {
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(TEST_CATCH_BLOCKS));
        ASTCompilationUnit c = parser.CompilationUnit();
        SymbolFacade stb = new SymbolFacade();
        stb.initializeWith(c);
        ASTBlock a = (ASTBlock)(c.findChildrenOfType(ASTBlock.class)).get(1);
        Scope s = a.getScope();
        System.out.println(s.getParent());


    }

    private static final String TEST1 =
    "import java.io.*;" + PMD.EOL +
    "public class Foo  {" + PMD.EOL +
    " void buz( ) {" + PMD.EOL +
    "  Object o = new Serializable() { int x; };" + PMD.EOL +
    "  Object o1 = new Serializable() { int x; };" + PMD.EOL +
    " }" + PMD.EOL  +
    "}" + PMD.EOL;

    private static final String TEST_NON_STATIC_INITIALIZER =
    "public class Foo  {" + PMD.EOL +
    " {} " + PMD.EOL +
    "}" + PMD.EOL;

    private static final String TEST_STATIC_INITIALIZER =
    "public class Foo  {" + PMD.EOL +
    " static {} " + PMD.EOL +
    "}" + PMD.EOL;

    private static final String TEST_CATCH_BLOCKS =
    "public class Foo  {" + PMD.EOL +
    " void foo() { " + PMD.EOL +
    "  try { " + PMD.EOL +
    "  } catch (Exception e) { " + PMD.EOL +
    "   e.printStackTrace(); " + PMD.EOL +
    "   int x; " + PMD.EOL +
    "  } " + PMD.EOL +
    " } " + PMD.EOL +
    "}" + PMD.EOL;

}
