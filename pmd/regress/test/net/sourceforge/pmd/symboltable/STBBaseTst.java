package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.symboltable.SymbolFacade;

import java.io.StringReader;

public abstract class STBBaseTst extends TestCase {

    protected ASTCompilationUnit acu;
    protected SymbolFacade stb = new SymbolFacade();

    protected void parseCode(String code) {
        JavaParser parser = (new TargetJDK1_4()).createParser(new StringReader(code));
        acu = parser.CompilationUnit();
        stb.initializeWith(acu);
    }

}
