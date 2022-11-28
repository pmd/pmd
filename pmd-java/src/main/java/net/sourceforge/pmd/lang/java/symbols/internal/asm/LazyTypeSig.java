/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.TypePath;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.types.JArrayType;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JWildcardType;
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

        private final List<Pair<TypePath, SymAnnot>> pathAndAnnot = new ArrayList<>();

        void add(TypePath path, SymAnnot annot) {
            pathAndAnnot.add(Pair.of(path, annot));
        }

        /**
         * Transform the given type to apply type annotations. Returns
         * the type decorated with type annotations in the right places.
         */
        JTypeMirror decorate(JTypeMirror base) {
            for (Pair<TypePath, SymAnnot> pair : pathAndAnnot) {
                TypePath path = pair.getKey();

                base = resolvePathStep(base, path, 0, pair.getValue());
            }
            return base;
        }

        private static JTypeMirror resolvePathStep(JTypeMirror t, @Nullable TypePath path, int i, SymAnnot annot) {
            if (t instanceof JClassType && ((JClassType) t).getEnclosingType() != null) {
                return handleEnclosingType((JClassType) t, path, i, annot);
            }
            return resolvePathStepNoInner(t, path, i, annot);
        }

        private static JTypeMirror resolvePathStepNoInner(JTypeMirror t, @Nullable TypePath path, int i, SymAnnot annot) {
            assert path == null || path.getStep(i) != TypePath.INNER_TYPE;

            if (path == null || i == path.getLength()) {
                return t.addAnnotation(annot);
            }

            switch (path.getStep(i)) {
            case TypePath.TYPE_ARGUMENT:
                if (t instanceof JClassType) {
                    int typeArgIndex = path.getStepArgument(i);
                    JTypeMirror arg = ((JClassType) t).getTypeArgs().get(typeArgIndex);
                    JTypeMirror newArg = resolvePathStep(arg, path, i + 1, annot);
                    List<JTypeMirror> newArgs = replaceAtIndex(((JClassType) t).getTypeArgs(), typeArgIndex, newArg);
                    return ((JClassType) t).withTypeArguments(newArgs);
                }
                throw new IllegalArgumentException("Expected class type: " + t);
            case TypePath.ARRAY_ELEMENT:
                if (t instanceof JArrayType) {
                    JTypeMirror component = ((JArrayType) t).getComponentType();
                    JTypeMirror newComponent = resolvePathStep(component, path, i + 1, annot);
                    return t.getTypeSystem().arrayType(newComponent).withAnnotations(t.getTypeAnnotations());
                }
                throw new IllegalArgumentException("Expected array type: " + t);
            case TypePath.INNER_TYPE:
                throw new IllegalStateException("Should be handled elsewhere"); // there's an assert above too
            case TypePath.WILDCARD_BOUND:
                if (t instanceof JWildcardType) {
                    JWildcardType wild = (JWildcardType) t;
                    JTypeMirror newBound = resolvePathStep(wild.getBound(), path, i + 1, annot);
                    return wild.getTypeSystem().wildcard(wild.isUpperBound(), newBound).withAnnotations(wild.getTypeAnnotations());
                }
                throw new IllegalArgumentException("Expected wilcard type: " + t);
            default:
                throw new IllegalArgumentException("Illegal path step for annotation TypePath" + i);
            }
        }

        private static JClassType handleEnclosingType(JClassType t, @Nullable TypePath path, int i, SymAnnot annot) {
            // We need to resolve the inner types left to right as given in the path.
            // Because JClassType is left-recursive its structure does not match the
            // structure of the path.
            final JClassType selectedT;
            // this list is in inner to outer order
            // eg for A.B.C, the list is [A.B.C, A.B, A]
            List<JClassType> enclosingTypes = getEnclosingTypes(t);
            int selectionDepth = 0;
            while (path != null && path.getStep(i + selectionDepth) == TypePath.INNER_TYPE) {
                selectionDepth++;
            }
            final int selectedTypeIndex = enclosingTypes.size() - 1 - selectionDepth;
            selectedT = enclosingTypes.get(selectedTypeIndex);

            // interpret the rest of the path as with this type as context
            JClassType rebuiltType = (JClassType) resolvePathStepNoInner(selectedT, path, i + selectionDepth, annot);
            // Then, we may need to rebuild the type by adding the remaining segments.
            for (int j = selectedTypeIndex - 1; j >= 0; j--) {
                JClassType nextInner = enclosingTypes.get(j);
                rebuiltType = rebuiltType.selectInner(nextInner.getSymbol(), nextInner.getTypeArgs(), nextInner.getTypeAnnotations());
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
