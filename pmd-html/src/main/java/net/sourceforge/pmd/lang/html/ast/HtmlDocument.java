/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.jsoup.nodes.Document;

import net.sourceforge.pmd.lang.ast.RootNode;

public class HtmlDocument extends HtmlElement implements RootNode {

    HtmlDocument(Document document) {
        super(document);
    }
}
