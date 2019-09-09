/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;

/**
 * Represents a lazy flatmap operation.
 */
public final class FlatmapNodeStream<T extends Node> implements NodeStream<T> {

    private final NodeStream<?> input;
    private final List<PathSegment<?, ?>> path;

    private List<T> listCache;

    public <I extends Node> FlatmapNodeStream(NodeStream<I> in, Function<? super I, ? extends NodeStream<? extends T>> mapper) {
        this(in, mapper, PredicateUtil.truePredicate());
    }

    public FlatmapNodeStream(NodeStream<T> in, Predicate<? super T> filter) {
        this(in, NodeStream::of, filter);
    }

    public <I extends Node> FlatmapNodeStream(NodeStream<I> in,
                                              Function<? super I, ? extends NodeStream<? extends T>> mapper,
                                              Predicate<? super T> filter) {
        this.input = in;
        this.path = Collections.singletonList(new PathSegment<>(mapper, filter));
    }

    private FlatmapNodeStream(NodeStream<?> input,
                              List<PathSegment<?, ?>> path) {
        this.input = input;
        this.path = path;
    }

    @Override
    public Stream<T> toStream() {
        return toList().stream();
    }

    @Override
    public <R extends Node> NodeStream<R> flatMap(Function<? super T, ? extends NodeStream<? extends R>> mapper) {
        return new FlatmapNodeStream<>(input, plus(path, new PathSegment<T, R>(mapper, PredicateUtil.truePredicate())));
    }

    @Override
    public NodeStream<T> filter(Predicate<? super T> predicate) {
        ArrayList<PathSegment<?, ?>> result = new ArrayList<>(path);
        result.add(getLastSegment(result).withFilter(predicate));

        return new FlatmapNodeStream<>(input, result);
    }

    @SuppressWarnings("unchecked")
    private PathSegment<?, T> getLastSegment(ArrayList<PathSegment<?, ?>> result) {
        return (PathSegment<?, T>) result.remove(result.size() - 1);
    }

    @Override
    public List<T> toList() {
        if (listCache == null) {
            List<Node> in = new ArrayList<>(input.toList());
            List<Node> out = new ArrayList<>(in.size() * 2);

            for (PathSegment<?, ?> segment : path) {
                segment.applySegment(in, out);

                List<Node> tmp = in;
                in = out;
                out = tmp;
                out.clear();
            }

            @SuppressWarnings("unchecked")
            List<T> result = (List<T>) Collections.unmodifiableList(in);
            this.listCache = result;
        }
        return listCache;
    }

    private static <T> List<T> plus(List<? extends T> ts, T t) {
        ArrayList<T> result = new ArrayList<>(ts);
        result.add(t);
        return result;
    }

    private static class PathSegment<I extends Node, O extends Node> {

        private final Function<? super I, ? extends NodeStream<? extends O>> mapper;
        private final Predicate<O> filter;


        private PathSegment(Function<? super I, ? extends NodeStream<? extends O>> mapper, Predicate<? super O> filter) {
            this.mapper = mapper;
            this.filter = (Predicate<O>) filter;
        }

        PathSegment<I, O> withFilter(Predicate<? super O> filter) {
            return new PathSegment<>(mapper, this.filter.and(filter));
        }

        void applySegment(List<Node> in, List<Node> out) {
            for (Node node : in) {
                NodeStream<? extends O> ns = mapper.apply((I) node);
                if (ns == null) {
                    continue;
                }
                for (O n : ns) {
                    if (filter.test(n)) {
                        out.add(n);
                    }
                }
            }
        }
    }
}
