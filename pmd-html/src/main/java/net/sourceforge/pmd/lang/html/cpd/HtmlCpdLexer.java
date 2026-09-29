/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.cpd;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.cpd.TokenFactory;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.html.HtmlLanguageModule;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlDocument;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlElement;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlTextNode;
import net.sourceforge.pmd.lang.html.ast.HtmlNode;
import net.sourceforge.pmd.lang.html.ast.HtmlParser;

/**
 * <p>Note: This class has been called HtmlTokenizer in PMD 6</p>.
 */
public class HtmlCpdLexer implements CpdLexer {

    @Override
    public void tokenize(TextDocument document, TokenFactory tokens) {
        HtmlLanguageModule html = HtmlLanguageModule.getInstance();

        try (LanguageProcessor processor = html.createProcessor(html.newPropertyBundle())) {

            ParserTask task = new ParserTask(
                document,
                SemanticErrorReporter.noop(),
                LanguageProcessorRegistry.singleton(processor)
            );

            HtmlParser parser = new HtmlParser();
            ASTHtmlDocument root = parser.parse(task);

            traverse(root, tokens, document, new HashSet<>());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void traverse(HtmlNode node, TokenFactory tokenEntries, TextDocument document,
                          Set<Integer> closingTagOffsets) {
        String image = node.getXPathNodeName();

        if (node instanceof ASTHtmlTextNode) {
            image = ((ASTHtmlTextNode) node).getWholeText();
        }

        tokenEntries.recordToken(image, node.getReportLocation());

        for (HtmlNode child : node.children()) {
            traverse(child, tokenEntries, document, closingTagOffsets);
        }

        if (node instanceof ASTHtmlElement) {
            recordClosingTag(node, tokenEntries, document, closingTagOffsets);
        }
    }

    private void recordClosingTag(HtmlNode node, TokenFactory tokenEntries, TextDocument document,
                                  Set<Integer> closingTagOffsets) {
        String name = node.getXPathNodeName();
        // HTML node regions currently end at the last source character instead of after it.
        int endOffset = node.getTextRegion().getEndOffset() + 1;
        Chars source = document.getText();
        int startOffset = source.lastIndexOf('<', endOffset - 1);

        // Only tokenize explicit closing tags, and emit each source range once.
        if (isClosingTag(source, startOffset, endOffset, name) && closingTagOffsets.add(startOffset)) {
            TextRegion region = TextRegion.fromBothOffsets(startOffset, endOffset);
            tokenEntries.recordToken("/" + name, document.toLocation(region));
        }
    }

    private boolean isClosingTag(Chars source, int startOffset, int endOffset, String name) {
        if (startOffset < 0 || endOffset > source.length() || !source.startsWith("</", startOffset)) {
            return false;
        }

        int nameStart = startOffset + 2;
        if (nameStart + name.length() > endOffset) {
            return false;
        }

        for (int i = 0; i < name.length(); i++) {
            if (Character.toLowerCase(source.charAt(nameStart + i)) != Character.toLowerCase(name.charAt(i))) {
                return false;
            }
        }

        int tagEnd = nameStart + name.length();
        while (tagEnd < endOffset && isHtmlWhitespace(source.charAt(tagEnd))) {
            tagEnd++;
        }
        return tagEnd + 1 == endOffset && source.charAt(tagEnd) == '>';
    }

    private boolean isHtmlWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\f' || c == '\r';
    }
}
