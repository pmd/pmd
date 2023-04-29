/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.ast;

import java.io.IOException;
import java.io.UncheckedIOException;

import net.sourceforge.pmd.cpd.TokenFactory;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.html.HtmlLanguageModule;

public class HtmlTokenizer implements Tokenizer {

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
            image = ((ASTHtmlTextNode) node).getText();
        }

        tokenEntries.recordToken(image, node.getReportLocation());

        for (HtmlNode child : node.children()) {
            traverse(child, tokenEntries);
        }
    }
}
