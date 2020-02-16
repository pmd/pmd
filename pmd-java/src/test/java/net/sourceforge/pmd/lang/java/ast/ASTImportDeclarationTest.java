/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class ASTImportDeclarationTest extends BaseParserTest {

    @Test
    public void testImportOnDemand() {
        List<ASTImportDeclaration> ops = java.getNodes(ASTImportDeclaration.class, TEST1);
        assertTrue(ops.get(0).isImportOnDemand());
    }

    @Test
    public void testGetImportedNameNode() {
        ASTImportDeclaration i = java.getNodes(ASTImportDeclaration.class, TEST2).get(0);
        assertEquals("foo.bar.Baz", i.getImportedName());
    }

    @Test
    public void testStaticImport() {
        List<ASTImportDeclaration> ops = java.getNodes(ASTImportDeclaration.class, TEST3);
        ASTImportDeclaration i = ops.get(0);
        assertTrue(i.isStatic());
    }

    @Test(expected = ParseException.class)
    public void testStaticImportFailsWithJDK14() {
        java.parse(TEST3, "1.4");
    }

    private static final String TEST1 = "import foo.bar.*;\npublic class Foo {}";

    private static final String TEST2 = "import foo.bar.Baz;\npublic class Foo {}";

    private static final String TEST3 = "import static foo.bar.Baz;\npublic class Foo {}";
}
