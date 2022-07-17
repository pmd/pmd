/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import java.util.HashMap;

import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

public final class HtmlParser implements net.sourceforge.pmd.lang.ast.Parser {

    @Override
    public ASTHtmlDocument parse(ParserTask task) {
        Document doc = Parser.xmlParser().parseInput(task.getTextDocument().getText().newReader(), "");
        HtmlTreeBuilder builder = new HtmlTreeBuilder();
        return builder.build(doc, task, new HashMap<>());
    }
}
