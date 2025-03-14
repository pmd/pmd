/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;
import org.pcollections.ConsPStack;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.types.JArrayType;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JWildcardType;

/**
 * @author Clément Fournier
 */
final class TypeAnnotationHelper {

    private TypeAnnotationHelper() {
        // utility class
    }


    /** Accumulate type annotations to be applied on a single type. */
    static final class TypeAnnotationSet {

        private final List<Pair<@Nullable TypePath, SymAnnot>> pathAndAnnot = new ArrayList<>();

        void add(@Nullable TypePath path, SymAnnot annot) {
            pathAndAnnot.add(Pair.of(path, annot));
        }

        /**
         * Transform the given type to apply type annotations. Returns
         * the type decorated with type annotations in the right places.
         */
        JTypeMirror decorate(@NonNull JTypeMirror base) {
            for (Pair<@Nullable TypePath, SymAnnot> pair : pathAndAnnot) {
                base = applySinglePath(base, pair.getLeft(), pair.getRight());
            }
            return base;
        }

    }

    /**
     * Accumulate type annotations to be applied on a more complex signature than just a field.
     * This includes method signatures and class signatures.
     */
    static final class TypeAnnotationSetWithReferences {

        private final List<Triple<TypeReference, @Nullable TypePath, SymAnnot>> pathAndAnnot = new ArrayList<>();

        void add(TypeReference reference, @Nullable TypePath path, SymAnnot annot) {
            pathAndAnnot.add(Triple.of(reference, path, annot));
        }

        /** Return true if the parameter returns true on any parameter. */
        boolean forEach(TypeAnnotationConsumer consumer) {
            boolean result = false;
            for (Triple<TypeReference, TypePath, SymAnnot> triple : pathAndAnnot) {
                result |= consumer.acceptAnnotation(triple.getLeft(), triple.getMiddle(), triple.getRight());
            }
            return result;
        }

        @Override
        public String toString() {
            return pathAndAnnot.toString();
        }

        @FunctionalInterface
        interface TypeAnnotationConsumer {

            /** Add an annotation at the given path and type ref. */
            boolean acceptAnnotation(TypeReference tyRef, @Nullable TypePath path, SymAnnot annot);
        }
    }

    /**
     * Add one type annotation into the given type at the location given
     * by the given path.
     */
    static JTypeMirror applySinglePath(@NonNull JTypeMirror base, @Nullable TypePath path, SymAnnot annot) {
        return resolvePathStep(base, path, 0, annot);
    }

    private static JTypeMirror resolvePathStep(JTypeMirror t, @Nullable TypePath path, int i, SymAnnot annot) {
        if (t instanceof JClassType && ((JClassType) t).getEnclosingType() != null) {
            return handleEnclosingType((JClassType) t, path, i, annot);
        }
        return resolvePathStepNoInner(t, path, i, annot);
    }

    private static JTypeMirror resolvePathStepNoInner(JTypeMirror t, @Nullable TypePath path, int i, SymAnnot annot) {
        assert path == null || path.getLength() == i
            || path.getStep(i) != TypePath.INNER_TYPE;

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

    static @NonNull List<JTypeMirror> replaceAtIndex(List<JTypeMirror> typeArgs, int typeArgIndex, JTypeMirror newArg) {
        if (typeArgs.size() == 1 && typeArgIndex == 0) {
            return ConsPStack.singleton(newArg);
        }
        return ConsPStack.from(typeArgs).with(typeArgIndex, newArg);
    }
}
