package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.TargetJDK1_5;
import net.sourceforge.pmd.TargetJDKVersion;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.symboltable.SymbolFacade;

import java.io.StringReader;

public abstract class STBBaseTst extends TestCase {

    protected ASTCompilationUnit acu;
    protected SymbolFacade stb;

    protected void parseCode(String code) {
        parseCode(code, new TargetJDK1_4());
    }

    protected void parseCode15(String code) {
        parseCode(code, new TargetJDK1_5());
    }

    protected void parseCode(String code, TargetJDKVersion jdk) {
        JavaParser parser = jdk.createParser(new StringReader(code));
        acu = parser.CompilationUnit();
        stb = new SymbolFacade();
        stb.initializeWith(acu);
    }
}
