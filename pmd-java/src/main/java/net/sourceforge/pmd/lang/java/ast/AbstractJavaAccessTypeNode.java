/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

abstract class AbstractJavaAccessTypeNode extends AbstractJavaAccessNode implements TypeNode {

    /**
     * Type definition, used to get the type of the node.
     */
    protected JavaTypeDefinition typeDefinition;

    AbstractJavaAccessTypeNode(int i) {
        super(i);
    }

    @Override
    public Class<?> getType() {
        if (typeDefinition != null) {
            return typeDefinition.getType();
        }

        return null;
    }

    @Override
    public JavaTypeDefinition getTypeDefinition() {
        return typeDefinition;
    }

    void setTypeDefinition(JavaTypeDefinition typeDefinition) {
        this.typeDefinition = typeDefinition;
    }
}
