/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.cpd;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.cpd.TokenFactory;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
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

            traverse(root, tokens);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void traverse(HtmlNode node, TokenFactory tokenEntries) {
        recordToken(node, tokenEntries, null);

        for (HtmlNode child : node.children()) {
            FileLocation endLocation = child instanceof ASTHtmlElement ? child.getReportLocation() : null;
            traverse(child, tokenEntries, endLocation);
        }
    }

    private void traverse(HtmlNode node, TokenFactory tokenEntries, @Nullable FileLocation endLocation) {
        recordToken(node, tokenEntries, endLocation);

        for (int i = 0; i < node.getNumChildren(); i++) {
            HtmlNode child = node.getChild(i);
            traverse(child, tokenEntries, i == node.getNumChildren() - 1 ? endLocation : null);
        }
    }

    private void recordToken(HtmlNode node, TokenFactory tokenEntries, @Nullable FileLocation endLocation) {
        String image = node.getXPathNodeName();

        if (node instanceof ASTHtmlTextNode) {
            image = ((ASTHtmlTextNode) node).getWholeText();
        }

        FileLocation location = node.getReportLocation();
        if (node.getNumChildren() == 0 && endLocation != null) {
            tokenEntries.recordToken(image, location.getStartLine(), location.getStartColumn(),
                                     endLocation.getEndLine(), endLocation.getEndColumn());
        } else {
            tokenEntries.recordToken(image, location);
        }
    }
}
