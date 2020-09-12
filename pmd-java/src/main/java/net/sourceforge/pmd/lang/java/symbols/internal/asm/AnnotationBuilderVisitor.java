/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;

class AnnotationBuilderVisitor extends SymbolicValueBuilder {

    final SymbolicAnnotationImpl annot;
    private final AnnotationOwner owner;

    public AnnotationBuilderVisitor(AnnotationOwner owner, AsmSymbolResolver resolver, boolean visible, String descriptor) {
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
}
