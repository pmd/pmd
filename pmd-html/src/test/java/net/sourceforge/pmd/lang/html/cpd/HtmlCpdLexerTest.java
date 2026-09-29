/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.cpd.CPDConfiguration;
import net.sourceforge.pmd.cpd.CpdAnalysis;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.Tokens;
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
        String source = "<html>\n<body>\n</body>\n</html>";
        assertCompleteMatch(tempDir, source, source, 4, 8, 8);
    }

    @Test
    void reportsLastLineWithMixedTrailingNewline(@TempDir Path tempDir) throws Exception {
        String source = "<html>\n<body>\n</body>\n</html>";
        assertCompleteMatch(tempDir, source, source + "\n", 4, 8, 8);
    }

    @Test
    void reportsClosingTagsAfterNestedElement(@TempDir Path tempDir) throws Exception {
        String source = "<html><body><div>x</div></body></html>";
        assertCompleteMatch(tempDir, source, source, 1, source.length() + 1, 8);
    }

    @Test
    void recordsClosingTagsAsDistinctTokens() {
        String source = "<html><body><div>x</div></body></html>";
        Tokens tokens = tokenize(newCpdLexer(defaultProperties()), sourceCodeOf(source));
        List<String> images = tokens.getTokens().stream()
                .filter(token -> !token.isEof())
                .map(token -> token.getImage(tokens))
                .collect(Collectors.toList());

        assertEquals(Arrays.asList("#document", "html", "body", "div", "x", "/div", "/body", "/html"),
                     images);
    }

    @Test
    void doesNotReuseParentClosingTagForSelfClosingChild(@TempDir Path tempDir) throws Exception {
        String source = "<html><body><div><div/></div></body></html>";
        assertCompleteMatch(tempDir, source, source, 1, source.length() + 1, 8);
    }

    @Test
    void reportsClosingTagsWithWhitespace(@TempDir Path tempDir) throws Exception {
        String source = "<HTML><BODY><DIV>x</dIv ></BoDy ></HtMl >";
        assertCompleteMatch(tempDir, source, source, 1, source.length() + 1, 8);
    }

    @Test
    void reportsClosingTagsAfterQuotedAngleBracket(@TempDir Path tempDir) throws Exception {
        String source = "<html><body><div title='>'>x</div></body></html>";
        assertCompleteMatch(tempDir, source, source, 1, source.length() + 1, 8);
    }

    private void assertCompleteMatch(Path tempDir, String firstSource, String secondSource,
                                     int endLine, int endColumn, int tokenCount) throws Exception {
        Path first = Files.write(tempDir.resolve("first.html"), firstSource.getBytes(StandardCharsets.UTF_8));
        Path second = Files.write(tempDir.resolve("second.html"), secondSource.getBytes(StandardCharsets.UTF_8));

        CPDConfiguration configuration = new CPDConfiguration();
        configuration.setMinimumTileSize(5);
        configuration.setOnlyRecognizeLanguage(HtmlLanguageModule.getInstance());
        try (CpdAnalysis cpd = CpdAnalysis.create(configuration)) {
            cpd.files().addFile(first);
            cpd.files().addFile(second);

            cpd.performAnalysis(report -> {
                assertEquals(1, report.getMatches().size());
                Match match = report.getMatches().get(0);
                assertEquals(tokenCount, match.getTokenCount());
                assertEquals(endLine, match.getFirstMark().getLocation().getEndLine());
                assertEquals(endColumn, match.getFirstMark().getLocation().getEndColumn());
                assertEquals(endLine, match.getSecondMark().getLocation().getEndLine());
                assertEquals(endColumn, match.getSecondMark().getLocation().getEndColumn());
                assertEquals(firstSource,
                             report.getSourceCodeSlice(match.getFirstMark()).toString());
                assertEquals(secondSource,
                             report.getSourceCodeSlice(match.getSecondMark()).toString());
            });
        }
    }
}
