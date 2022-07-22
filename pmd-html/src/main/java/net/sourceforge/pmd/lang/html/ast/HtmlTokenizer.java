/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.ast;

import java.io.IOException;
import java.io.UncheckedIOException;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.document.CpdCompat;
import net.sourceforge.pmd.lang.document.TextDocument;

public class HtmlTokenizer implements Tokenizer {

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        try (TextDocument textDoc = TextDocument.create(CpdCompat.cpdCompat(sourceCode))) {
            ParserTask task = new ParserTask(
                textDoc,
                SemanticErrorReporter.noop()// fixme
            );

            HtmlParser parser = new HtmlParser();
            ASTHtmlDocument root = parser.parse(task);

            traverse(root, tokenEntries);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            tokenEntries.add(TokenEntry.EOF);
        }
    }

    private void traverse(HtmlNode node, Tokens tokenEntries) {
        String image = node.getXPathNodeName();

        if (node instanceof ASTHtmlTextNode) {
            image = ((ASTHtmlTextNode) node).getText();
        }

        TokenEntry token = new TokenEntry(image, node.getReportLocation());
        tokenEntries.add(token);

        for (HtmlNode child : node.children()) {
            traverse(child, tokenEntries);
        }
    }
}
