/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;

public class HtmlTokenizer implements Tokenizer {

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        String data = sourceCode.getCodeBuffer().toString();

        Document doc = Parser.xmlParser().parseInput(data, "");
        HtmlTreeBuilder builder = new HtmlTreeBuilder();
        ASTHtmlDocument root = builder.build(doc, data);
        
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
