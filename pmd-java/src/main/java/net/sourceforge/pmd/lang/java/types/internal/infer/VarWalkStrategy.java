/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.java.types.internal.infer.Graph.UniqueGraph;
import net.sourceforge.pmd.lang.java.types.internal.infer.Graph.Vertex;
import net.sourceforge.pmd.lang.java.types.internal.infer.JInferenceVar.BoundKind;

/**
 * Strategy to walk the set of remaining free variables. Interdependent
 * variables must be solved together.
 */
interface VarWalkStrategy extends Iterator<Set<JInferenceVar>> {

    /**
     * Picks the next batch of inference vars to resolve.
     * Interdependent variables must be solved together.
     */
    @Override
    Set<JInferenceVar> next();


    /**
     * Returns true if there is no more batch to process.
     */
    @Override
    boolean hasNext();


    /**
     * Walk a DAG of the dependencies. Building the model is
     * is linear instead of exponential like for the other strategy.
     */
    class GraphWalk implements VarWalkStrategy {

        private final Iterator<Set<JInferenceVar>> iterator;

        GraphWalk(InferenceContext infCtx) {
            this.iterator = buildGraphIterator(infCtx);
        }

        GraphWalk(JInferenceVar var) {
            this.iterator = IteratorUtil.singletonIterator(Collections.singleton(var));
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Set<JInferenceVar> next() {
            return iterator.next();
        }

        Iterator<Set<JInferenceVar>> buildGraphIterator(InferenceContext ctx) {

            Set<JInferenceVar> freeVars = ctx.getFreeVars();
            if (freeVars.size() == 1) {
                // common case
                return IteratorUtil.singletonIterator(freeVars);
            }

            // Builds a graph representing the dependencies
            // between free ivars in the context.

            Graph<JInferenceVar> graph = new UniqueGraph<>();

            for (JInferenceVar ivar : freeVars) {
                Vertex<JInferenceVar> vertex = graph.addLeaf(ivar);
                Set<JInferenceVar> dependencies = ctx.freeVarsIn(ivar.getBounds(BoundKind.ALL));
                for (JInferenceVar dep : dependencies) {
                    Vertex<JInferenceVar> target = graph.addLeaf(dep);
                    graph.addEdge(vertex, target);
                }
                if (ivar.getDelegate() != null) {
                    Vertex<JInferenceVar> target = graph.addLeaf(ivar.getDelegate());
                    graph.addEdge(vertex, target);
                }
            }

            // Here, "α depends on β" is modelled by an edge α -> β

            // Merge strongly connected components into a "super node".
            // Eg α -> β -> α is replaced with a single node [α,β] to
            // remove the cycle. Each node then has a batch of variables
            // that must be solved together. The resulting graph is a
            // directed acyclic graph
            graph.mergeCycles();

            if (graph.getVertices().size() == 1) {
                // All variables are interdependent
                return IteratorUtil.singletonIterator(freeVars);
            }

            // Next we do a topological sort of the DAG
            // This means the iterator will yield batches of vars
            // in such a way that if a batch A has a dependency on
            // a batch B, then B will be yielded before A

            // meaning we'll solve variables in the correct order,
            // which also respects the JLS
            return graph.topologicalSort().iterator();
        }
    }

}
