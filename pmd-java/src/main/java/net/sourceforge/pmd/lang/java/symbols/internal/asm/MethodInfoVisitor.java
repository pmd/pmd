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


}
