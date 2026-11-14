/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.ast;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.AttributeAxisIterator;

/**
 * @since 7.30.0
 */
public final class XmlRootNode extends XmlNodeWrapper implements XmlNode, RootNode {
    private final AstInfo<XmlRootNode> astInfo;

    XmlRootNode(XmlParserImpl parser, Document domNode, Parser.ParserTask task) {
        super(parser, domNode);
        this.astInfo = new AstInfo<>(task, this);
    }

    @Override
    public AstInfo<XmlRootNode> getAstInfo() {
        return astInfo;
    }

    @Override
    public XmlNode wrap(Node domNode) {
        return super.wrap(domNode);
    }

    @Override
    public Document getNode() {
        return (Document) super.getNode();
    }

    public String getXmlEncoding() {
        return getNode().getXmlEncoding();
    }

    public boolean isXmlStandalone() {
        return getNode().getXmlStandalone();
    }

    public String getXmlVersion() {
        return getNode().getXmlVersion();
    }

    @Override
    public Iterator<Attribute> getXPathAttributesIterator() {
        // Expose this node's attributes through reflection
        return new AttributeAxisIterator(this);
    }
}
