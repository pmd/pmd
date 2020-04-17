/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.saxon;


import net.sf.saxon.om.FingerprintedNode;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SiblingCountingNode;
import net.sf.saxon.om.VirtualNode;

abstract class BaseNodeInfo extends AbstractNodeInfo implements VirtualNode, SiblingCountingNode, FingerprintedNode {

    private final int nodeKind;
    private final NamePool namePool;
    private final int fingerprint;

    private final ElementNode parent;

    BaseNodeInfo(int nodeKind, NamePool namePool, String localName, ElementNode parent) {
        this.nodeKind = nodeKind;
        this.namePool = namePool;
        this.fingerprint = namePool.allocate("", "", localName);
        this.parent = parent;
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
    public final NodeInfo getParent() {
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
