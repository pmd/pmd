/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.saxon;

import java.util.Iterator;

import net.sourceforge.pmd.lang.ast.xpath.Attribute;

import net.sf.saxon.om.Navigator;
import net.sf.saxon.om.SequenceIterator;

/**
 * This is an Attribute axis iterator.
 */
public class AttributeAxisIterator extends Navigator.BaseEnumeration {

    protected final ElementNode startNodeInfo;
    protected final Iterator<Attribute> iterator;

    /**
     * Create an iterator over the Attribute axis for the given ElementNode.
     *
     * @see net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator
     */
    public AttributeAxisIterator(ElementNode startNodeInfo) {
        this.startNodeInfo = startNodeInfo;
        this.iterator = startNodeInfo.node instanceof net.sourceforge.pmd.lang.ast.xpath.AttributeNode
                ? ((net.sourceforge.pmd.lang.ast.xpath.AttributeNode) startNodeInfo.node).getAttributeIterator()
                : new net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator(startNodeInfo.node);
    }

    @Override
    public SequenceIterator getAnother() {
        return new AttributeAxisIterator(startNodeInfo);
    }

    @Override
    public void advance() {
        if (this.iterator.hasNext()) {
            Attribute attribute = this.iterator.next();
            super.current = new AttributeNode(attribute, super.position());
        } else {
            super.current = null;
        }
    }
}
