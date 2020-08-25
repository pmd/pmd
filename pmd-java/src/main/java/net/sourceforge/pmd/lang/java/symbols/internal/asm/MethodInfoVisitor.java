/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymArray;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymEnum;
import net.sourceforge.pmd.lang.java.symbols.AnnotationUtils;

class MethodInfoVisitor extends MethodVisitor {

    private final ExecutableStub execStub;
    private SymbolicValue defaultAnnotValue;

    MethodInfoVisitor(ExecutableStub execStub) {
        super(AsmSymbolResolver.ASM_API_V);
        this.execStub = execStub;
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return new DefaultAnnotValueVisitor();
    }


    @Override
    public void visitEnd() {
        execStub.setDefaultAnnotValue(defaultAnnotValue);
        super.visitEnd();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return super.visitAnnotation(descriptor, visible);
    }

    private class DefaultAnnotValueVisitor extends SymbolicValueBuilder {

        @Override
        public void visitEnd() {
            assert this.result != null;
            defaultAnnotValue = this.result;
        }
    }

    private static class SymbolicValueBuilder extends AnnotationVisitor {

        SymbolicValue result;

        SymbolicValueBuilder() {
            super(AsmSymbolResolver.ASM_API_V);
        }


        @Override
        public void visitEnum(String name, String descriptor, String value) {
            result = new SymEnum(descriptor, value);
        }

        @Override
        public void visit(String name, Object value) {
            result = AnnotationUtils.symValueFor(value);
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            return new ArrayValueBuilder(new ArrayList<>(), v -> this.result = v);
        }
    }


    static class ArrayValueBuilder extends AnnotationVisitor {

        private final List<SymbolicValue> arrayElements;
        private final Consumer<SymbolicValue> finisher;

        ArrayValueBuilder(List<SymbolicValue> arrayElements, Consumer<SymbolicValue> finisher) {
            super(AsmSymbolResolver.ASM_API_V);
            this.arrayElements = arrayElements;
            this.finisher = finisher;
        }

        @Override
        public void visitEnum(String name, String descriptor, String value) {
            arrayElements.add(new SymEnum(descriptor, value));
        }

        @Override
        public void visit(String name, Object value) {
            arrayElements.add(AnnotationUtils.symValueFor(value));
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            return new ArrayValueBuilder(new ArrayList<>(), this.arrayElements::add);
        }

        @Override
        public void visitEnd() {
            finisher.accept(new SymArray(arrayElements));
        }
    }
}
