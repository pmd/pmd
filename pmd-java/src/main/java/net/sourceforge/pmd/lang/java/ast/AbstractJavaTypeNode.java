/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

/**
 * An extension of the SimpleJavaNode which implements the TypeNode interface.
 *
 * @see AbstractJavaNode
 * @see TypeNode
 */
@Deprecated
@InternalApi
public abstract class AbstractJavaTypeNode extends AbstractJavaNode implements TypeNode {

    private JavaTypeDefinition typeDefinition;

    @InternalApi
    @Deprecated
    public AbstractJavaTypeNode(int i) {
        super(i);
    }

    @InternalApi
    @Deprecated
    public AbstractJavaTypeNode(JavaParser p, int i) {
        super(p, i);
    }

    @Override
    @Nullable
    public Class<?> getType() {
        return typeDefinition == null ? null : typeDefinition.getType();
    }

    @InternalApi
    @Deprecated
    @Override
    public void setType(@Nullable Class<?> type) {
        typeDefinition = JavaTypeDefinition.forClass(type);
    }

    @Override
    @Nullable
    public JavaTypeDefinition getTypeDefinition() {
        return typeDefinition;
    }

    @InternalApi
    @Deprecated
    @Override
    public void setTypeDefinition(@Nullable JavaTypeDefinition typeDefinition) {
        this.typeDefinition = typeDefinition;
    }
}
