/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import static net.sourceforge.pmd.lang.java.types.TestUtilitiesForTypesKt.captureMatcher;
import static net.sourceforge.pmd.lang.java.types.internal.infer.BaseTypeInferenceUnitTest.Bound.eqBound;
import static net.sourceforge.pmd.lang.java.types.internal.infer.BaseTypeInferenceUnitTest.Bound.lower;
import static net.sourceforge.pmd.lang.java.types.internal.infer.BaseTypeInferenceUnitTest.Bound.upper;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar.BoundKind;

/**
 *
 */
class InferenceCtxUnitTests extends BaseTypeInferenceUnitTest {

    @Test
    void testHasPrimaryBound() {
        TypeInferenceLogger log = spy(TypeInferenceLogger.noop());
        InferenceContext ctx = emptyCtx(log);

        InferenceVar v1 = newIvar(ctx);
        InferenceVar v2 = newIvar(ctx, ts.SERIALIZABLE);

        assertThat(v1, hasBound(BoundKind.UPPER, ts.OBJECT));
        assertThat(v2, hasBound(BoundKind.UPPER, ts.SERIALIZABLE));

        assertTrue(v1.hasOnlyPrimaryBound());
        assertTrue(v2.hasOnlyPrimaryBound());

        JTypeMirror listOfV1 = listType(v1);
        TypeOps.isConvertible(v2, listOfV1);

        assertThat(v2, hasBoundsExactly(upper(ts.SERIALIZABLE), upper(listOfV1)));

        assertFalse(v2.hasOnlyPrimaryBound());
        assertTrue(v1.hasOnlyPrimaryBound());

        verify(log).boundAdded(ctx, v2, BoundKind.UPPER, listOfV1, false);
    }

    @Test
    void testBoundsOnConvertibilityCheck() {
        TypeInferenceLogger log = spy(TypeInferenceLogger.noop());
        InferenceContext ctx = emptyCtx(log);

        InferenceVar v1 = newIvar(ctx);
        InferenceVar v2 = newIvar(ctx);

        assertThat(v2, hasBound(BoundKind.UPPER, ts.OBJECT));
        assertThat(v1, hasBound(BoundKind.UPPER, ts.OBJECT));

        JTypeMirror listOfV1 = listType(v1);
        TypeOps.isConvertible(v2, listOfV1);

        assertThat(v2, hasBoundsExactly(upper(listOfV1), upper(ts.OBJECT)));

        verify(log).boundAdded(ctx, v2, BoundKind.UPPER, listOfV1, false);
    }

    @Test
    void testEqBoundWithGenerics() {
        TypeInferenceLogger log = spy(TypeInferenceLogger.noop());
        InferenceContext ctx = emptyCtx(log);

        InferenceVar v1 = newIvar(ctx);
        InferenceVar v2 = newIvar(ctx);

        JTypeMirror listOfV1 = listType(v1);
        JTypeMirror listOfListOfV2 = listType(listType(v2));

        TypeOps.isConvertible(listOfV1, listOfListOfV2);

        assertThat(v1, hasBoundsExactly(eqBound(listType(v2)), upper(ts.OBJECT)));
        assertThat(v2, hasBoundsExactly(upper(ts.OBJECT)));

        ctx.incorporate();

        verify(log, never()).ivarMerged(any(), any(), any());
        assertFalse(v1.isEquivalentTo(v2));
        assertFalse(v2.isEquivalentTo(v1));

        ctx.solve();

        assertEquals(ts.OBJECT, v2.getInst());
        assertEquals(listType(ts.OBJECT), v1.getInst());
    }


    @Test
    void testEqBoundMergesIvar() {
        TypeInferenceLogger log = spy(TypeInferenceLogger.noop());
        InferenceContext ctx = emptyCtx(log);

        InferenceVar v1 = newIvar(ctx);
        InferenceVar v2 = newIvar(ctx);

        JTypeMirror listOfV1 = listType(v1);
        JTypeMirror listOfV2 = listType(v2);

        TypeOps.isConvertible(listOfV1, listOfV2);

        assertThat(v2, hasBoundsExactly(eqBound(v1), upper(ts.OBJECT)));
        assertThat(v1, hasBoundsExactly(upper(ts.OBJECT))); // bound is propagated to v1 later

        ctx.incorporate();

        verify(log, times(1)).ivarMerged(ctx, v2, v1);
        assertTrue(v1.isEquivalentTo(v2));
        assertTrue(v2.isEquivalentTo(v1));
    }

    @Test
    void testSymmetricPropagationOfUpper() {
        TypeInferenceLogger log = spy(TypeInferenceLogger.noop());
        InferenceContext ctx = emptyCtx(log);

        InferenceVar a = newIvar(ctx);
        InferenceVar b = newIvar(ctx);

        // 'a <: 'b
        // ~> 'b >: 'a
        addSubtypeConstraint(ctx, a, b);

        assertThat(a, hasBoundsExactly(upper(b)));
        assertThat(b, hasBoundsExactly(lower(a)));

        verify(log, never()).ivarMerged(any(), any(), any());
        verify(log, times(2)).boundAdded(any(), any(), any(), any(), eq(false));
        verify(log, never()).boundAdded(any(), any(), any(), any(), eq(true));
    }

    @Test
    void testWildLowerLower() {
        InferenceContext ctx = emptyCtx();

        InferenceVar a = newIvar(ctx);
        InferenceVar b = newIvar(ctx);

        // List<? super 'a> <: List<? super 'b>
        // ~> 'b <: 'a
        addSubtypeConstraint(ctx, listType(superWild(a)), listType(superWild(b)));

        assertThat(a, hasBoundsExactly(lower(b)));
        assertThat(b, hasBoundsExactly(upper(a)));
    }

    @Test
    void testWildUpperUpper() {
        InferenceContext ctx = emptyCtx();

        InferenceVar a = newIvar(ctx);
        InferenceVar b = newIvar(ctx);

        // List<? extends 'a> <: List<? extends 'b>
        // ~> 'a <: 'b
        addSubtypeConstraint(ctx, listType(extendsWild(a)), listType(extendsWild(b)));

        assertThat(a, hasBoundsExactly(upper(b)));
        assertThat(b, hasBoundsExactly(lower(a)));
    }

    /* Remember:
            let S, T != Object, S <: T

            G<? super T> </: G<? extends T>
            G<? extends T> </: G<? super T>

            G<? super T> <: G<? super S>
            G<? extends S> <: G<? extends T>

            if T = Object, then G<? extends T> = G<?>, and

            G<A> <: G<?> forall A (incl. wildcards)
     */

    @Test
    void testWildLowerUpper() {
        InferenceContext ctx = emptyCtx();

        InferenceVar a = newIvar(ctx);
        InferenceVar b = newIvar(ctx);

        // List<? super 'a> <: List<? extends 'b>
        // ~> 'b >: Object
        addSubtypeConstraint(ctx, listType(superWild(a)), listType(extendsWild(b)));

        assertThat(b, hasBoundsExactly(upper(ts.OBJECT), lower(ts.OBJECT)));
        assertThat(a, hasBoundsExactly(upper(ts.OBJECT)));
    }

    @Test
    void testWildUpperLower() {
        InferenceContext ctx = emptyCtx();

        InferenceVar a = newIvar(ctx);
        InferenceVar b = newIvar(ctx);

        // List<? extends 'a> <: List<? super 'b>
        // ~> 'b <: capture of ? extends 'a

        // Proof:
        // capture(List<? extends 'a>) <: List<? super 'b>
        // |- List<capture of ? extends 'a> <: List<? super 'b>
        //   |- capture of ? extends 'a <= ? super 'b
        //     |- lower(? super 'b) <: lower(capture of ? extends 'a)
        //       |- 'b             <: capture of ? extends 'a
        //     |- upper(capture of ? extends 'a) <: upper(? super 'b)
        //       |- 'a                          <: Object

        // Note that lower(capture of ? extends 'a) does not reduce to
        // the null type, as that would be useless and disprove valid programs.
        addSubtypeConstraint(ctx, listType(extendsWild(a)), listType(superWild(b)));

        assertThat(b, hasBoundsExactly(upper(ts.OBJECT), upper(captureMatcher(extendsWild(a)))));
        assertThat(a, hasBoundsExactly(upper(ts.OBJECT)));
    }



    @Test
    void testIntersectionRight() {
        InferenceContext ctx = emptyCtx();

        InferenceVar a = newIvar(ctx);
        InferenceVar b = newIvar(ctx);

        // 'a <: List<? extends 'b> & Serializable
        // ~> 'b >: List<? extends 'a>
        // ~> 'b >: Serializable

        JTypeMirror listOfB = listType(extendsWild(b));
        addSubtypeConstraint(ctx,
                             a,
                             intersect(listOfB, ts.SERIALIZABLE));

        assertThat(a, hasBoundsExactly(upper(ts.SERIALIZABLE), upper(listOfB)));
        assertThat(b, hasBoundsExactly(upper(ts.OBJECT)));
    }


    @Test
    void testIntersectionLeft() {
        InferenceContext ctx = emptyCtx();

        InferenceVar a = newIvar(ctx);
        InferenceVar b = newIvar(ctx);

        // List<? extends 'a> & Serializable <: 'b
        // ~> 'b >: List<? extends 'a> & Serializable

        // Note that this does not split the intersection into several constraints, like eg
        // 'b >: List<? extends 'a>
        // 'b >: Serializable

        // This is because those constraints together would require 'b
        // to be a supertype of both, whereas `'b >: x & y` only requires
        // that 'b be a supertype of either

        // When the intersection is on the right it is appropriate to split it,
        // in order to have more constraints to propagate

        JTypeMirror listOfA = listType(extendsWild(a));
        addSubtypeConstraint(ctx,
                             intersect(listOfA, ts.SERIALIZABLE),
                             b);

        assertThat(a, hasBoundsExactly(upper(ts.OBJECT)));
        assertThat(b, hasBoundsExactly(lower(intersect(listOfA, ts.SERIALIZABLE))));
    }

    @Test
    void testArrayLower() {
        InferenceContext ctx = emptyCtx();

        InferenceVar a = newIvar(ctx);

        // Boolean[] <: 'a[]
        // ~> Boolean <: 'a
        addSubtypeConstraint(ctx,
                             ts.arrayType(ts.BOOLEAN.box()),
                             ts.arrayType(a));

        assertThat(a, hasBoundsExactly(lower(ts.BOOLEAN.box())));
    }

    @Test
    void testArrayUpper() {
        InferenceContext ctx = emptyCtx();

        InferenceVar a = newIvar(ctx);

        // 'a[] <: Boolean[]
        // ~> 'a <: Boolean
        addSubtypeConstraint(ctx,
                             ts.arrayType(a),
                             ts.arrayType(ts.BOOLEAN.box()));

        assertThat(a, hasBoundsExactly(upper(ts.BOOLEAN.box())));
    }

}
