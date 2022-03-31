/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.jsoup.nodes.XmlDeclaration;

public final class HtmlXmlDeclaration extends AbstractHtmlNode<XmlDeclaration> {

    HtmlXmlDeclaration(XmlDeclaration node) {
        super(node);
    }

    public String getName() {
        return node.name();
    }
}
