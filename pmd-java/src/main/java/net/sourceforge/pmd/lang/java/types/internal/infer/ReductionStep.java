/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.internal.infer.JInferenceVar.BoundKind;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Reduction steps on a variable. If its bounds match a certain pattern,
 * it will be instantiated by one of these reductions.
 */
enum ReductionStep {

    /**
     * Instantiate an inference variable using one of its equality bounds.
     */
    EQ(BoundKind.EQ) {
        @Override
        JTypeMirror solve(JInferenceVar uv, InferenceContext inferenceContext) {
            return filterBounds(uv, inferenceContext).get(0);
        }
    },

    /**
     * Instantiate an inference variables using its (ground) lower bounds. Such
     * bounds are merged together using lub().
     */
    LOWER(BoundKind.LOWER) {
        @Override
        JTypeMirror solve(JInferenceVar uv, InferenceContext infCtx) {
            return infCtx.ts.lub(filterBounds(uv, infCtx));
        }
    },

    /**
     * Instantiate an inference variables using its (ground) upper bounds. Such
     * bounds are merged together using glb().
     */
    UPPER(BoundKind.UPPER) {
        @Override
        JTypeMirror solve(JInferenceVar uv, InferenceContext infCtx) {
            return infCtx.ts.glb(filterBounds(uv, infCtx));
        }
    },
    /**
     * Like the former; the only difference is that this step can only be applied
     * if all upper/lower bounds are ground.
     */
    CAPTURED(BoundKind.UPPER) {
        @Override
        public boolean accepts(JInferenceVar t, InferenceContext inferenceContext) {
            return t.isCaptured()
                && inferenceContext.areAllGround(t.getBounds(BoundKind.LOWER))
                && inferenceContext.areAllGround(t.getBounds(BoundKind.UPPER));
        }

        @Override
        JTypeMirror solve(JInferenceVar uv, InferenceContext infCtx) {
            JTypeMirror upper = !UPPER.filterBounds(uv, infCtx).isEmpty()
                                ? UPPER.solve(uv, infCtx)
                                : infCtx.ts.OBJECT;

            JTypeMirror lower = !LOWER.filterBounds(uv, infCtx).isEmpty()
                                ? LOWER.solve(uv, infCtx)
                                : infCtx.ts.NULL_TYPE;

            return uv.getBaseVar().cloneWithBounds(lower, upper);
        }
    },

    FBOUND(BoundKind.UPPER) {
        // This is just spitballing, Javac doesn't do this
        // I use this for fbounds, like a context that has stuff like this
        //   β { β <: java.lang.Enum<β> }
        // It may be solvable, with an unchecked warning

        @Override
        public boolean accepts(JInferenceVar t, InferenceContext inferenceContext) {
            Set<JTypeMirror> ubounds = t.getBounds(BoundKind.UPPER);
            Set<JInferenceVar> freeVars = inferenceContext.freeVarsIn(ubounds);
            return CollectionUtil.asSingle(freeVars) == t // contains only itself in its upper bounds
                && ubounds.iterator().next() instanceof JClassType; // and erasure is enough to get rid of the dependency
        }

        @Override
        JTypeMirror solve(JInferenceVar uv, InferenceContext infCtx) {
            return infCtx.ts.glb(TypeOps.erase(uv.getBounds(BoundKind.UPPER)));
        }
    };

    /**
     * Sequence of steps to use in order when solving.
     * This is for compatibility with Javac.
     */
    static final List<Set<ReductionStep>> WAVES =
        listOf(
            EnumSet.of(EQ),
            EnumSet.of(EQ, LOWER),
            EnumSet.of(EQ, LOWER, UPPER, CAPTURED),
            EnumSet.of(EQ, LOWER, UPPER, CAPTURED, FBOUND)
        );

    final BoundKind kind;

    ReductionStep(BoundKind kind) {
        this.kind = kind;
    }

    /**
     * Find an instantiated type for a given inference variable within
     * a given inference context
     */
    abstract JTypeMirror solve(JInferenceVar uv, InferenceContext infCtx);

    /**
     * Can the inference variable be instantiated using this step?
     */
    public boolean accepts(JInferenceVar t, InferenceContext infCtx) {
        return !t.isCaptured() && !filterBounds(t, infCtx).isEmpty();
    }

    protected boolean acceptsBound(JTypeMirror bound, InferenceContext infCtx) {
        return infCtx.isGround(bound) && bound != infCtx.ts.NULL_TYPE;
    }

    /**
     * Return the subset of ground bounds in a given bound set (i.e. eq/lower/upper)
     */
    List<JTypeMirror> filterBounds(JInferenceVar ivar, InferenceContext infCtx) {
        List<JTypeMirror> res = new ArrayList<>();

        for (JTypeMirror bound : ivar.getBounds(kind)) {
            if (acceptsBound(bound, infCtx)) {
                res.add(bound);
            }
        }
        return res;
    }


}
