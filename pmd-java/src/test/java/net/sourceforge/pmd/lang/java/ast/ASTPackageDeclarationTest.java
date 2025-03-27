/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextPos2d;
import net.sourceforge.pmd.lang.java.BaseParserTest;

class ASTPackageDeclarationTest extends BaseParserTest {

    private static final String PACKAGE_INFO_ANNOTATED = "@Deprecated\npackage net.sourceforge.pmd.foobar;\n";

    /**
     * Regression test for bug https://sourceforge.net/p/pmd/bugs/1006/.
     */
    @Test
    void testPackageName() {
        ASTCompilationUnit nodes = java.parse(PACKAGE_INFO_ANNOTATED);

        assertEquals("net.sourceforge.pmd.foobar", nodes.getPackageDeclaration().getName());
        assertEquals("net.sourceforge.pmd.foobar", nodes.getPackageName());
    }

    @Test
    void testReportLocation() {
        ASTCompilationUnit nodes = java.parse(
            "/** a javadoc comment */\n"
                + "package \n"
                + "     foo.\n"
                + "     bar\n"
                + ";"
        );
        ASTPackageDeclaration packageDecl = nodes.getPackageDeclaration();
        // this is the range of the Name.
        FileLocation loc = packageDecl.getReportLocation();
        assertEquals(
            TextPos2d.pos2d(3, 6),
            loc.getStartPos()
        );
        assertEquals(
            TextPos2d.pos2d(4, 9),
            loc.getEndPos()
        );
        assertEquals(packageDecl.getTextRegion(),
                     nodes.getTextDocument().getEntireRegion());
    }
}
