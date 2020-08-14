/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.internal.ast.LazyTypeResolver;

/**
 * An extension of the SimpleJavaNode which implements the TypeNode interface.
 *
 * @see AbstractJavaNode
 * @see TypeNode
 */
abstract class AbstractJavaTypeNode extends AbstractJavaNode implements TypeNode {

    protected JTypeMirror typeMirror;

    AbstractJavaTypeNode(int i) {
        super(i);
    }

    @Override
    public @NonNull JTypeMirror getTypeMirror() {
        if (typeMirror == null) {
            LazyTypeResolver resolver = getRoot().getLazyTypeResolver();
            typeMirror = this.acceptVisitor(resolver, null);
        }
        return typeMirror;
    }

    JTypeMirror getTypeMirrorInternal() {
        return typeMirror;
    }

    void setTypeMirror(JTypeMirror mirror) {
        typeMirror = mirror;
    }


}
