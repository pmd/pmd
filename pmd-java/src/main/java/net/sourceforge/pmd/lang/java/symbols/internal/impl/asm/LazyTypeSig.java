/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.asm;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;

class LazyTypeSig {

    private final String sig;
    private final ClassStub ctx;
    private JTypeMirror parsed;

    LazyTypeSig(ClassStub ctx,
                String descriptor,
                @Nullable String signature) {
        this.ctx = ctx;
        this.sig = signature == null ? descriptor : signature;
    }

    JTypeMirror get() {
        if (parsed == null) {
            parsed = ctx.sigParser().parseFieldType(ctx.getLexicalScope(), sig);
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
}
