/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;


import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;

class MethodInfoVisitor extends MethodVisitor {

    private final ExecutableStub execStub;
    private SymbolicValue defaultAnnotValue;

    MethodInfoVisitor(ExecutableStub execStub) {
        super(AsmSymbolResolver.ASM_API_V);
        this.execStub = execStub;
    }
    
    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return new SymbolicValueBuilder(execStub.getResolver()) {
            @Override
            protected void acceptValue(String name, SymbolicValue v) {
                defaultAnnotValue = v;
            }
        };
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        return new SymbolicValueBuilder(execStub.getResolver()) {
            private final SymbolicAnnotationImpl annot = new SymbolicAnnotationImpl(getResolver(), visible, descriptor);

            @Override
            protected void acceptValue(String name, SymbolicValue v) {
                annot.addAttribute(name, v);
            }

            @Override
            public void visitEnd() {
                execStub.addParameterAnnotation(parameter, annot);
            }
        };
    }

    @Override
    public void visitEnd() {
        execStub.setDefaultAnnotValue(defaultAnnotValue);
        super.visitEnd();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return new AnnotationBuilderVisitor(execStub, execStub.getResolver(), visible, descriptor);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return new AnnotationBuilderVisitor.TypeAnnotBuilderImpl(execStub.getResolver(), execStub, typeRef, typePath, visible, descriptor);
    }

}
