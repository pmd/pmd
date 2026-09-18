/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.cpd;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringWriter;

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
                StringWriter xml = new StringWriter();
                try {
                    new XMLRenderer().render(report, xml);
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
                // the XML serializer writes newlines as the platform line separator
                String rendered = xml.toString().replace("\r\n", "\n");
                assertTrue(rendered.contains("<codefragment><![CDATA[" + CODE + "]]></codefragment>"), rendered);
            });
        }
    }
}
