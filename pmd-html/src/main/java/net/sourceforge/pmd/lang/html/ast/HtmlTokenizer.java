/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.ast;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.html.HtmlLanguageModule;

public class HtmlTokenizer implements Tokenizer {

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        ParserTask task = new ParserTask(
                LanguageRegistry.getLanguage(HtmlLanguageModule.NAME).getDefaultVersion(),
                sourceCode.getFileName(),
                sourceCode.getCodeBuffer().toString(),
                SemanticErrorReporter.noop() // todo
        );

        HtmlParser parser = new HtmlParser();
        ASTHtmlDocument root = parser.parse(task);

        traverse(root, tokenEntries);
        tokenEntries.add(TokenEntry.EOF);
    }

    private void traverse(HtmlNode node, Tokens tokenEntries) {
        String image = node.getXPathNodeName();

        if (node instanceof ASTHtmlTextNode) {
            image = ((ASTHtmlTextNode) node).getText();
        }

        TokenEntry token = new TokenEntry(image, node.getXPathNodeName(), node.getBeginLine(),
                node.getBeginColumn(), node.getEndColumn());
        tokenEntries.add(token);

        for (HtmlNode child : node.children()) {
            traverse(child, tokenEntries);
        }
    }
}
