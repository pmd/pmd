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
import net.sourceforge.pmd.cpd.TokenEntry;
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
    void reportsLastLineWhenBothInputsLackTrailingNewline(@TempDir Path tempDir) throws Exception {
        String source = "<html>\n<body>\n</body>\n</html>";
        assertCompleteMatch(tempDir, source, source);
    }

    @Test
    void reportsLastLineWithMixedTrailingNewline(@TempDir Path tempDir) throws Exception {
        String source = "<html>\n<body>\n</body>\n</html>";
        assertCompleteMatch(tempDir, source, source + "\n");
    }

    @Test
    void recordsClosingTagsAtTheirSourceRanges() {
        String source = "<html><body><div>x</div></body></html>";
        Tokens tokens = tokenize(newCpdLexer(defaultProperties()), sourceCodeOf(source));
        List<TokenEntry> entries = tokens.getTokens().stream()
                .filter(token -> !token.isEof())
                .collect(Collectors.toList());
        List<String> images = entries.stream()
                .map(token -> token.getImage(tokens))
                .collect(Collectors.toList());

        assertEquals(Arrays.asList("#document", "html", "body", "div", "x", "/div", "/body", "/html"),
                     images);
        assertLocation(entries.get(5), 1, 19, 1, 25);
        assertLocation(entries.get(6), 1, 25, 1, 32);
        assertLocation(entries.get(7), 1, 32, 1, 39);
    }

    @Test
    void doesNotInventClosingTags() {
        String source = "<html><body><br><hr/><p>x</body></html>";
        Tokens tokens = tokenize(newCpdLexer(defaultProperties()), sourceCodeOf(source));
        List<String> images = tokens.getTokens().stream()
                .filter(token -> !token.isEof())
                .map(token -> token.getImage(tokens))
                .collect(Collectors.toList());

        assertEquals(Arrays.asList("#document", "html", "body", "br", "hr", "p", "x", "/body", "/html"),
                     images);
    }

    @Test
    void recordsClosingTagsAfterCharacterReferences() {
        String source = "<html><body><div>&amp;</div></body></html>";
        Tokens tokens = tokenize(newCpdLexer(defaultProperties()), sourceCodeOf(source));
        List<TokenEntry> entries = tokens.getTokens().stream()
                .filter(token -> !token.isEof())
                .collect(Collectors.toList());
        List<String> images = entries.stream()
                .map(token -> token.getImage(tokens))
                .collect(Collectors.toList());

        assertEquals(Arrays.asList("#document", "html", "body", "div", "&", "/div", "/body", "/html"), images);
        assertLocation(entries.get(5), 1, 23, 1, 29);
        assertLocation(entries.get(6), 1, 29, 1, 36);
        assertLocation(entries.get(7), 1, 36, 1, 43);
    }

    private void assertCompleteMatch(Path tempDir, String firstSource, String secondSource) throws Exception {
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
                assertEquals(8, match.getTokenCount());
                assertEquals(4, match.getFirstMark().getLocation().getEndLine());
                assertEquals(8, match.getFirstMark().getLocation().getEndColumn());
                assertEquals(4, match.getSecondMark().getLocation().getEndLine());
                assertEquals(8, match.getSecondMark().getLocation().getEndColumn());
                assertEquals(firstSource, report.getSourceCodeSlice(match.getFirstMark()).toString());
                assertEquals(secondSource, report.getSourceCodeSlice(match.getSecondMark()).toString());
            });
        }
    }

    private void assertLocation(TokenEntry token, int beginLine, int beginColumn, int endLine, int endColumn) {
        assertEquals(beginLine, token.getBeginLine());
        assertEquals(beginColumn, token.getBeginColumn());
        assertEquals(endLine, token.getEndLine());
        assertEquals(endColumn, token.getEndColumn());
    }
}
