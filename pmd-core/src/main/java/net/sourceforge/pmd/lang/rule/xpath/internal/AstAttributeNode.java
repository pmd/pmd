/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import net.sourceforge.pmd.lang.rule.xpath.Attribute;

import net.sf.saxon.om.AtomicSequence;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.tree.iter.AxisIterator;
import net.sf.saxon.tree.iter.EmptyIterator;
import net.sf.saxon.tree.util.FastStringBuffer;
import net.sf.saxon.tree.util.Navigator;
import net.sf.saxon.tree.wrapper.SiblingCountingNode;
import net.sf.saxon.type.SchemaType;
import net.sf.saxon.type.Type;


/**
 * @since 7.0.0
 */
class AstAttributeNode extends BaseNodeInfo implements SiblingCountingNode {


    private final Attribute attribute;
    private AtomicSequence value;
    private final SchemaType schemaType;
    private final int siblingPosition;


    AstAttributeNode(AstElementNode parent, Attribute attribute, int siblingPosition) {
        super(Type.ATTRIBUTE, parent.getNamePool(), attribute.getName(), parent);
        this.attribute = attribute;
        this.schemaType = DomainConversion.buildType(attribute.getType());
        this.siblingPosition = siblingPosition;
        this.treeInfo = parent.getTreeInfo();
    }

    @Override
    List<AstElementNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public int getSiblingPosition() {
        return siblingPosition;
    }


    @Override
    protected AxisIterator iterateAttributes(Predicate<? super NodeInfo> nodeTest) {
        return EmptyIterator.ofNodes();
    }

    @Override
    protected AxisIterator iterateChildren(Predicate<? super NodeInfo> nodeTest) {
        return EmptyIterator.ofNodes();
    }

    @Override
    protected AxisIterator iterateSiblings(Predicate<? super NodeInfo> nodeTest, boolean forwards) {
        return EmptyIterator.ofNodes();
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
    public int compareOrder(NodeInfo other) {
        if (other instanceof SiblingCountingNode) {
            return Navigator.compareOrder(this, (SiblingCountingNode) other);
        }
        throw new UnsupportedOperationException();
    }


    @Override
    public String getLocalPart() {
        return attribute.getName();
    }


    @Override
    public void generateId(FastStringBuffer buffer) {
        buffer.append(Integer.toString(hashCode()));
    }


    @Override
    public CharSequence getStringValueCS() {
        getTreeInfo().getLogger().recordUsageOf(attribute);
        return attribute.getStringValue();
    }
}
