/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.NodeStyleSpan.PositionSnapshot;


/**
 * @author Clément Fournier
 * @since 6.5.0
 */
public class UniformStyleCollection {

    private static final Map<Set<String>, Map<Integer, Set<String>>> DEPTH_STYLE_CACHE = new HashMap<>();
    private final Set<String> style;
    // sorted in document order
    private final List<NodeStyleSpan> nodes;


    public UniformStyleCollection(Set<String> style, Collection<NodeStyleSpan> ns) {
        this.style = style;
        this.nodes = new ArrayList<>(ns);
        nodes.sort(Comparator.comparing(NodeStyleSpan::getNode, Comparator.comparingInt(Node::getBeginLine).thenComparing(Node::getBeginColumn)));
    }


    public boolean isEmpty() {
        return nodes.isEmpty();
    }


    public Set<String> getStyle() {
        return style;
    }


    public UniformStyleCollection merge(UniformStyleCollection collection) {
        assert collection.getStyle().equals(getStyle());

        if (collection.isEmpty()) {
            return this;
        } else if (this.isEmpty()) {
            return collection;
        } else {
            Set<NodeStyleSpan> merged = new HashSet<>(nodes);
            merged.addAll(collection.nodes);

            return new UniformStyleCollection(style, merged);
        }
    }


    private Set<String> styleForDepth(int depth) {
        if (depth < 0) {
            return Collections.emptySet();
        } else if (depth == 0) {
            return style;
        } else {
            DEPTH_STYLE_CACHE.putIfAbsent(style, new HashMap<>());
            Map<Integer, Set<String>> depthToStyle = DEPTH_STYLE_CACHE.get(style);

            if (depthToStyle.containsKey(depth)) {
                return depthToStyle.get(depth);
            }

            Set<String> s = new HashSet<>(style);
            s.add("depth-" + depth);
            depthToStyle.put(depth, s);
            return s;
        }
    }


    StyleSpans<Collection<String>> cachedSpans;


    public StyleSpans<Collection<String>> toSpans() {
        if (cachedSpans != null) {
            return cachedSpans;
        }

        if (nodes.isEmpty()) {
            return StyleSpans.singleton(Collections.emptyList(), 0);
        } else if (nodes.size() == 1) {
            PositionSnapshot snapshot = nodes.get(0).snapshot();
            return new StyleSpansBuilder<Collection<String>>().add(Collections.emptyList(), snapshot.getBeginIndex())
                                                              .add(style, snapshot.getLength())
                                                              .create();
        }

        final StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<>();

        Deque<PositionSnapshot> overlappingNodes = new ArrayDeque<>();
        PositionSnapshot previous = null;
        int lastSpanEnd = 0;

        for (NodeStyleSpan span : nodes) {

            PositionSnapshot current = span.snapshot();
            if (current == null) {
                continue;
            }

            // first iteration
            if (previous == null) {
                previous = current;
                builder.add(Collections.emptyList(), previous.getBeginIndex());
                lastSpanEnd = previous.getBeginIndex();
                continue;
            }

            if (previous.getEndIndex() > current.getBeginIndex()) {
                // The current overlaps with the previous

                int length = current.getLength();
                int offset = current.getBeginIndex() - lastSpanEnd;


                // common part
                builder.add(styleForDepth(overlappingNodes.size()), current.getBeginIndex() - lastSpanEnd);
                lastSpanEnd = current.getBeginIndex();

                overlappingNodes.addFirst(previous);

                previous = current;
                continue;
            } else {
                // no overlap, the previous span can be added
                // the depth - 1 is for the "empty" span
                builder.add(styleForDepth(overlappingNodes.size() - 1), previous.getBeginIndex() - lastSpanEnd);
                // previous node
                builder.add(styleForDepth(overlappingNodes.size()), previous.getEndIndex() - previous.getBeginIndex());
                lastSpanEnd = previous.getEndIndex();
                previous = current;
            }

            Iterator<PositionSnapshot> overlaps = overlappingNodes.iterator();
            while (overlaps.hasNext()) {
                PositionSnapshot enclosing = overlaps.next();
                if (enclosing.getEndIndex() < current.getBeginIndex()) {
                    overlaps.remove();
                    // this is the underscored part [ [ ]_]
                    builder.add(styleForDepth(overlappingNodes.size()), enclosing.getEndIndex() - lastSpanEnd);
                    lastSpanEnd = enclosing.getEndIndex();
                }
            }
        }

        builder.add(styleForDepth(overlappingNodes.size() - 1), previous.getBeginIndex() - lastSpanEnd);
        // last node
        builder.add(styleForDepth(overlappingNodes.size()), previous.getLength());
        lastSpanEnd = previous.getEndIndex();

        // close the enclosing contexts
        int depth = overlappingNodes.size();
        for (PositionSnapshot enclosing : overlappingNodes) {
            depth--;
            builder.add(styleForDepth(depth), enclosing.getEndIndex() - lastSpanEnd);
            lastSpanEnd = enclosing.getEndIndex();
        }

        cachedSpans = builder.create();
        return cachedSpans;
    }


    public static UniformStyleCollection empty() {
        return new UniformStyleCollection(Collections.emptySet(), Collections.emptySet());
    }


}
