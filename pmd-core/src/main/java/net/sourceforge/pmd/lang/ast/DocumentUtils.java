/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.ast.xpath.DocumentNavigator;

/**
 * Remove when we have Java 9 support, and make all methods private on Node interface
 */
/* default */ final class DocumentUtils {

    private DocumentUtils() {

    }

    /* default */ static void appendElement(final Node node, final org.w3c.dom.Node parentNode) {
        final DocumentNavigator docNav = new DocumentNavigator();
        Document ownerDocument = parentNode.getOwnerDocument();
        if (ownerDocument == null) {
            // If the parentNode is a Document itself, it's ownerDocument is null
            ownerDocument = (Document) parentNode;
        }
        final String elementName = docNav.getElementName(node);
        final Element element = ownerDocument.createElement(elementName);
        parentNode.appendChild(element);
        for (final Iterator<Attribute> iter = docNav.getAttributeAxisIterator(node); iter.hasNext();) {
            final Attribute attr = iter.next();
            element.setAttribute(attr.getName(), attr.getStringValue());
        }
        for (final Iterator<Node> iter = docNav.getChildAxisIterator(node); iter.hasNext();) {
            final AbstractNode child = (AbstractNode) iter.next();
            appendElement(child, element);
        }
    }
}
