/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;

import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.internal.SymbolEquality;
import net.sourceforge.pmd.lang.java.symbols.internal.SymbolToStrings;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;

class FieldStub extends MemberStubBase implements JFieldSymbol, TypeAnnotationReceiver {

    private final LazyTypeSig type;
    private final @Nullable Object constValue;

    FieldStub(ClassStub classStub,
              String name,
              int accessFlags,
              String descriptor,
              String signature,
              @Nullable Object constValue) {
        super(classStub, name, accessFlags);
        this.type = new LazyTypeSig(classStub, descriptor, signature);
        this.constValue = constValue;
    }

    @Override
    public void acceptTypeAnnotation(int typeRef, @Nullable TypePath path, SymAnnot annot) {
        assert new TypeReference(typeRef).getSort() == TypeReference.FIELD : typeRef;
        this.type.addTypeAnnotation(path, annot);
    }

    @Override
    public @Nullable Object getConstValue() {
        return constValue;
    }

    @Override
    public boolean isEnumConstant() {
        return (getModifiers() & Opcodes.ACC_ENUM) != 0;
    }

    @Override
    public JTypeMirror getTypeMirror(Substitution subst) {
        return type.get(subst);
    }

    @Override
    public String toString() {
        return SymbolToStrings.ASM.toString(this);
    }

    @Override
    public int hashCode() {
        return SymbolEquality.FIELD.hash(this);
    }

    @Override
    public boolean equals(Object obj) {
        return SymbolEquality.FIELD.equals(this, obj);
    }

}
