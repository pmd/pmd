/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.typeresolution.JavaTypeDefinition;

/**
 * An extension of the SimpleJavaNode which implements the TypeNode interface.
 *
 * @see AbstractJavaNode
 * @see TypeNode
 */
public abstract class AbstractJavaTypeNode extends AbstractJavaNode implements TypeNode {
    private JavaTypeDefinition typeDefinition;

    public AbstractJavaTypeNode(int i) {
        super(i);
    }

    public AbstractJavaTypeNode(JavaParser p, int i) {
        super(p, i);
    }

    @Override
    public Class<?> getType() {
        if (typeDefinition != null) {
            return typeDefinition.getType();
        }

        return null;
    }

    @Override
    public void setType(Class<?> type) {
        if (typeDefinition == null) {
            typeDefinition = new JavaTypeDefinition(type);
        } else {
            typeDefinition.setClazz(type);
        }
    }

    @Override
    public JavaTypeDefinition getTypeDefinition() {
        return typeDefinition;
    }

    @Override
    public void setTypeDefinition(JavaTypeDefinition typeDefinition) {
        this.typeDefinition = typeDefinition;
    }
}
