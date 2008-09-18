package net.sourceforge.pmd.lang.ast.xpath.saxon;

import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.Type;
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
	    // TODO Need better typing on the Attribute class?
	    value = new StringValue(attribute.getValue());
	}
	return value;
    }

    @Override
    public CharSequence getStringValueCS() {
	return attribute.getValue();
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
