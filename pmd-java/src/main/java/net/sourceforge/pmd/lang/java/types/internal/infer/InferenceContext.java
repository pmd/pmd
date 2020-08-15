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

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JIntersectionType;
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
import net.sourceforge.pmd.lang.java.types.internal.infer.JInferenceVar.BoundKind;
import net.sourceforge.pmd.lang.java.types.internal.infer.VarWalkStrategy.GraphWalk;

/**
 * Context of a type inference process. This object maintains a set of
 * unique inference variables. Inference variables maintain the set of
 * bounds that apply to them.
 */
final class InferenceContext {

    private static int varId = 0;
    private static int ctxId = 0;

    private final Map<InstantiationListener, Set<JInferenceVar>> instantiationListeners = new HashMap<>();

    private final Set<JInferenceVar> freeVars = new LinkedHashSet<>();
    private final Set<JInferenceVar> inferenceVars = new LinkedHashSet<>();
    private final Deque<IncorporationAction> incorporationActions = new ArrayDeque<>();
    final TypeSystem ts;
    final TypeInferenceLogger logger;

    private Substitution mapping = Substitution.EMPTY;
    @Nullable
    private InferenceContext parent;
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

        for (JInferenceVar ivar : inferenceVars) {
            addPrimaryBound(ivar);
        }
    }

    public int getId() {
        return id;
    }

    private void addPrimaryBound(JInferenceVar ivar) {
        for (JTypeMirror ui : asList(ivar.getBaseVar().getUpperBound())) {
            ivar.addBound(BoundKind.UPPER, mapToIVars(ui));
        }
    }

    /** Add a variable to this context. */
    void addVar(JTypeVar tvar) {
        JInferenceVar ivar = addVarImpl(tvar);
        addPrimaryBound(ivar);

        for (JInferenceVar otherIvar : inferenceVars) {
            // remove remaining occurrences of type params
            otherIvar.substBounds(this::mapToIVars);
        }
    }

    /** Add a variable to this context. */
    private JInferenceVar addVarImpl(JTypeVar tvar) {
        JInferenceVar ivar = new JInferenceVar(this, tvar, varId++);
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

    Set<JInferenceVar> freeVarsIn(Iterable<? extends JTypeVisitable> types) {
        Set<JInferenceVar> vars = new LinkedHashSet<>();
        for (JInferenceVar ivar : freeVars) {
            for (JTypeVisitable t : types) {
                if (TypeOps.mentions(t, ivar)) {
                    vars.add(ivar);
                }
            }
        }
        return vars;
    }

    Set<JInferenceVar> freeVarsIn(JTypeVisitable t) {
        return freeVarsIn(Collections.singleton(t));
    }

    /**
     * Replace instantiated inference vars with their instantiation in the given type.
     */
    JTypeMirror ground(JTypeMirror t) {
        return t.subst(InferenceContext::groundSubst);
    }

    /**
     * Replace instantiated inference vars with their instantiation in the given type.
     */
    JMethodSig ground(JMethodSig t) {
        return t.subst(InferenceContext::groundSubst);
    }


    private static JTypeMirror groundSubst(SubstVar var) {
        if (var instanceof JInferenceVar) {
            JTypeMirror inst = ((JInferenceVar) var).getInst();
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
            if (!(s instanceof JInferenceVar)) {
                return s;
            } else {
                JInferenceVar ivar = (JInferenceVar) s;
                return ivar.getInst() != null ? ivar.getInst() : s.getTypeSystem().ERROR_TYPE;
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
        for (JInferenceVar freeVar : this.freeVars) {
            that.incorporationActions.add(new PropagateAllBounds(freeVar));
        }
    }


    void addInstantiationListener(Set<? extends JTypeMirror> relevantTypes, InstantiationListener listener) {
        Set<JInferenceVar> free = freeVarsIn(relevantTypes);
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
        Set<JInferenceVar> solved = new LinkedHashSet<>(inferenceVars);
        solved.removeAll(freeVars);


        for (Entry<InstantiationListener, Set<JInferenceVar>> entry : new LinkedHashSet<>(instantiationListeners.entrySet())) {
            if (solved.containsAll(entry.getValue())) {
                try {
                    entry.getKey().onInstantiation(this);
                } catch (ResolutionFailedException ignored) {
                    // that is a compile-time error, but that
                    // shouldn't affect PMD

                    // This can happen eg when an assertion fails in a
                    // subcontext that depends on this one, which is waiting
                    // for more inference to happen
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    instantiationListeners.remove(entry.getKey());
                }
            }
        }
    }

    Set<JInferenceVar> getFreeVars() {
        return Collections.unmodifiableSet(freeVars);
    }

    private void onVarInstantiated(JInferenceVar ivar) {
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


    void onBoundAdded(JInferenceVar ivar, BoundKind kind, JTypeMirror bound, boolean isSubstitution) {
        if (ivar.getDelegate() != null) {
            return;
        }
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

    void onIvarMerged(JInferenceVar prev, JInferenceVar delegate) {
        if (parent != null) {
            parent.onIvarMerged(prev, delegate);
            return;
        }

        logger.ivarMerged(this, prev, delegate);

        mapping = mapping.plus(prev.getBaseVar(), delegate);
        // freeVars.remove(prev);
        incorporationActions.addFirst(new SubstituteInst(prev, delegate));
    }

    /**
     * Runs the incorporation hooks registered for the free vars.
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

    void solve() {
        solve(new GraphWalk(this));
    }

    void solve(JInferenceVar var) {
        solve(new GraphWalk(var));
    }

    private void solve(VarWalkStrategy walker) {
        incorporate();

        while (walker.hasNext()) {

            Set<JInferenceVar> varsToSolve = walker.next();

            boolean progress = true;
            //repeat until all variables are solved
            outer:
            while (!intersect(freeVars, varsToSolve).isEmpty() && progress) {
                progress = false;
                for (Set<ReductionStep> wave : ReductionStep.WAVES) {
                    if (!solveBasic(varsToSolve, wave).isEmpty()) {
                        incorporate();
                        progress = true;
                        continue outer;
                    }
                }
            }

            // TODO in case we stopped because we aren't making progress (cyclic dep)
            //      in some cases we can recover cleanly
        }
    }

    /**
     * Tries to solve as much of varsToSolve as possible using some reduction steps.
     * Returns the set of solved variables during this step.
     */
    private Set<JInferenceVar> solveBasic(Set<JInferenceVar> varsToSolve, Set<ReductionStep> steps) {
        Set<JInferenceVar> solvedVars = new LinkedHashSet<>();
        for (JInferenceVar ivar : intersect(varsToSolve, freeVars)) {
            for (ReductionStep step : steps) {
                if (step.accepts(ivar, this)) {
                    ivar.setInst(step.solve(ivar, this));
                    onVarInstantiated(ivar);
                    solvedVars.add(ivar);
                    break;
                }
            }
        }
        return solvedVars;
    }

    public boolean isEmpty() {
        return inferenceVars.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Inference context " + getId()).append('\n');
        for (JInferenceVar ivar : inferenceVars) {
            sb.append(ivar);
            if (ivar.getInst() != null) {
                sb.append(" := ").append(ivar.getInst()).append('\n');
            } else if (ivar.getDelegate() != null) {
                sb.append(" -> ").append(ivar.getDelegate()).append('\n');
            } else {
                sb.append(" {");
                boolean any = false;
                for (BoundKind bk : BoundKind.values()) {
                    for (JTypeMirror bound : ivar.getBounds(bk)) {
                        sb.append(any ? ", " : " ").append(bk.format(ivar, bound));
                        any = true;
                    }
                }
                sb.append(any ? " }" : "}").append('\n');
            }
        }

        return sb.toString();
    }

    @Nullable JClassType toClassType(JTypeMirror t) {
        if (t instanceof JClassType) {
            return (JClassType) t;
        } else if (t instanceof JIntersectionType) {
            return ((JIntersectionType) t).getInducedClassType();
        } else if (t instanceof JInferenceVar) {
            JTypeVar baseVar = ((JInferenceVar) t).getBaseVar();
            return toClassType(mapToIVars(baseVar.getUpperBound()));
        }
        return null;
    }

    public interface InstantiationListener {


        void onInstantiation(InferenceContext solvedCtx);

    }
}
