/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;

import net.sf.saxon.Configuration;
import net.sf.saxon.om.GenericTreeInfo;


/**
 * A wrapper around the root node of an AST, implementing {@link net.sf.saxon.om.TreeInfo}.
 */
public final class AstTreeInfo extends GenericTreeInfo {

    private DeprecatedAttrLogger logger;
    private final Map<Node, AstElementNode> wrapperCache = new LinkedHashMap<Node, AstElementNode>() {
        @Override
        protected boolean removeEldestEntry(Entry eldest) {
            /*
            hit ratio depending on cache size:
            512: 61%
            1024: 75%
            2048: 82%
            unbounded: 85%
             */
            return size() > 1024;
        }
    };

    /**
     * Builds an AstDocument, with the given node as the root.
     *
     * @param node          The root AST Node.
     * @param configuration Configuration of the run
     *
     * @see AstElementNode
     */
    public AstTreeInfo(RootNode node, Configuration configuration) {
        super(configuration);
        MutableInt idGenerator = new MutableInt(1); // 0 is taken by the document node
        setRootNode(new AstDocumentNode(this, idGenerator, node, configuration));
    }

    public AstElementNode findWrapperFor(Node node) {
        AstElementNode element = wrapperCache.get(node);
        if (element == null) {
            element = findWrapperImpl(node);
            wrapperCache.put(node, element);
            assert element.getUnderlyingNode() == node : "Incorrect wrapper " + element + " for " + node;
        }
        return element;
    }

    // for the RootNode, this returns the document node
    private AstElementNode findWrapperImpl(Node node) {
        // find the closest cached ancestor
        AstElementNode cur = getRootNode().getRootElement();
        List<Node> ancestors = new ArrayList<>();
        for (Node ancestor : node.ancestorsOrSelf()) {
            AstElementNode wrappedAncestor = wrapperCache.get(ancestor);
            ancestors.add(ancestor);
            if (wrappedAncestor != null) {
                cur = wrappedAncestor;
                break;
            }
        }

        // then go down the tree from that ancestor

        // note we skip the first, who is the topmost ancestor
        for (int i = ancestors.size() - 2; i >= 0; i--) {
            Node ancestor = ancestors.get(i);
            int idx = ancestor.getIndexInParent();
            if (idx >= cur.getChildren().size()) {
                throw new IllegalArgumentException("Node is not part of this tree " + node);
            }

            cur = cur.getChildren().get(idx);
            wrapperCache.put(ancestor, cur);
        }
        if (cur.getUnderlyingNode() != node) {
            // may happen with the root
            throw new IllegalArgumentException("Node is not part of this tree " + node);
        }
        return cur;
    }

    /**
     * Returns the document node of the tree. Note that this has a single
     * child of element type. Both the document and this element child have
     * the {@link RootNode} as {@link AstElementNode#getUnderlyingNode()}.
     */
    @Override
    public AstDocumentNode getRootNode() {
        return (AstDocumentNode) super.getRootNode();
    }


    public void setAttrCtx(DeprecatedAttrLogger attrCtx) {
        this.logger = attrCtx;
    }

    public DeprecatedAttrLogger getLogger() {
        return logger == null ? DeprecatedAttrLogger.noop() : logger;
    }
}
