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
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.html.HtmlLanguageModule;

public class HtmlTokenizer implements Tokenizer {

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        HtmlLanguageModule html = HtmlLanguageModule.getInstance();

        try (LanguageProcessor processor = html.createProcessor(html.newPropertyBundle());
             TextFile tf = TextFile.forCharSeq(
                 sourceCode.getCodeBuffer(),
                 sourceCode.getFileName(),
                 html.getDefaultVersion()
             );
             TextDocument textDoc = TextDocument.create(tf)) {

            ParserTask task = new ParserTask(
                textDoc,
                SemanticErrorReporter.noop(),
                LanguageProcessorRegistry.singleton(processor)
            );

            HtmlParser parser = new HtmlParser();
            ASTHtmlDocument root = parser.parse(task);

            traverse(root, tokenEntries);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
