/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

abstract class MemberStubBase implements JAccessibleElementSymbol, AsmStub, AnnotationOwner {

    private final ClassStub classStub;
    private final String simpleName;
    private final int accessFlags;

    private List<SymAnnot> annotations = Collections.emptyList();


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
        if (annotations.isEmpty()) {
            annotations = new ArrayList<>(1); // make writable
        }
        annotations.add(annot);
    }

    protected void finalizeVisit() {
        if (!annotations.isEmpty()) {
            annotations = Collections.unmodifiableList(annotations);
        }
    }

    @Override
    public List<SymAnnot> getDeclaredAnnotations() {
        return annotations;
    }
}
