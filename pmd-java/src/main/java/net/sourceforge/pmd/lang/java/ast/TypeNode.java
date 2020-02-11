/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

/**
 * This interface allows a Java Class to be associated with a node.
 */
public interface TypeNode extends TypedNode {

    /**
     * Get the Java Class associated with this node.
     *
     * @return The Java Class, may return <code>null</code>.
     */
    @Nullable
    default Class<?> getType() {
        JavaTypeDefinition td = getTypeDefinition();
        return td == null ? null : td.getType();
    }


    /**
     * Get the TypeDefinition associated with this node. The Class object
     * contained in the TypeDefinition will always be equal to that which
     * is returned by <code>getType()</code>.
     *
     * @return The TypeDefinition, may return <code>null</code>
     */
    @Nullable
    JavaTypeDefinition getTypeDefinition();


}
