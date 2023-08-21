/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

abstract class MemberStubBase implements JAccessibleElementSymbol, AsmStub, AnnotationOwner {

    private final ClassStub classStub;
    private final String simpleName;
    private final int accessFlags;

    private PSet<SymAnnot> annotations = HashTreePSet.empty();


    protected MemberStubBase(ClassStub classStub, String simpleName, int accessFlags) {
        this.classStub = classStub;
        this.simpleName = simpleName;
        this.accessFlags = accessFlags;
    }

    @Override
    public String getSimpleName() {
        return simpleName;
    }

    @Override
    public TypeSystem getTypeSystem() {
        return classStub.getTypeSystem();
    }

    @Override
    public AsmSymbolResolver getResolver() {
        return classStub.getResolver();
    }

    @Override
    public int getModifiers() {
        return accessFlags;
    }

    @Override
    public @NonNull ClassStub getEnclosingClass() {
        return classStub;
    }

    @Override
    public void addAnnotation(SymAnnot annot) {
        annotations = annotations.plus(annot);
    }

    @Override
    public PSet<SymAnnot> getDeclaredAnnotations() {
        return annotations;
    }
}
