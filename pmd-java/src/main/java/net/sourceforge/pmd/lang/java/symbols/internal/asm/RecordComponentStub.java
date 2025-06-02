/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;

import net.sourceforge.pmd.lang.java.symbols.JRecordComponentSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;

/**
 * @author Clément Fournier
 */
class RecordComponentStub extends MemberStubBase implements JRecordComponentSymbol, TypeAnnotationReceiver {

    private final LazyTypeSig type;


    RecordComponentStub(ClassStub classStub, String name, String descriptor, String signature) {
        super(classStub, name, JRecordComponentSymbol.RECORD_COMPONENT_MODIFIERS);
        this.type = new LazyTypeSig(classStub, descriptor, signature);
    }


    @Override
    public JTypeMirror getTypeMirror(Substitution substitution) {
        return type.get(substitution);
    }


    @Override
    public void acceptTypeAnnotation(int typeRef, @Nullable TypePath path, SymAnnot annot) {
        assert new TypeReference(typeRef).getSort() == TypeReference.FIELD : typeRef;
        this.type.addTypeAnnotation(path, annot);
    }
}
