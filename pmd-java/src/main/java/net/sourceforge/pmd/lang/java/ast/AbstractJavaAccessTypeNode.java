/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

abstract class AbstractJavaAccessTypeNode extends AbstractJavaAccessNode implements TypeNode {

    /**
     * Type definition, used to get the type of the node.
     */
    protected JavaTypeDefinition typeDefinition;

    AbstractJavaAccessTypeNode(int i) {
        super(i);
    }

    AbstractJavaAccessTypeNode(JavaParser parser, int i) {
        super(parser, i);
    }

    @Override
    public Class<?> getType() {
        if (typeDefinition != null) {
            return typeDefinition.getType();
        }

        return null;
    }

    @InternalApi
    @Deprecated
    @Override
    public void setType(Class<?> type) {
        typeDefinition = JavaTypeDefinition.forClass(type);
    }

    @Override
    public JavaTypeDefinition getTypeDefinition() {
        return typeDefinition;
    }

    @InternalApi
    @Deprecated
    @Override
    public void setTypeDefinition(JavaTypeDefinition typeDefinition) {
        this.typeDefinition = typeDefinition;
    }
}
