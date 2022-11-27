/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.TypePath;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.types.JClassType;
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

    public void addTypeAnnotation(TypePath path, SymAnnot annot) {
        if (parsed != null) {
            throw new IllegalStateException("Must add annotations before the field type is parsed.");
        }
        if (typeAnnots == null) {
            typeAnnots = new TypeAnnotationSet();
        }
        typeAnnots.add(path, annot);
    }


    static final class TypeAnnotationSet {

        private final Map<TypePath, List<SymAnnot>> pathAndAnnot = new HashMap<>();

        void add(TypePath path, SymAnnot annot) {
            pathAndAnnot.computeIfAbsent(path, k -> new ArrayList<>(1)).add(annot);
        }

        JTypeMirror decorate(JTypeMirror base) {
            for (Map.Entry<TypePath, List<SymAnnot>> pair : pathAndAnnot.entrySet()) {
                TypePath path = pair.getKey();

                base = resolvePath(base, path, pair.getValue());
            }
            return base;
        }

        private JTypeMirror resolvePath(JTypeMirror t, TypePath path, List<SymAnnot> annot) {
            if (path == null) {
                return t.withAnnotations(annot);
            }

            return resolvePathStep(t, path, 0, annot);
        }

        private JTypeMirror resolvePathStep(JTypeMirror t, TypePath path, int i, List<SymAnnot> annots) {
            if (i == path.getLength()) {
                return t.withAnnotations(annots);
            }
            switch (path.getStep(i)) {
            case TypePath.TYPE_ARGUMENT:
                if (t instanceof JClassType) {
                    int typeArgIndex = path.getStepArgument(i);
                    JTypeMirror arg = ((JClassType) t).getTypeArgs().get(typeArgIndex);
                    JTypeMirror newArg = resolvePathStep(arg, path, i + 1, annots);
                    List<JTypeMirror> newArgs = replaceAtIndex(((JClassType) t).getTypeArgs(), typeArgIndex, newArg);
                    return ((JClassType) t).withTypeArguments(newArgs);
                }
            case TypePath.INNER_TYPE:
            case TypePath.ARRAY_ELEMENT:
            case TypePath.WILDCARD_BOUND:
                throw new NotImplementedException("TODO type annotations with paths");
            }
            return t;
        }

        private static @NonNull List<JTypeMirror> replaceAtIndex(List<JTypeMirror> typeArgs, int typeArgIndex, JTypeMirror newArg) {
            if (typeArgs.size() == 1 && typeArgIndex == 0) {
                return Collections.singletonList(newArg);
            }
            List<JTypeMirror> newArgs = new ArrayList<>(typeArgs);
            newArgs.set(typeArgIndex, newArg);
            return newArgs;
        }
    }


}
