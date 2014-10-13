/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast.xpath.saxon;

import net.sf.saxon.om.Navigator;
import net.sf.saxon.om.SequenceIterator;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;

/**
 * This is an Attribute axis iterator.
 */
public class AttributeAxisIterator extends Navigator.BaseEnumeration {

    protected final ElementNode startNodeInfo;
    protected final net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator iterator;

    /**
     * Create an iterator over the Attribute axis for the given ElementNode.
     * @see net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator
     */
    public AttributeAxisIterator(ElementNode startNodeInfo) {
	this.startNodeInfo = startNodeInfo;
	this.iterator = new net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator(startNodeInfo.node);
    }

    /**
     * {@inheritDoc}
     */
    public SequenceIterator getAnother() {
	return new AttributeAxisIterator(startNodeInfo);
    }

    /**
     * {@inheritDoc}
     */
    public void advance() {
	if (this.iterator.hasNext()) {
	    Attribute attribute = this.iterator.next();
	    super.current = new AttributeNode(attribute, super.position());
	} else {
	    super.current = null;
	}
    }
}
