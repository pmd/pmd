/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import net.sourceforge.pmd.lang.ast.xpath.Attribute;

import net.sf.saxon.om.AtomicSequence;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.pattern.NodeTest;
import net.sf.saxon.tree.iter.AxisIterator;
import net.sf.saxon.tree.util.FastStringBuffer;
import net.sf.saxon.tree.wrapper.AbstractNodeWrapper;
import net.sf.saxon.type.SchemaType;
import net.sf.saxon.type.Type;


/**
 * @author Clément Fournier
 * @since 7.0.0
 */
public class AstAttributeNode extends AbstractNodeWrapper {


    private final AstElementNode parent;
    private final Attribute attribute;
    private  AtomicSequence value;
    private final SchemaType schemaType;


    AstAttributeNode(AstElementNode parent, Attribute attribute) {
        this.parent = parent;
        this.attribute = attribute;
        this.schemaType = DomainConversion.buildType(attribute.getType());
    }


    @Override
    protected AxisIterator iterateAttributes(NodeTest nodeTest) {
        return null;
    }


    @Override
    protected AxisIterator iterateChildren(NodeTest nodeTest) {
        return null;
    }


    @Override
    protected AxisIterator iterateSiblings(NodeTest nodeTest, boolean forwards) {
        return null;
    }


    @Override
    protected AxisIterator iterateDescendants(NodeTest nodeTest, boolean includeSelf) {
        return null;
    }


    @Override
    public AtomicSequence atomize() {
        if (value == null) {
            value = DomainConversion.convert(attribute.getValue());
        }
        return value;
    }

    @Override
    public SchemaType getSchemaType() {
        return schemaType;
    }


    @Override
    public Attribute getUnderlyingNode() {
        return attribute;
    }


    @Override
    public int getNodeKind() {
        return Type.ATTRIBUTE;
    }


    @Override
    public int compareOrder(NodeInfo other) {
        // attributes have no order in the xdm
        return 0;
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
    public String getPrefix() {
        return "";
    }


    @Override
    public NodeInfo getParent() {
        return parent;
    }


    @Override
    public void generateId(FastStringBuffer buffer) {
        buffer.append(Integer.toString(hashCode()));
    }


    @Override
    public CharSequence getStringValueCS() {
        return attribute.getStringValue();
    }
}
