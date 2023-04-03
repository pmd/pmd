/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import static java.lang.Math.min;
import static net.sourceforge.pmd.util.CollectionUtil.union;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.pmd.util.GraphUtil;
import net.sourceforge.pmd.util.GraphUtil.DotColor;

/**
 * A graph to walk over ivar dependencies in an efficient way.
 * This is not a general purpose implementation, there's no cleanup
 * of the vertices whatsoever, meaning each algo ({@link #mergeCycles()}
 * and {@link #topologicalSort()}) can only be done once reliably.
 */
class Graph<T> {

    /** Undefined index for Tarjan's algo. */
    private static final int UNDEFINED = -1;

    private final Set<Vertex<T>> vertices = new LinkedHashSet<>();
    // direct successors
    private final Map<Vertex<T>, Set<Vertex<T>>> successors = new HashMap<>();

    Vertex<T> addLeaf(T data) {
        Vertex<T> v = new Vertex<>(this, Collections.singleton(data));
        vertices.add(v);
        return v;
    }

    /**
     * Implicitly add both nodes to the graph and record a directed
     * edge between the first and the second.
     */
    void addEdge(Vertex<T> start, Vertex<T> end) {
        Objects.requireNonNull(end);
        Objects.requireNonNull(start);

        vertices.add(start);
        vertices.add(end);
        if (start == end) { // NOPMD CompareObjectsWithEquals
            // no self loop allowed (for tarjan), and besides an
            // inference variable depending on itself is trivial
            return;
        }
        successors.computeIfAbsent(start, k -> new LinkedHashSet<>()).add(end);
    }

    // test only
    Set<Vertex<T>> successorsOf(Vertex<T> node) {
        return successors.getOrDefault(node, Collections.emptySet());
    }

    Set<Vertex<T>> getVertices() {
        return vertices;
    }

    /**
     * Returns a list in which the vertices of this graph are sorted
     * in the following way:
     *
     * if there exists an edge u -> v, then u comes AFTER v in the list.
     */
    List<Set<T>> topologicalSort() {
        List<Set<T>> sorted = new ArrayList<>(vertices.size());
        for (Vertex<T> n : vertices) {
            toposort(n, sorted);
        }
        return sorted;
    }

    private void toposort(Vertex<T> v, List<Set<T>> sorted) {
        if (v.mark) {
            return;
        }

        for (Vertex<T> w : successorsOf(v)) {
            toposort(w, sorted);
        }

        v.mark = true;
        sorted.add(v.getData());
    }

    /**
     * Merge strongly connected components into a single node each.
     * This turns the graph into a DAG. This modifies the graph in
     * place, no cleanup of the vertices is performed.
     */
    void mergeCycles() {
        // https://en.wikipedia.org/wiki/Tarjan's_strongly_connected_components_algorithm

        TarjanState<T> state = new TarjanState<>();
        for (Vertex<T> vertex : new ArrayList<>(vertices)) {
            if (vertex.index == UNDEFINED) {
                strongConnect(state, vertex);
            }
        }
    }

    private void strongConnect(TarjanState<T> state, Vertex<T> v) {
        v.index = state.index;
        v.lowLink = state.index;
        state.index++;
        state.stack.push(v);
        v.onStack = true;

        for (Vertex<T> w : new ArrayList<>(successorsOf(v))) {
            if (w.index == UNDEFINED) {
                // Successor has not yet been visited; recurse on it
                strongConnect(state, w);
                v.lowLink = min(w.lowLink, v.lowLink);
            } else if (w.onStack) {
                // Successor w is in stack S and hence in the current SCC
                // If w is not on stack, then (v, w) is a cross-edge in the DFS tree and must be ignored
                // Note: The next line may look odd - but is correct.
                // It says w.index not w.lowlink; that is deliberate and from the original paper
                v.lowLink = min(v.lowLink, w.index);
            }
        }

        // If v is a root node, pop the stack and generate an SCC
        if (v.lowLink == v.index) {
            Vertex<T> w;
            do {
                w = state.stack.pop();
                w.onStack = false;
                // merge w into v
                v.absorb(w);
            } while (w != v); // NOPMD CompareObjectsWithEquals
        }
    }

    void onAbsorb(Vertex<T> vertex, Vertex<T> toMerge) {
        Set<Vertex<T>> succ = union(successorsOf(vertex), successorsOf(toMerge));
        succ.remove(toMerge);
        succ.remove(vertex);
        successors.put(vertex, succ);
        successors.remove(toMerge);
        vertices.remove(toMerge);
        successors.values().forEach(it -> it.remove(toMerge));
    }

    @Override
    public String toString() {
        return GraphUtil.toDot(
            vertices,
            this::successorsOf,
            v -> DotColor.BLACK,
            v -> v.data.toString()
        );
    }

    private static final class TarjanState<T> {

        int index;
        Deque<Vertex<T>> stack = new ArrayDeque<>();

    }

    static final class Vertex<T> {

        private final Graph<T> owner;
        private final Set<T> data;
        // Tarjan state
        private int index = UNDEFINED;
        private int lowLink = UNDEFINED;
        private boolean onStack = false;
        // Toposort state
        private boolean mark;

        private Vertex(Graph<T> owner, Set<T> data) {
            this.owner = owner;
            this.data = new LinkedHashSet<>(data);
        }

        public Set<T> getData() {
            return data;
        }

        /** Absorbs the given node into this node. */
        private void absorb(Vertex<T> toMerge) {
            if (this == toMerge) { // NOPMD CompareObjectsWithEquals
                return;
            }
            this.data.addAll(toMerge.data);
            owner.onAbsorb(this, toMerge);
        }

        @Override
        public String toString() {
            return data.toString();
        }
    }


    /** Maintains uniqueness of nodes wrt data. */
    static class UniqueGraph<T> extends Graph<T> {

        private final Map<T, Vertex<T>> vertexMap = new HashMap<>();

        @Override
        Vertex<T> addLeaf(T data) {
            if (vertexMap.containsKey(data)) {
                return vertexMap.get(data);
            }
            Vertex<T> v = super.addLeaf(data);
            vertexMap.put(data, v);
            return v;
        }

        @Override
        void onAbsorb(Vertex<T> vertex, Vertex<T> toMerge) {
            super.onAbsorb(vertex, toMerge);
            for (T ivar : toMerge.getData()) {
                vertexMap.put(ivar, vertex);
            }
        }
    }
}
