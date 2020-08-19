/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar;

/**
 * Visits a type. This allows implementing many algorithms simply.
 *
 * @param <R> Return type
 * @param <P> Parameter type
 */
public interface JTypeVisitor<R, P> {

    R visit(JTypeMirror t, P p);


    default R visitClass(JClassType t, P p) {
        return visit(t, p);
    }


    default R visitWildcard(JWildcardType t, P p) {
        return visit(t, p);
    }


    default R visitPrimitive(JPrimitiveType t, P p) {
        return visit(t, p);
    }


    default R visitTypeVar(JTypeVar t, P p) {
        return visit(t, p);
    }


    default R visitInferenceVar(InferenceVar t, P p) {
        return visit(t, p);
    }


    default R visitMethodType(JMethodSig t, P p) {
        throw new UnsupportedOperationException("You can't do this by accident");
    }


    default R visitIntersection(JIntersectionType t, P p) {
        return visit(t, p);
    }


    default R visitArray(JArrayType t, P p) {
        return visit(t, p);
    }


    default R visitNullType(JTypeMirror t, P p) {
        return visit(t, p);
    }


    /**
     * Visit a sentinel type. The argument may be one of
     * {@link TypeSystem#UNRESOLVED_TYPE}, {@link TypeSystem#NO_TYPE},
     * and {@link TypeSystem#NULL_TYPE}.
     */
    default R visitSentinel(JTypeMirror t, P p) {
        return visit(t, p);
    }

}
