/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.cpd.CPDConfiguration;
import net.sourceforge.pmd.cpd.CpdAnalysis;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.lang.html.HtmlLanguageModule;
import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;

class HtmlCpdLexerTest extends CpdTextComparisonTest {

    HtmlCpdLexerTest() {
        super(HtmlLanguageModule.getInstance(), ".html");
    }

    @Test
    void testSimpleHtmlFile() {
        doTest("SimpleHtmlFile");
    }

    @Test
    void invalidHtml() {
        doTest("InvalidHtml");
    }

    @Test
    void metaTag() {
        doTest("MetaTag");
    }

    /**
     * @see <a href="https://github.com/pmd/pmd/issues/6135">Issue 6135</a>
     */
    @Test
    void unescapedTagInScript() {
        doTest("UnescapedTagInScript");
    }

    @Test
    void reportsLastLineWithoutTrailingNewline(@TempDir Path tempDir) throws Exception {
        byte[] source = "<html>\n<body>\n</body>\n</html>".getBytes(StandardCharsets.UTF_8);
        Path first = Files.write(tempDir.resolve("first.html"), source);
        Path second = Files.write(tempDir.resolve("second.html"),
                                  "<html>\n<body>\n</body>\n</html>\n".getBytes(StandardCharsets.UTF_8));

        CPDConfiguration configuration = new CPDConfiguration();
        configuration.setMinimumTileSize(5);
        configuration.setOnlyRecognizeLanguage(HtmlLanguageModule.getInstance());
        try (CpdAnalysis cpd = CpdAnalysis.create(configuration)) {
            cpd.files().addFile(first);
            cpd.files().addFile(second);

            cpd.performAnalysis(report -> {
                assertEquals(1, report.getMatches().size());
                Match match = report.getMatches().get(0);
                assertEquals(6, match.getTokenCount());
                assertEquals(4, match.getFirstMark().getLocation().getEndLine());
                assertEquals(4, match.getSecondMark().getLocation().getEndLine());
                assertEquals(source.length,
                             report.getSourceCodeSlice(match.getFirstMark()).length());
            });
        }
    }
}
