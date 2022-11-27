/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.TypePath;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;

class AnnotationBuilderVisitor extends SymbolicValueBuilder {

    final SymbolicAnnotationImpl annot;
    private final AnnotationOwner owner;

    AnnotationBuilderVisitor(AnnotationOwner owner, AsmSymbolResolver resolver, boolean visible, String descriptor) {
        super(resolver);
        this.annot = new SymbolicAnnotationImpl(resolver, visible, descriptor);
        this.owner = owner;
    }

    @Override
    protected void acceptValue(String name, SymbolicValue v) {
        annot.addAttribute(name, v);
    }

    @Override
    public void visitEnd() {
        owner.addAnnotation(annot);
    }

    static class TypeAnnotBuilderImpl extends SymbolicValueBuilder {

        private final TypeAnnotationReceiver owner;
        private final TypePath path;
        private final SymbolicAnnotationImpl annot;

        TypeAnnotBuilderImpl(AsmSymbolResolver resolver, TypeAnnotationReceiver owner, @Nullable TypePath path,
                             boolean visible, String descriptor) {
            super(resolver);
            this.owner = owner;
            this.path = path;
            annot = new SymbolicAnnotationImpl(resolver, visible, descriptor);
        }

        @Override
        protected void acceptValue(String name, SymbolicValue v) {
            annot.addAttribute(name, v);
        }

        @Override
        public void visitEnd() {
            owner.addTypeAnnotation(path, annot);
        }
    }
}
