/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.typeresolution.TypeWrapper;

/**
 * An extension of the SimpleJavaNode which implements the TypeNode interface.
 *
 * @see AbstractJavaNode
 * @see TypeNode
 */
public abstract class AbstractJavaTypeNode extends AbstractJavaNode implements TypeNode {
    private TypeWrapper typeWrapper;

    public AbstractJavaTypeNode(int i) {
        super(i);
    }

    public AbstractJavaTypeNode(JavaParser p, int i) {
        super(p, i);
    }

    @Override
    public Class<?> getType() {
        if (typeWrapper != null) {
            return typeWrapper.getType();
        }

        return null;
    }

    @Override
    public void setType(Class<?> type) {
        if (typeWrapper == null) {
            typeWrapper = new TypeWrapper(type);
        } else {
            typeWrapper.setClazz(type);
        }
    }

    @Override
    public TypeWrapper getTypeWrapper() {
        return typeWrapper;
    }

    @Override
    public void setTypeWrapper(TypeWrapper typeWrapper) {
        this.typeWrapper = typeWrapper;
    }
}
