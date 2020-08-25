/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.Array;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.EnumConstant;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.ExecutableStub.FormalParamStub;

class MethodInfoVisitor extends MethodVisitor {

    private final ExecutableStub execStub;
    private List<JFormalParamSymbol> params = Collections.emptyList();
    private SymbolicValue defaultAnnotValue;

    public MethodInfoVisitor(ExecutableStub execStub) {
        super(AsmSymbolResolver.ASM_API_V);
        this.execStub = execStub;
    }

    @Override
    public void visitParameter(String name, int access) {
        if (params.isEmpty()) {
            params = new ArrayList<>(); // make it writable
        }
        boolean isFinal = (access & Opcodes.ACC_FINAL) != 0;
        FormalParamStub paramStub = execStub.new FormalParamStub(params.size(), isFinal, name);
        params.add(paramStub);
        super.visitParameter(name, access);
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return new DefaultAnnotValueVisitor();
    }


    @Override
    public void visitEnd() {
        execStub.setParams(params);
        execStub.setDefaultAnnotValue(defaultAnnotValue);
        super.visitEnd();
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
            result = new EnumConstant(descriptor, value);
        }

        @Override
        public void visit(String name, Object value) {
            result = SymbolicValue.ofAtom(value);
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
            arrayElements.add(new EnumConstant(descriptor, value));
        }

        @Override
        public void visit(String name, Object value) {
            arrayElements.add(SymbolicValue.ofAtom(value));
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            return new ArrayValueBuilder(new ArrayList<>(), this.arrayElements::add);
        }

        @Override
        public void visitEnd() {
            finisher.accept(new Array(arrayElements));
        }
    }
}
