package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.symboltable.SymbolFacade;

import java.io.StringReader;

public class AcceptanceTest extends TestCase {

    private static final String TEST1 =
    "import java.io.*;" + PMD.EOL +
    "public class Foo  {" + PMD.EOL +
    " void buz( ) {" + PMD.EOL +
    "  Object o = new Serializable() { int x; };" + PMD.EOL +
    "  Object o1 = new Serializable() { int x; };" + PMD.EOL +
    " }" + PMD.EOL  +
    "}" + PMD.EOL;

    public void testClashingSymbols() {
        JavaParser parser = new JavaParser(new StringReader(TEST1));
        ASTCompilationUnit c = parser.CompilationUnit();
        SymbolFacade stb = new SymbolFacade();
        stb.initializeWith(c);

    }
}
