/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

public class ASTPackageDeclarationTest extends BaseParserTest {

    /**
     * Regression test for bug 3524607.
     */
    @Test
    public void testPackageName() {
        ASTCompilationUnit nodes = java.parse("@Deprecated package net.sourceforge.pmd.foobar;");

        assertEquals("net.sourceforge.pmd.foobar", nodes.getPackageDeclaration().getName());
        assertEquals("net.sourceforge.pmd.foobar", nodes.getPackageName());
    }
}
