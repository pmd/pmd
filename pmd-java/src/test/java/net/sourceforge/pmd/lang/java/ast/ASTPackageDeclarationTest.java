/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.PMD;

public class ASTPackageDeclarationTest extends BaseParserTest {

    private static final String PACKAGE_INFO_ANNOTATED = "@Deprecated" + PMD.EOL + "package net.sourceforge.pmd.foobar;"
            + PMD.EOL;

    /**
     * Regression test for bug 3524607.
     */
    @Test
    public void testPackageName() {
        ASTCompilationUnit nodes = java.parse(PACKAGE_INFO_ANNOTATED);

        assertEquals("net.sourceforge.pmd.foobar", nodes.getPackageDeclaration().getPackageNameImage());
        assertEquals("net.sourceforge.pmd.foobar", nodes.getPackageName());
    }
}
