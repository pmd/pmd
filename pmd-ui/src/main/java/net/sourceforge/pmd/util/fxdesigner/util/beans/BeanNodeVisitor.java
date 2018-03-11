/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.beans;

/**
 * Implements a visitor pattern over bean nodes. Used to restore properties
 * from a model and build an XML document to represent the model.
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
public abstract class BeanNodeVisitor<T> {

    public void visit(BeanModelNode node, T data) {
        node.childrenAccept(this, data);
    }


    public void visit(BeanModelNodeSeq<?> node, T data) {
        visit((BeanModelNode) node, data);
    }


    public void visit(SimpleBeanModelNode node, T data) {
        visit((BeanModelNode) node, data);
    }


}
