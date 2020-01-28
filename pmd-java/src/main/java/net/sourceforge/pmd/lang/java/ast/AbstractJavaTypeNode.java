/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

/**
 * An extension of the SimpleJavaNode which implements the TypeNode interface.
 *
 * @see AbstractJavaNode
 * @see TypeNode
 */
abstract class AbstractJavaTypeNode extends AbstractJavaNode implements TypeNode {

    private JavaTypeDefinition typeDefinition;

    AbstractJavaTypeNode(int i) {
        super(i);
    }

    @Override
    @Nullable
    public JavaTypeDefinition getTypeDefinition() {
        return typeDefinition;
    }

    void setTypeDefinition(@Nullable JavaTypeDefinition typeDefinition) {
        this.typeDefinition = typeDefinition;
    }
}
