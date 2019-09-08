/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.saxon;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.SaxonXPathRuleQuery;

import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.Type;
import net.sf.saxon.value.Value;

/**
 * A Saxon OM Attribute node for an AST Node Attribute.
 * Belongs to an {@link ElementNode}, and wraps an
 * {@link Attribute}.
 */
@Deprecated
@InternalApi
public class AttributeNode extends AbstractNodeInfo {
    protected final Attribute attribute;
    protected final int id;
    protected Value value;


    /**
     * Creates a new AttributeNode from a PMD Attribute.
     *
     * @param id The index within the attribute order
     */
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
    public Value atomize() {
        if (value == null) {
            Object data = attribute.getValue();
            if (data instanceof List) {
                value = SaxonXPathRuleQuery.getSequenceRepresentation((List<?>) data);
            } else {
                value = SaxonXPathRuleQuery.getAtomicRepresentation(attribute.getValue());
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
