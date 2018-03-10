/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.rule;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.xml.ast.XmlNode;

/**
 * This is a base class for XML Java bases rules that which to visit the nodes
 * using the DOM. Subclasses should override the DOM appropriate method and can
 * call <code>super</code> to visit children.
 */
public class AbstractDomXmlRule extends AbstractXmlRule {

    @Override
    protected void visit(XmlNode node, RuleContext ctx) {
        final Node domNode = node.getNode();

        // Visit the node
        visitDomNode(node, domNode, ctx);

        // Visit attributes
        visitAttributeNodes(node, domNode, ctx);
    }

    protected void visitDomNode(XmlNode node, Node domNode, RuleContext ctx) {
        switch (domNode.getNodeType()) {
        case Node.CDATA_SECTION_NODE:
            visit(node, (CharacterData) domNode, ctx);
            break;
        case Node.COMMENT_NODE:
            visit(node, (Comment) domNode, ctx);
            break;
        case Node.DOCUMENT_NODE:
            visit(node, (Document) domNode, ctx);
            break;
        case Node.DOCUMENT_TYPE_NODE:
            visit(node, (DocumentType) domNode, ctx);
            break;
        case Node.ELEMENT_NODE:
            visit(node, (Element) domNode, ctx);
            break;
        case Node.ENTITY_NODE:
            visit(node, (Entity) domNode, ctx);
            break;
        case Node.ENTITY_REFERENCE_NODE:
            visit(node, (EntityReference) domNode, ctx);
            break;
        case Node.NOTATION_NODE:
            visit(node, (Notation) domNode, ctx);
            break;
        case Node.PROCESSING_INSTRUCTION_NODE:
            visit(node, (ProcessingInstruction) domNode, ctx);
            break;
        case Node.TEXT_NODE:
            visit(node, (Text) domNode, ctx);
            break;
        default:
            throw new RuntimeException("Unexpected node type: " + domNode.getNodeType() + " on node: " + domNode);
        }
    }

    protected void visitAttributeNodes(XmlNode node, Node domNode, RuleContext ctx) {
        NamedNodeMap attributes = domNode.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                visit(node, (Attr) attributes.item(i), ctx);
            }
        }
    }

    protected void visit(XmlNode node, Attr attr, RuleContext ctx) {
        // does nothing by default since attributes are leaf nodes
    }

    protected void visit(XmlNode node, CharacterData characterData, RuleContext ctx) {
        super.visit(node, ctx);
    }

    protected void visit(XmlNode node, Comment comment, RuleContext ctx) {
        super.visit(node, ctx);
    }

    protected void visit(XmlNode node, Document document, RuleContext ctx) {
        super.visit(node, ctx);
    }

    protected void visit(XmlNode node, DocumentType documentType, RuleContext ctx) {
        super.visit(node, ctx);
    }

    protected void visit(XmlNode node, Element element, RuleContext ctx) {
        super.visit(node, ctx);
    }

    protected void visit(XmlNode node, Entity entity, RuleContext ctx) {
        super.visit(node, ctx);
    }

    protected void visit(XmlNode node, EntityReference entityReference, RuleContext ctx) {
        super.visit(node, ctx);
    }

    protected void visit(XmlNode node, Notation notation, RuleContext ctx) {
        super.visit(node, ctx);
    }

    protected void visit(XmlNode node, ProcessingInstruction processingInstruction, RuleContext ctx) {
        super.visit(node, ctx);
    }

    protected void visit(XmlNode node, Text text, RuleContext ctx) {
        super.visit(node, ctx);
    }
}
