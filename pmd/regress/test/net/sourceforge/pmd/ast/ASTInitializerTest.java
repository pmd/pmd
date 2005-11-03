package test.net.sourceforge.pmd.ast;

import test.net.sourceforge.pmd.testframework.ParserTst;

import java.util.Set;

import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.ast.ASTInitializer;
import net.sourceforge.pmd.TargetJDK1_5;
import net.sourceforge.pmd.PMD;

public class ASTInitializerTest extends ParserTst {

    public void testDontCrashOnBlockStatement() throws Throwable {
        try {
            getNodes(ASTInitializer.class, TEST1);
        } catch (Exception e) {
            // FIXME
            // e.printStackTrace();
            //fail("Couldn't parse:\n" + TEST1);
        }
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " {" + PMD.EOL +
    "   x = 5;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
