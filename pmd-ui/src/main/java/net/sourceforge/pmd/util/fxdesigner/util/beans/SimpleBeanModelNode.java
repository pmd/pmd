/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsPersistenceUtil.PersistentSequence;


/**
 * Represents a node in the settings owner tree, and stores the values of the properties that
 * should be saved and restored. A node can have other nodes as children, in which case they are
 * identified using their type at restore time. To persist the properties of multiple children with
 * the same type, see {@link PersistentSequence} and {@link BeanModelNodeSeq}.
 *
 * <p>This intermediary representation decouples the XML representation from the business logic,
 * allowing several parsers / serializers to coexist for different versions of the schema.
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
public class SimpleBeanModelNode extends BeanModelNode {


    private final Class<?> nodeType;

    private final Map<String, Object> propertyValues = new HashMap<>();
    private final Map<String, Class<?>> propertyTypes = new HashMap<>();
    private final Map<Class<?>, BeanModelNode> children = new HashMap<>();
    private final Set<BeanModelNodeSeq<?>> sequenceProperties = new HashSet<>();


    public SimpleBeanModelNode(Class<?> nodeType) {
        this.nodeType = nodeType;
    }


    /**
     * Add one more property with its value.
     *
     * @param propertyKey Unique name identifying the property.
     * @param value       Value
     * @param type        Type of the property
     */
    public void addProperty(String propertyKey, Object value, Class<?> type) {
        propertyValues.put(propertyKey, value);
        propertyTypes.put(propertyKey, type);
    }


    /**
     * Add a sequence of nodes as a child of this node.
     *
     * @param seq Sequence of nodes
     */
    public void addChild(BeanModelNodeSeq<?> seq) {
        sequenceProperties.add(seq);
    }


    /**
     * Add a node to the children of this node.
     *
     * @param child Node
     */
    public void addChild(SimpleBeanModelNode child) {
        children.put(child.nodeType, child);
    }


    /** Returns a map of property names to their value. */
    public Map<String, Object> getSettingsValues() {
        return Collections.unmodifiableMap(propertyValues);
    }


    /** Returns a map of property names to their type. */
    public Map<String, Class<?>> getSettingsTypes() {
        return Collections.unmodifiableMap(propertyTypes);
    }


    /** Returns a map of children by type. */
    public Map<Class<?>, BeanModelNode> getChildrenByType() {
        return Collections.unmodifiableMap(children);
    }


    @Override
    public List<? extends BeanModelNode> getChildrenNodes() {
        Set<BeanModelNode> allChildren = new HashSet<>(children.values());
        allChildren.addAll(sequenceProperties);
        return new ArrayList<>(allChildren);
    }


    /** Gets the sequences of nodes registered as children. */
    public Set<BeanModelNodeSeq<?>> getSequenceProperties() {
        return sequenceProperties;
    }


    /** Get the type of the settings owner represented by this node. */
    public Class<?> getNodeType() {
        return nodeType;
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
        SimpleBeanModelNode that = (SimpleBeanModelNode) o;
        return Objects.equals(nodeType, that.nodeType)
                && Objects.equals(propertyValues, that.propertyValues)
                && Objects.equals(propertyTypes, that.propertyTypes)
                && Objects.equals(children, that.children)
                && Objects.equals(sequenceProperties, that.sequenceProperties);
    }


    @Override
    public int hashCode() {
        return Objects.hash(nodeType, propertyValues, propertyTypes, children, sequenceProperties);
    }
}
