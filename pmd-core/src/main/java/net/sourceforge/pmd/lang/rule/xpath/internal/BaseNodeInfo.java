/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;


import net.sf.saxon.om.NamePool;
import net.sf.saxon.tree.wrapper.AbstractNodeWrapper;
import net.sf.saxon.tree.wrapper.SiblingCountingNode;

abstract class BaseNodeInfo extends AbstractNodeWrapper implements SiblingCountingNode {

    // It's important that all our NodeInfo implementations share the
    // same getNodeKind implementation, otherwise NameTest spends a lot
    // of time in virtual dispatch
    private final int nodeKind;
    private final NamePool namePool;
    private final int fingerprint;

    protected final AstElementNode parent;

    BaseNodeInfo(int nodeKind, NamePool namePool, String localName, AstElementNode parent) {
        this.nodeKind = nodeKind;
        this.namePool = namePool;
        this.fingerprint = namePool.allocateFingerprint("", localName) & NamePool.FP_MASK;
        this.parent = parent;
    }

    @Override
    public AstDocumentNode getTreeInfo() {
        return (AstDocumentNode) treeInfo;
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
    public final AstElementNode getParent() {
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

}
