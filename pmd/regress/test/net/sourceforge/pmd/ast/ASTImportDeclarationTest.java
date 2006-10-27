/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_5;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.ParseException;
import test.net.sourceforge.pmd.testframework.ParserTst;

import java.util.Set;

public class ASTImportDeclarationTest extends ParserTst {

    public void testImportOnDemand() throws Throwable {
        Set ops = getNodes(ASTImportDeclaration.class, TEST1);
        assertTrue(((ASTImportDeclaration) (ops.iterator().next())).isImportOnDemand());
    }

    public void testGetImportedNameNode() throws Throwable {
        ASTImportDeclaration i = (ASTImportDeclaration) (getNodes(ASTImportDeclaration.class, TEST2).iterator().next());
        assertEquals("foo.bar.Baz", i.getImportedName());
    }

    public void testStaticImport() throws Throwable {
        Set ops = getNodes(new TargetJDK1_5(), ASTImportDeclaration.class, TEST3);
        ASTImportDeclaration i = (ASTImportDeclaration) (ops.iterator().next());
        assertTrue(i.isStatic());
    }

    public void testStaticImportFailsWithJDK14() throws Throwable {
        try {
            getNodes(ASTImportDeclaration.class, TEST3);
            fail("Should have failed to parse a static import in JDK 1.4 mode");
        } catch (ParseException pe) {
            // cool
        }
    }

    private static final String TEST1 =
            "import foo.bar.*;" + PMD.EOL +
            "public class Foo {}";

    private static final String TEST2 =
            "import foo.bar.Baz;" + PMD.EOL +
            "public class Foo {}";

    private static final String TEST3 =
            "import static foo.bar.Baz;" + PMD.EOL +
            "public class Foo {}";

}
