/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsPersistenceUtil.PersistentSequence;


/**
 * Represents an indexed list of nodes sharing the same type.
 * This type of node is flagged with a {@link PersistentSequence},
 * which is applied to a getter of a collection.
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
public class BeanModelNodeSeq<T extends SimpleBeanModelNode> extends BeanModelNode {

    private final String propertyName;
    private final List<T> children = new ArrayList<>();


    public BeanModelNodeSeq(String name) {
        this.propertyName = name;
    }


    public void addChild(T node) {
        children.add(node);
    }


    /** Returns the elements of the sequence. */
    @Override
    public List<? extends SimpleBeanModelNode> getChildrenNodes() {
        return children;
    }


    /** Returns the name of the property that contains the collection. */
    public String getPropertyName() {
        return propertyName;
    }


    @Override
    protected <U> void accept(BeanNodeVisitor<U> visitor, U data) {
        visitor.visit(this, data);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BeanModelNodeSeq<?> that = (BeanModelNodeSeq<?>) o;
        return Objects.equals(propertyName, that.propertyName)
                && Objects.equals(children, that.children);
    }


    @Override
    public int hashCode() {

        return Objects.hash(propertyName, children);
    }

}
