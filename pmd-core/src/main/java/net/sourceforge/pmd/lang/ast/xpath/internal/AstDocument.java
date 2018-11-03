/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.internal;

import net.sourceforge.pmd.lang.ast.Node;

import net.sf.saxon.Configuration;
import net.sf.saxon.om.GenericTreeInfo;


/**
 * A wrapper around the root node of an AST, implementing {@link net.sf.saxon.om.TreeInfo}.
 */
public final class AstDocument extends GenericTreeInfo {

    private DeprecatedAttrLogger logger;

    /**
     * Builds an AstDocument, with the given node as the root.
     *
     * @param node          The root AST Node.
     * @param configuration Configuration of the run
     *
     * @see AstNodeWrapper
     */
    public AstDocument(Node node, Configuration configuration) {
        super(configuration);
        setRootNode(new AstNodeWrapper(this, new IdGenerator(), null, node));
    }


    @Override
    public AstNodeWrapper getRootNode() {
        return (AstNodeWrapper) super.getRootNode();
    }

    public void setAttrCtx(DeprecatedAttrLogger attrCtx) {
        this.logger = attrCtx;
    }

    public DeprecatedAttrLogger getLogger() {
        return logger == null ? DeprecatedAttrLogger.noop() : logger;
    }
}
