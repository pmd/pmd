/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ParserTstUtil.getNodes;
import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

import net.sourceforge.pmd.PMD;

public class ASTPackageDeclarationTest {

    private static final String PACKAGE_INFO_ANNOTATED = "@Deprecated" + PMD.EOL + "package net.sourceforge.pmd.foobar;"
            + PMD.EOL;

    /**
     * Regression test for bug 3524607.
     */
    @Test
    public void testPackageName() {
        Set<ASTPackageDeclaration> nodes = getNodes(ASTPackageDeclaration.class, PACKAGE_INFO_ANNOTATED);

        assertEquals(1, nodes.size());
        ASTPackageDeclaration packageNode = nodes.iterator().next();
        assertEquals("net.sourceforge.pmd.foobar", packageNode.getPackageNameImage());
    }
}
