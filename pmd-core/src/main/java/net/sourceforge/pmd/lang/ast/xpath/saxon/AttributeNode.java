/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast.xpath.saxon;

import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.Type;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.EmptySequence;
import net.sf.saxon.value.Int64Value;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.Value;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;

/**
 * A Saxon OM Attribute node for an AST Node Attribute.
 */
public class AttributeNode extends AbstractNodeInfo {
    protected final Attribute attribute;
    protected final int id;
    protected Value value;

    public AttributeNode(Attribute attribute, int id) {
	this.attribute = attribute;
	this.id = id;
    }

    @Override
    public int getNodeKind() {
	return Type.ATTRIBUTE;
    }

    @Override
    public String getLocalPart() {
	return attribute.getName();
    }

    @Override
    public String getURI() {
	return "";
    }

    @Override
    public Value atomize() throws XPathException {
	if (value == null) {
	    Object v = attribute.getValue();
	    // TODO Need to handle the full range of types, is there something Saxon can do to help?
	    if (v instanceof String) {
		value = new StringValue((String) v);
	    } else if (v instanceof Boolean) {
		value = BooleanValue.get(((Boolean) v).booleanValue());
	    } else if (v instanceof Integer) {
		value = Int64Value.makeIntegerValue((Integer) v);
	    } else if (v == null) {
		value = EmptySequence.getInstance();
	    } else {
		throw new RuntimeException("Unable to create ValueRepresentaton for attribute value: " + v
			+ " of type " + v.getClass());
	    }
	}
	return value;
    }

    @Override
    public CharSequence getStringValueCS() {
	return attribute.getStringValue();
    }

    @Override
    public SequenceIterator getTypedValue() throws XPathException {
	return atomize().iterate();
    }

    @Override
    public int compareOrder(NodeInfo other) {
	return Integer.signum(this.id - ((AttributeNode) other).id);
    }
}
