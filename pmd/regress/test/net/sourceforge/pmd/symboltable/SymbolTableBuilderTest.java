/*
 * User: tom
 * Date: Sep 30, 2002
 * Time: 2:28:03 PM
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.symboltable.SymbolTableBuilder;
import net.sourceforge.pmd.symboltable.SymbolTable;

import java.io.Reader;
import java.io.InputStreamReader;

public class SymbolTableBuilderTest extends TestCase {

    public void testBasic() throws Throwable {
        Reader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("SymbolTableBuilderTest.java"));
        JavaParser parser = new JavaParser(reader);
        ASTCompilationUnit c = parser.CompilationUnit();
        SymbolTableBuilder stb = new SymbolTableBuilder();
        stb.initializeWith(c);
        SymbolTable table = stb.getSymbolTable();


    }

}
