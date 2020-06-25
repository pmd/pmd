/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;


import java.util.List;
import java.util.function.Predicate;

import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.pattern.AnyNodeTest;
import net.sf.saxon.tree.iter.AxisIterator;
import net.sf.saxon.tree.iter.ListIterator;
import net.sf.saxon.tree.iter.ReverseListIterator;
import net.sf.saxon.tree.util.Navigator.AxisFilter;
import net.sf.saxon.tree.wrapper.AbstractNodeWrapper;
import net.sf.saxon.tree.wrapper.SiblingCountingNode;

abstract class BaseNodeInfo extends AbstractNodeWrapper implements SiblingCountingNode {

    // It's important that all our NodeInfo implementations share the
    // same getNodeKind implementation, otherwise NameTest spends a lot
    // of time in virtual dispatch
    private final int nodeKind;
    private final NamePool namePool;
    private final int fingerprint;

    protected final BaseNodeInfo parent;

    BaseNodeInfo(int nodeKind, NamePool namePool, String localName, BaseNodeInfo parent) {
        this.nodeKind = nodeKind;
        this.namePool = namePool;
        this.fingerprint = namePool.allocateFingerprint("", localName) & NamePool.FP_MASK;
        this.parent = parent;
    }

    abstract List<AstElementNode> getChildren();

    @Override
    public AstTreeInfo getTreeInfo() {
        return (AstTreeInfo) treeInfo;
    }

    @Override
    public final String getURI() {
        return "";
    }

    @Override
    public final String getBaseURI() {
        return "";
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public final BaseNodeInfo getParent() {
        return parent;
    }

    @Override
    public final int getFingerprint() {
        return fingerprint;
    }

    @Override
    public final NamePool getNamePool() {
        return namePool;
    }

    @Override
    public final int getNodeKind() {
        return nodeKind;
    }

    protected static AxisIterator filter(Predicate<? super NodeInfo> nodeTest, AxisIterator iter) {
        return nodeTest == null || (nodeTest instanceof AnyNodeTest) ? iter : new AxisFilter(iter, nodeTest);
    }


    static AxisIterator iterateList(List<? extends NodeInfo> nodes) {
        return iterateList(nodes, true);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static AxisIterator iterateList(List<? extends NodeInfo> nodes, boolean forwards) {
        return forwards ? new ListIterator.OfNodes((List) nodes)
                        : new RevListAxisIterator((List) nodes);
    }

    private static class RevListAxisIterator extends ReverseListIterator<NodeInfo> implements AxisIterator {
        RevListAxisIterator(List<NodeInfo> list) {
            super(list);
        }
    }
}
