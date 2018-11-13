/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.beans;

import java.util.Collections;
import java.util.List;


/**
 * Represents a node in the settings model. The settings model is a
 * tree of such nodes, mirroring the state hierarchy of the application.
 *
 * <p>Each node can be serialised to XML.
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
public abstract class BeanModelNode {

    /** Makes the children accept the visitor. */
    public <T> void childrenAccept(BeanNodeVisitor<T> visitor, T data) {
        for (BeanModelNode child : getChildrenNodes()) {
            child.accept(visitor, data);
        }
    }


    /** Accepts a visitor. */
    protected abstract <T> void accept(BeanNodeVisitor<T> visitor, T data);


    public List<? extends BeanModelNode> getChildrenNodes() {
        return Collections.emptyList();
    }
}
