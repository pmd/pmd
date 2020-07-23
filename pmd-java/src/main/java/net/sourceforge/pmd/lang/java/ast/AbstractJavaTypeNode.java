/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.internal.ast.LazyTypeResolver;

/**
 * An extension of the SimpleJavaNode which implements the TypeNode interface.
 *
 * @see AbstractJavaNode
 * @see TypeNode
 */
abstract class AbstractJavaTypeNode extends AbstractJavaNode implements TypeNode {

    private JavaTypeDefinition typeDefinition;
    protected JTypeMirror typeMirror;

    AbstractJavaTypeNode(int i) {
        super(i);
    }

    @Override
    @Nullable
    public JavaTypeDefinition getTypeDefinition() {
        return typeDefinition;
    }

    @Override
    @NonNull
    public JTypeMirror getTypeMirror() {
        if (typeMirror == null) {

            LazyTypeResolver resolver = getRoot().getLazyTypeResolver();
            assert resolver != null : "Null type resolver!";

            JTypeMirror result = this.acceptVisitor(resolver, null);

            this.typeMirror = result != null ? result : resolver.getTypeSystem().UNRESOLVED_TYPE;
        }
        return typeMirror;
    }

    JTypeMirror getTypeMirrorInternal() {
        return typeMirror;
    }

    void setTypeMirror(JTypeMirror mirror) {
        typeMirror = mirror;
    }

    void setTypeDefinition(@Nullable JavaTypeDefinition typeDefinition) {
        this.typeDefinition = typeDefinition;
    }


}
