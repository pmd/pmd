/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;


import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;

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
        return new DefaultAnnotValueVisitor(execStub.getResolver());
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        return new ParameterAnnotVisitor(execStub.getResolver(), parameter, visible, descriptor);
    }

    @Override
    public void visitEnd() {
        execStub.setDefaultAnnotValue(defaultAnnotValue);
        execStub.finalizeVisit();
        super.visitEnd();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return new AnnotationBuilderVisitor(execStub, execStub.getResolver(), visible, descriptor);
    }

    private class DefaultAnnotValueVisitor extends SymbolicValueBuilder {

        DefaultAnnotValueVisitor(AsmSymbolResolver resolver) {
            super(resolver);
        }

        @Override
        public void visitEnd() {
            assert this.result != null;
            defaultAnnotValue = this.result;
        }
    }

    private class ParameterAnnotVisitor extends SymbolicValueBuilder {

        private final SymbolicAnnotationImpl annot;
        private final int paramIndex;
        
        ParameterAnnotVisitor(AsmSymbolResolver resolver, int paramIndex, boolean visible, String descriptor) {
            super(resolver);
            this.annot = new SymbolicAnnotationImpl(resolver, visible, descriptor);
            this.paramIndex = paramIndex;
        }
        
        @Override
        protected void acceptValue(String name, SymbolicValue v) {
            annot.addAttribute(name, v);
        }

        
        @Override
        public void visitEnd() {
            execStub.addParameterAnnotation(paramIndex, annot);
        }
    }
}
