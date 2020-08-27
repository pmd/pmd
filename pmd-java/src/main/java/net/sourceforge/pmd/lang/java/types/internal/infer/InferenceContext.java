/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;


import static net.sourceforge.pmd.lang.java.types.TypeOps.asList;
import static net.sourceforge.pmd.util.CollectionUtil.intersect;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.JTypeVisitable;
import net.sourceforge.pmd.lang.java.types.SubstVar;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.internal.infer.IncorporationAction.CheckBound;
import net.sourceforge.pmd.lang.java.types.internal.infer.IncorporationAction.PropagateAllBounds;
import net.sourceforge.pmd.lang.java.types.internal.infer.IncorporationAction.PropagateBounds;
import net.sourceforge.pmd.lang.java.types.internal.infer.IncorporationAction.SubstituteInst;
import net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar.BoundKind;
import net.sourceforge.pmd.lang.java.types.internal.infer.VarWalkStrategy.GraphWalk;

/**
 * Context of a type inference process. This object maintains a set of
 * unique inference variables. Inference variables maintain the set of
 * bounds that apply to them.
 */
final class InferenceContext {

    // ivar/ctx ids are globally unique, & repeatable in debug output if you do exactly the same run
    private static int varId = 0;
    private static int ctxId = 0;

    private final Map<InstantiationListener, Set<InferenceVar>> instantiationListeners = new HashMap<>();

    private final Set<InferenceVar> freeVars = new LinkedHashSet<>();
    private final Set<InferenceVar> inferenceVars = new LinkedHashSet<>();
    private final Deque<IncorporationAction> incorporationActions = new ArrayDeque<>();
    final TypeSystem ts;
    final TypeInferenceLogger logger;

    private Substitution mapping = Substitution.EMPTY;
    private @Nullable InferenceContext parent;
    private final int id;

    /**
     * Create an inference context from a set of type variables to instantiate.
     * This creates inference vars and adds the initial bounds as described in
     *
     * https://docs.oracle.com/javase/specs/jls/se9/html/jls-18.html#jls-18.1.3
     *
     * under the purple rectangle.
     */
    InferenceContext(TypeSystem ts, List<JTypeVar> tvars, TypeInferenceLogger logger) {
        this.ts = ts;
        this.logger = logger;
        this.id = ctxId++;

        for (JTypeVar p : tvars) {
            addVarImpl(p);
        }

        for (InferenceVar ivar : inferenceVars) {
            addPrimaryBound(ivar);
        }
    }

    public int getId() {
        return id;
    }

    private void addPrimaryBound(InferenceVar ivar) {
        for (JTypeMirror ui : asList(ivar.getBaseVar().getUpperBound())) {
            ivar.addBound(BoundKind.UPPER, mapToIVars(ui));
        }
    }

    /** Add a variable to this context. */
    InferenceVar addVar(JTypeVar tvar) {
        InferenceVar ivar = addVarImpl(tvar);
        addPrimaryBound(ivar);

        for (InferenceVar otherIvar : inferenceVars) {
            // remove remaining occurrences of type params
            otherIvar.substBounds(this::mapToIVars);
        }
        return ivar;
    }

    /** Add a variable to this context. */
    private InferenceVar addVarImpl(@NonNull JTypeVar tvar) {
        InferenceVar ivar = new InferenceVar(this, tvar, varId++);
        freeVars.add(ivar);
        inferenceVars.add(ivar);
        mapping = mapping.plus(tvar, ivar);
        return ivar;
    }

    /**
     * Replace all type variables in the given type with corresponding
     * inference vars.
     */
    JTypeMirror mapToIVars(JTypeMirror t) {
        return TypeOps.subst(t, mapping);
    }

    /**
     * Replace all type variables in the given type with corresponding
     * inference vars.
     */
    JMethodSig mapToIVars(JMethodSig t) {
        return t.subst(mapping);
    }

    /**
     * Returns true if the type mentions no free inference variables.
     * This is what the JLS calls a "proper type".
     */
    boolean isGround(JTypeVisitable t) {
        return !TypeOps.mentionsAny(t, freeVars);
    }

    /**
     * Returns true if the type mentions no free inference variables.
     */
    boolean areAllGround(Collection<? extends JTypeVisitable> ts) {
        for (JTypeVisitable t : ts) {
            if (!isGround(t)) {
                return false;
            }
        }
        return true;
    }

    Set<InferenceVar> freeVarsIn(Iterable<? extends JTypeVisitable> types) {
        Set<InferenceVar> vars = new LinkedHashSet<>();
        for (InferenceVar ivar : freeVars) {
            for (JTypeVisitable t : types) {
                if (TypeOps.mentions(t, ivar)) {
                    vars.add(ivar);
                }
            }
        }
        return vars;
    }

    Set<InferenceVar> freeVarsIn(JTypeVisitable t) {
        return freeVarsIn(Collections.singleton(t));
    }

    /**
     * Replace instantiated inference vars with their instantiation in the given type.
     */
    JTypeMirror ground(JTypeMirror t) {
        return t.subst(InferenceContext::groundSubst);
    }

    JClassType ground(JClassType t) {
        return t.subst(InferenceContext::groundSubst);
    }

    /**
     * Replace instantiated inference vars with their instantiation in the given type.
     */
    JMethodSig ground(JMethodSig t) {
        return t.subst(InferenceContext::groundSubst);
    }


    private static JTypeMirror groundSubst(SubstVar var) {
        if (var instanceof InferenceVar) {
            JTypeMirror inst = ((InferenceVar) var).getInst();
            if (inst != null) {
                return inst;
            }
        }
        return var;
    }

    /**
     * Replace instantiated inference vars with their instantiation in the given type,
     * or else replace them with a failed type.
     */
    static JMethodSig finalGround(JMethodSig t) {
        return t.subst(s -> {
            if (!(s instanceof InferenceVar)) {
                return s;
            } else {
                InferenceVar ivar = (InferenceVar) s;
                return ivar.getInst() != null ? ivar.getInst() : s.getTypeSystem().ERROR;
            }
        });
    }

    /**
     * Copy variable in this inference context to the given context
     */
    void duplicateInto(final InferenceContext that) {
        that.inferenceVars.addAll(this.inferenceVars);
        that.freeVars.addAll(this.freeVars);
        that.incorporationActions.addAll(this.incorporationActions);
        that.instantiationListeners.putAll(this.instantiationListeners);

        this.parent = that;

        // propagate existing bounds into the new context
        for (InferenceVar freeVar : this.freeVars) {
            that.incorporationActions.add(new PropagateAllBounds(freeVar));
        }
    }


    void addInstantiationListener(Set<? extends JTypeMirror> relevantTypes, InstantiationListener listener) {
        Set<InferenceVar> free = freeVarsIn(relevantTypes);
        if (free.isEmpty()) {
            listener.onInstantiation(this);
            return;
        }
        instantiationListeners.put(listener, free);
    }

    /**
     * Call the listeners registered with {@link #addInstantiationListener(Set, InstantiationListener)}.
     * Listeners are used to perform deferred checks, like checking
     * compatibility of a formal parameter with an expression when the
     * formal parameter is not ground.
     */
    void callListeners() {
        if (instantiationListeners.isEmpty()) {
            return;
        }
        Set<InferenceVar> solved = new LinkedHashSet<>(inferenceVars);
        solved.removeAll(freeVars);


        for (Entry<InstantiationListener, Set<InferenceVar>> entry : new LinkedHashSet<>(instantiationListeners.entrySet())) {
            if (solved.containsAll(entry.getValue())) {
                try {
                    entry.getKey().onInstantiation(this);
                } catch (ResolutionFailedException ignored) {
                    // that is a compile-time error, but that
                    // shouldn't affect PMD

                    // This can happen eg when an assertion fails in a
                    // subcontext that depends on this one, which is waiting
                    // for more inference to happen

                    // TODO investigate
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    instantiationListeners.remove(entry.getKey());
                }
            }
        }
    }

    Set<InferenceVar> getFreeVars() {
        return Collections.unmodifiableSet(freeVars);
    }

    private void onVarInstantiated(InferenceVar ivar) {
        if (parent != null) {
            parent.onVarInstantiated(ivar);
            return;
        }

        logger.ivarInstantiated(this, ivar, ivar.getInst());

        incorporationActions.addFirst(new SubstituteInst(ivar, ivar.getInst()) {
            @Override
            public void apply(InferenceContext ctx) {
                freeVars.removeIf(it -> it.getInst() != null);
                super.apply(ctx);
            }
        });
    }


    void onBoundAdded(InferenceVar ivar, BoundKind kind, JTypeMirror bound, boolean isSubstitution) {
        // guard against α <: Object
        // all variables have it, it's useless to propagate it
        if (kind != BoundKind.UPPER || bound != ts.OBJECT) {
            if (parent != null) {
                parent.onBoundAdded(ivar, kind, bound, isSubstitution);
                return;
            }

            logger.boundAdded(this, ivar, kind, bound, isSubstitution);

            incorporationActions.add(new CheckBound(ivar, kind, bound));
            incorporationActions.add(new PropagateBounds(ivar, kind, bound));
        }
    }

    void onIvarMerged(InferenceVar prev, InferenceVar delegate) {
        if (parent != null) {
            parent.onIvarMerged(prev, delegate);
            return;
        }

        logger.ivarMerged(this, prev, delegate);

        mapping = mapping.plus(prev.getBaseVar(), delegate);
        incorporationActions.addFirst(new SubstituteInst(prev, delegate));
    }

    /**
     * Runs the incorporation hooks registered for the free vars.
     *
     * @throws ResolutionFailedException If some propagated bounds are incompatible
     */
    void incorporate() {
        if (incorporationActions.isEmpty()) {
            return;
        }

        // TODO
        //  Organize incorporation actions better
        //  * When an ivar is adopted by an enclosing context it keeps
        //    sending messages to the old one but not the new...
        //  * Javac uses one local queue per ivar, seems much more
        //    resilient

        IncorporationAction hook = incorporationActions.pollFirst();
        while (hook != null) {

            if (hook.doApplyToInstVar || hook.ivar.getInst() == null) {
                hook.apply(this);
            }

            hook = incorporationActions.pollFirst();
        }
    }

    /**
     * @throws ResolutionFailedException Because it calls {@link #incorporate()}
     */
    void solve() {
        solve(new GraphWalk(this));
    }

    /**
     * Solve a single var, this does not solve its dependencies, so that
     * if some bounds are not ground, instantiation will be wrong.
     */
    void solve(InferenceVar var) {
        solve(new GraphWalk(var));
    }

    private void solve(VarWalkStrategy walker) {
        incorporate();

        while (walker.hasNext()) {

            Set<InferenceVar> varsToSolve = walker.next();

            boolean progress = true;
            //repeat until all variables are solved
            outer:
            while (!intersect(freeVars, varsToSolve).isEmpty() && progress) {
                progress = false;
                for (List<ReductionStep> wave : ReductionStep.WAVES) {
                    if (solveBatchProgressed(varsToSolve, wave)) {
                        incorporate();
                        progress = true;
                        callListeners();
                        continue outer;
                    }
                }
            }
        }
    }

    /**
     * Tries to solve as much of varsToSolve as possible using some reduction steps.
     * Returns the set of solved variables during this step.
     */
    private boolean solveBatchProgressed(Set<InferenceVar> varsToSolve, List<ReductionStep> wave) {
        for (InferenceVar ivar : intersect(varsToSolve, freeVars)) {
            for (ReductionStep step : wave) {
                if (step.accepts(ivar, this)) {
                    ivar.setInst(step.solve(ivar, this));
                    onVarInstantiated(ivar);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isEmpty() {
        return inferenceVars.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Inference context " + getId()).append('\n');
        for (InferenceVar ivar : inferenceVars) {
            sb.append(ivar);
            if (ivar.getInst() != null) {
                sb.append(" := ").append(ivar.getInst()).append('\n');
            } else {
                ivar.formatBounds(sb).append('\n');
            }
        }

        return sb.toString();
    }

    /** A callback called when a set of variables have been solved. */
    public interface InstantiationListener {

        /**
         * Called when the set of dependencies provided to {@link #addInstantiationListener(Set, InstantiationListener)}
         * have been solved. The parameter is not necessarily the context
         * on which this has been registered, because contexts adopt the
         * inference variables of their children in some cases, to solve
         * them together. Use {@link #ground(JClassType)} with the context
         * parameter, not the context on which the callback was registered.
         */
        void onInstantiation(InferenceContext solvedCtx);

    }
}
