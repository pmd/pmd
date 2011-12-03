/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ParseException;
import net.sourceforge.pmd.testframework.ParserTst;

import org.junit.Test;


public class ASTImportDeclarationTest extends ParserTst {

    @Test
    public void testImportOnDemand() throws Throwable {
        Set<ASTImportDeclaration> ops = getNodes(ASTImportDeclaration.class, TEST1);
        assertTrue(ops.iterator().next().isImportOnDemand());
    }

    @Test
    public void testGetImportedNameNode() throws Throwable {
        ASTImportDeclaration i = getNodes(ASTImportDeclaration.class, TEST2).iterator().next();
        assertEquals("foo.bar.Baz", i.getImportedName());
    }

    @Test
    public void testStaticImport() throws Throwable {
        Set<ASTImportDeclaration> ops = getNodes(ASTImportDeclaration.class, TEST3);
        ASTImportDeclaration i = ops.iterator().next();
        assertTrue(i.isStatic());
    }

    @Test(expected = ParseException.class)
    public void testStaticImportFailsWithJDK14() throws Throwable {
        getNodes(LanguageVersion.JAVA_14, ASTImportDeclaration.class, TEST3);
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

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ASTImportDeclarationTest.class);
    }
}
