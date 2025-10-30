/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.java.types.InternalApiBridge;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar.BoundKind;
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
        JTypeMirror solve(InferenceVar uv, InferenceContext inferenceContext) {
            List<JTypeMirror> types = filterBounds(uv, inferenceContext);
            if (types.size() == 1) {
                return types.get(0);
            }

            // If there are several EQ bounds, then they must be different
            // types for Object#equals, but since incorporation did not fail,
            // this means IncorporationAction#checkBound considers them compatible.
            // This must mean either:
            // 1. they are ERROR and another type (including UNKNOWN): return ERROR
            // 2. they are UNKNOWN and another type (including UNKNOWN): return the other type.
            TypeSystem ts = inferenceContext.ts;
            if (types.contains(ts.UNKNOWN) && types.size() == 2) {
                types.remove(ts.UNKNOWN);
                return types.get(0); // (this might be error)
            }
            return ts.ERROR;
        }
    },

    /**
     * Instantiate an inference variables using its (ground) lower bounds. Such
     * bounds are merged together using lub().
     */
    LOWER(BoundKind.LOWER) {
        @Override
        JTypeMirror solve(InferenceVar uv, InferenceContext infCtx) {
            return infCtx.ts.lub(filterBounds(uv, infCtx));
        }
    },

    /**
     * Instantiate an inference variables using its (ground) upper bounds. Such
     * bounds are merged together using glb().
     */
    UPPER(BoundKind.UPPER) {
        @Override
        JTypeMirror solve(InferenceVar uv, InferenceContext infCtx) {
            return infCtx.ts.glb(filterBounds(uv, infCtx));
        }
    },
    /**
     * Like the former; the only difference is that this step can only be applied
     * if all upper/lower bounds are ground.
     */
    CAPTURED(BoundKind.UPPER) {
        @Override
        public boolean accepts(InferenceVar t, InferenceContext inferenceContext) {
            return t.isCaptured()
                && inferenceContext.areAllGround(t.getBounds(BoundKind.LOWER))
                && inferenceContext.areAllGround(t.getBounds(BoundKind.UPPER));
        }

        @Override
        JTypeMirror solve(InferenceVar uv, InferenceContext infCtx) {
            JTypeMirror upper = !UPPER.filterBounds(uv, infCtx).isEmpty()
                                ? UPPER.solve(uv, infCtx)
                                : infCtx.ts.OBJECT;

            JTypeMirror lower = !LOWER.filterBounds(uv, infCtx).isEmpty()
                                ? LOWER.solve(uv, infCtx)
                                : infCtx.ts.NULL_TYPE;
            assert uv.getBaseVar() != null : "This is a captured var which can only be true if the base var is non-null";
            return uv.getBaseVar().cloneWithBounds(lower, upper);
        }
    },

    /**
     * Special case of {@link #UPPER}, that applies to f-bounds.
     * This is just spitballing, Javac doesn't do this.
     *
     * I use this for fbounds, like a context that has stuff like this
     * {@code β { β <: java.lang.Enum<β> } }. These usually get more bounds
     * via arguments, but unchecked casts may deny a more specific bound.
     * This should probably only apply when the call site doesn't need unchecked
     * conversions. This is a
     */
    FBOUND(BoundKind.UPPER) {

        @Override
        public boolean accepts(InferenceVar t, InferenceContext inferenceContext) {
            Set<JTypeMirror> ubounds = t.getBounds(BoundKind.UPPER);
            Set<InferenceVar> freeVars = inferenceContext.freeVarsIn(ubounds);
            return CollectionUtil.asSingle(freeVars) == t; // NOPMD - contains only itself in its upper bounds
        }

        @Override
        JTypeMirror solve(InferenceVar uv, InferenceContext infCtx) {
            JTypeMirror glb = infCtx.ts.glb(uv.getBounds(BoundKind.UPPER));
            JTypeMirror inst = glb.subst(var -> var == uv ? InternalApiBridge.freshCapture(infCtx.ts.UNBOUNDED_WILD) : var); // NOPMD

            // These upper bounds still contain references to the original ivar,
            // which, if we replace it with the inst, will be one level deeper than
            // we want (eg Enum<Enum<?>> instead of Enum<?>). We can delete them though,
            // because we don't need to check them. We know the inst is compatible because
            // we derived it directly from those bounds.
            uv.getBounds(BoundKind.UPPER).clear();

            if (infCtx.needsUncheckedConversion()) {
                return inst.getErasure();
            }
            // project to remove captures
            return TypeOps.projectUpwards(inst);
        }
    };

    /**
     * Sequence of steps to use in order when solving.
     */
    static final List<List<ReductionStep>> WAVES =
        listOf(
            listOf(EQ, LOWER, FBOUND, UPPER, CAPTURED)
        //  (There used to be several waves with different steps).
        );

    final BoundKind kind;

    ReductionStep(BoundKind kind) {
        this.kind = kind;
    }

    /**
     * Find an instantiated type for a given inference variable within
     * a given inference context
     */
    abstract JTypeMirror solve(InferenceVar uv, InferenceContext infCtx);

    /**
     * Can the inference variable be instantiated using this step?
     */
    public boolean accepts(InferenceVar t, InferenceContext infCtx) {
        return !t.isCaptured() && !filterBounds(t, infCtx).isEmpty();
    }

    protected boolean acceptsBound(JTypeMirror bound, InferenceContext infCtx) {
        return infCtx.isGround(bound) && bound != infCtx.ts.NULL_TYPE;
    }

    /**
     * Return the subset of ground bounds in a given bound set (i.e. eq/lower/upper)
     */
    List<JTypeMirror> filterBounds(InferenceVar ivar, InferenceContext infCtx) {
        List<JTypeMirror> res = new ArrayList<>();

        for (JTypeMirror bound : ivar.getBounds(kind)) {
            if (acceptsBound(bound, infCtx)) {
                res.add(bound);
            }
        }
        return res;
    }


}
