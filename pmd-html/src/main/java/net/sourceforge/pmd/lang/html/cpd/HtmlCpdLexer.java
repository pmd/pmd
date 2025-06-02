/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.cpd;

import java.io.IOException;
import java.io.UncheckedIOException;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.cpd.TokenFactory;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.html.HtmlLanguageModule;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlDocument;
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

            traverse(root, tokens);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void traverse(HtmlNode node, TokenFactory tokenEntries) {
        String image = node.getXPathNodeName();

        if (node instanceof ASTHtmlTextNode) {
            image = ((ASTHtmlTextNode) node).getWholeText();
        }

        tokenEntries.recordToken(image, node.getReportLocation());

        for (HtmlNode child : node.children()) {
            traverse(child, tokenEntries);
        }
    }
}
