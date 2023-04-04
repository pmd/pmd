/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.TypePath;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.TypeAnnotationHelper.TypeAnnotationSet;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;

class LazyTypeSig {

    private final String sig;
    private final ClassStub ctx;
    private JTypeMirror parsed;
    private TypeAnnotationSet typeAnnots;

    LazyTypeSig(ClassStub ctx,
                String descriptor,
                @Nullable String signature) {
        this.ctx = ctx;
        this.sig = signature == null ? descriptor : signature;
    }

    JTypeMirror get() {
        if (parsed == null) {
            parsed = ctx.sigParser().parseFieldType(ctx.getLexicalScope(), sig);
            if (typeAnnots != null) {
                parsed = typeAnnots.decorate(parsed);
                typeAnnots = null; // forget about them
            }
        }
        return parsed;
    }


    JTypeMirror get(Substitution subst) {
        return get().subst(subst);
    }


    @Override
    public String toString() {
        return sig;
    }

    public void addTypeAnnotation(@Nullable TypePath path, SymAnnot annot) {
        if (parsed != null) {
            throw new IllegalStateException("Must add annotations before the field type is parsed.");
        }
        if (typeAnnots == null) {
            typeAnnots = new TypeAnnotationSet();
        }
        typeAnnots.add(path, annot);
    }


}
