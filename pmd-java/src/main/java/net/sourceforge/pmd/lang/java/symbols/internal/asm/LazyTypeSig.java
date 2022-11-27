/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.TypePath;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.types.JArrayType;
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

        private final Map<ComparableTypePath, List<SymAnnot>> pathAndAnnot = new HashMap<>();

        void add(TypePath path, SymAnnot annot) {
            pathAndAnnot.computeIfAbsent(new ComparableTypePath(path), k -> new ArrayList<>(1)).add(annot);
        }

        /** TypePath does not implement equals or hashcode so we wrap it. */
        static class ComparableTypePath {

            String toString;
            final TypePath path;

            ComparableTypePath(@Nullable TypePath path) {
                this.path = path;
            }

            @Override
            public String toString() {
                if (toString == null) {
                    toString = path == null ? "" : path.toString();
                }
                return toString;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof ComparableTypePath && obj.toString().equals(this.toString());
            }

            @Override
            public int hashCode() {
                return toString().hashCode();
            }
        }

        JTypeMirror decorate(JTypeMirror base) {
            for (Map.Entry<ComparableTypePath, List<SymAnnot>> pair : pathAndAnnot.entrySet()) {
                TypePath path = pair.getKey().path;

                base = resolvePath(base, path, pair.getValue());
            }
            return base;
        }

        private JTypeMirror resolvePath(JTypeMirror t, TypePath path, List<SymAnnot> annot) {
            if (t instanceof JClassType && ((JClassType) t).getEnclosingType() != null) {
                return handleEnclosingType((JClassType) t, path, 0, annot);
            }

            return resolvePathStep(t, path, 0, annot);
        }

        private static JTypeMirror resolvePathStep(JTypeMirror t, @Nullable TypePath path, int i, List<SymAnnot> annots) {
            if (path == null || i == path.getLength()) {
                return t.withAnnotations(annots);
            }

            if (t instanceof JClassType && ((JClassType) t).getEnclosingType() != null) {
                return handleEnclosingType((JClassType) t, path, i, annots);
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
                throw new IllegalArgumentException("Expected class type: " + t);
            case TypePath.ARRAY_ELEMENT:
                if (t instanceof JArrayType) {
                    JTypeMirror component = ((JArrayType) t).getComponentType();
                    JTypeMirror newComponent = resolvePathStep(component, path, i + 1, annots);
                    return t.getTypeSystem().arrayType(newComponent).withAnnotations(t.getTypeAnnotations());
                }
                throw new IllegalArgumentException("Expected array type: " + t);
            case TypePath.INNER_TYPE:
                throw new IllegalStateException("Should be handled elsewhere");
            case TypePath.WILDCARD_BOUND:
                throw new IllegalArgumentException("Expected wilcard type: " + t);
            default:
                throw new IllegalArgumentException("Illegal path step for annotation TypePath" + i);
            }
        }

        private static JClassType handleEnclosingType(JClassType t, @Nullable TypePath path, int i, List<SymAnnot> annots) {
            // We need to resolve the inner types left to right as given in the path.
            // Because JClassType is left-recursive its structure does not match the
            // structure of the path.

            // These are the enclosing types in inner-to-outer order (inverse of source order).
            // Eg Map.Entry will give [Map.Entry, Map]
            List<JClassType> enclosing = getEnclosingTypes(t);
            final int innerTStart = i;
            while (path != null && path.getStep(i) == TypePath.INNER_TYPE) {
                i++;
            }
            final int numInnerTypeSegments = i - innerTStart; // could be zero
            final int selectedTypeIndex = enclosing.size() - 1 - numInnerTypeSegments;
            JClassType newType = (JClassType) resolvePathStep(enclosing.get(selectedTypeIndex), path, i, annots);
            enclosing.set(selectedTypeIndex, newType);

            // this is the outermost type
            JClassType rebuiltType = enclosing.get(enclosing.size() - 1);
            // Then, we may need to rebuild the type by adding the remaining segments.
            for (int j = enclosing.size() - 2; j >= 0; j--) {
                JClassType nextInner = enclosing.get(j);
                rebuiltType = rebuiltType.selectInner(nextInner.getSymbol(), nextInner.getTypeArgs()).withAnnotations(nextInner.getTypeAnnotations());
            }
            return rebuiltType;
        }


        /** Returns a list containing the given type and all its enclosing types, in reverse order. */
        private static List<JClassType> getEnclosingTypes(JClassType t) {
            List<JClassType> enclosing = new ArrayList<>(1);
            do {
                enclosing.add(t);
                t = t.getEnclosingType();
            } while (t != null);
            return enclosing;
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
