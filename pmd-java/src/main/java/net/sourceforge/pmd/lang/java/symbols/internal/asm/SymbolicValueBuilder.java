/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.objectweb.asm.AnnotationVisitor;

import net.sourceforge.pmd.lang.java.symbols.AnnotationUtils;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymArray;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymEnum;

/**
 *
 */
class SymbolicValueBuilder extends AnnotationVisitor {

    SymbolicValue result;

    SymbolicValueBuilder() {
        super(AsmSymbolResolver.ASM_API_V);
    }

    protected void acceptValue(String name, SymbolicValue v) {
        result = v;
    }


    @Override
    public void visitEnum(String name, String descriptor, String value) {
        acceptValue(name, new SymEnum(descriptor, value));
    }

    @Override
    public void visit(String name, Object value) {
        acceptValue(name, AnnotationUtils.symValueFor(value));
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return new ArrayValueBuilder(new ArrayList<>(), v -> acceptValue(name, v));
    }

    static class ArrayValueBuilder extends SymbolicValueBuilder {

        private final List<SymbolicValue> arrayElements;
        private final Consumer<SymbolicValue> finisher;

        ArrayValueBuilder(List<SymbolicValue> arrayElements, Consumer<SymbolicValue> finisher) {
            super();
            this.arrayElements = arrayElements;
            this.finisher = finisher;
        }

        @Override
        protected void acceptValue(String name, SymbolicValue v) {
            arrayElements.add(v);
        }

        @Override
        public void visitEnd() {
            finisher.accept(new SymArray(arrayElements));
        }
    }
}
