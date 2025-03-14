/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymArray;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymEnum;

abstract class SymbolicValueBuilder extends AnnotationVisitor {

    private final AsmSymbolResolver resolver;

    SymbolicValueBuilder(AsmSymbolResolver resolver) {
        super(AsmSymbolResolver.ASM_API_V);
        this.resolver = resolver;
    }

    AsmSymbolResolver getResolver() {
        return resolver;
    }

    protected abstract void acceptValue(String name, SymbolicValue v);


    @Override
    public void visitEnum(String name, String descriptor, String value) {
        acceptValue(name, SymEnum.fromTypeDescriptor(resolver.getTypeSystem(), descriptor, value));
    }

    @Override
    public void visit(String name, Object value) {
        if (value instanceof Type) {
            acceptValue(name, SymbolicValue.SymClass.ofBinaryName(resolver.getTypeSystem(), ((Type) value).getClassName()));
        } else {
            acceptValue(name, SymbolicValue.of(resolver.getTypeSystem(), value));
        }
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return new ArrayValueBuilder(resolver, new ArrayList<>(), v -> acceptValue(name, v));
    }

    static class ArrayValueBuilder extends SymbolicValueBuilder {

        private final List<SymbolicValue> arrayElements;
        private final Consumer<SymbolicValue> finisher;

        ArrayValueBuilder(AsmSymbolResolver resolver, List<SymbolicValue> arrayElements, Consumer<SymbolicValue> finisher) {
            super(resolver);
            this.arrayElements = arrayElements;
            this.finisher = finisher;
        }

        @Override
        protected void acceptValue(String name, SymbolicValue v) {
            arrayElements.add(v);
        }

        @Override
        public void visitEnd() {
            finisher.accept(SymArray.forElements(arrayElements));
        }
    }
}
