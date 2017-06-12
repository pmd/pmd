/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.typeresolution.TypeWrapper;

public abstract class AbstractJavaAccessTypeNode extends AbstractJavaAccessNode implements TypeNode {
    private TypeWrapper typeWrapper;

    public AbstractJavaAccessTypeNode(int i) {
        super(i);
    }

    public AbstractJavaAccessTypeNode(JavaParser parser, int i) {
        super(parser, i);
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
