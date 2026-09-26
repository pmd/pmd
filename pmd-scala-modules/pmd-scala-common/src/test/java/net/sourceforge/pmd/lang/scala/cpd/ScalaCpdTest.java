/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.cpd;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.CPDConfiguration;
import net.sourceforge.pmd.cpd.CpdAnalysis;
import net.sourceforge.pmd.cpd.XMLRenderer;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;

class ScalaCpdTest {

    private static final String CODE = "object Foo {\n"
            + "    void foo() {\n"
            + "        String[] array = null;\n"
            + "        array = {\"abc\", \"def\", \"ghi\"};\n"
            + "    }\n"
            + "}\n";

    /**
     * @see <a href="https://github.com/pmd/pmd/issues/2153">GitHub Issue #2153</a>
     */
    @Test
    void smallMinimumTokensRendersXml() throws Exception {
        CPDConfiguration configuration = new CPDConfiguration();
        configuration.setMinimumTileSize(20);
        configuration.setOnlyRecognizeLanguage(ScalaLanguageModule.getInstance());
        try (CpdAnalysis cpd = CpdAnalysis.create(configuration)) {
            cpd.files().addSourceFile(FileId.fromPathLikeString("Foo.scala"), CODE);
            cpd.files().addSourceFile(FileId.fromPathLikeString("Bar.scala"), CODE);

            cpd.performAnalysis(report -> {
                assertFalse(report.getMatches().isEmpty());
                assertDoesNotThrow(() -> new XMLRenderer().renderToString(report));
            });
        }
    }
}
