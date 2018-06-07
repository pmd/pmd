/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
 * Collection of nodes that share the same style. In case of overlap,
 * the nested ones gain css classes like depth-1, depth-2, etc.
 *
 * @author Clément Fournier
 * @since 6.5.0
 */
public class UniformStyleCollection {

    private static final Map<Set<String>, Map<Integer, Map<Boolean, Set<String>>>> DEPTH_STYLE_CACHE = new HashMap<>();

    private final Set<String> style;
    // sorted in document order
    private final List<NodeStyleSpan> nodes;


    public UniformStyleCollection(Set<String> style, Collection<NodeStyleSpan> ns) {
        this.style = style;
        this.nodes = new ArrayList<>(ns);
        nodes.sort(NodeStyleSpan.documentOrderComparator());
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


    private boolean useInlineHighlight(Node node) {
        return node.getBeginLine() == node.getEndLine();
    }


    private Set<String> styleForDepth(int depth, Node n) {
        return styleForDepth(depth, useInlineHighlight(n));
    }


    private Set<String> styleForDepth(int depth) {
        return styleForDepth(depth, false);
    }


    private Set<String> styleForDepth(int depth, boolean inlineHighlight) {
        if (depth < 0) {
            return Collections.emptySet();
        } else if (depth == 0) {
            return style;
        } else {
            DEPTH_STYLE_CACHE.putIfAbsent(style, new HashMap<>());
            Map<Integer, Map<Boolean, Set<String>>> depthToStyle = DEPTH_STYLE_CACHE.get(style);

            depthToStyle.putIfAbsent(depth, new HashMap<>());
            Map<Boolean, Set<String>> isInlineToStyle = depthToStyle.get(depth);

            if (isInlineToStyle.containsKey(inlineHighlight)) {
                return isInlineToStyle.get(inlineHighlight);
            }

            Set<String> s = new HashSet<>(style);
            s.add("depth-" + depth);
            if (inlineHighlight) {
                s.add("inline-highlight");
            }
            isInlineToStyle.put(inlineHighlight, s);
            return s;
        }
    }


    /**
     * Overlays all the nodes in this collection into a single StyleSpans.
     * This algorithm makes the strong assumption that the nodes can be
     * ordered as a tree, that is, given two nodes n and m, then one of the
     * following holds true:
     * - m and n are disjoint
     * - m is entirely contained within n, or the reverse is true
     *
     * E.g. [    m        ] but not [  m  ]
     *        [ n ] [ n' ]              [   n   ]
     */
    public StyleSpans<Collection<String>> toSpans() {

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

                // This part sometimes throws exceptions when the text changes while the computation is in progress
                // In practice, they can totally be ignored since the highlighting will be recomputed next time
                // the code area is recomputed.

                // gap
                builder.add(styleForDepth(overlappingNodes.size() - 1), previous.getBeginIndex() - lastSpanEnd);
                // common part
                builder.add(styleForDepth(overlappingNodes.size(), current.getNode()), current.getBeginIndex() - previous.getBeginIndex());
                lastSpanEnd = current.getBeginIndex();

                overlappingNodes.addFirst(previous);

                previous = current;
                continue;
            } else {
                // no overlap, the previous span can be added

                // the depth - 1 is for the gap
                builder.add(styleForDepth(overlappingNodes.size() - 1), previous.getBeginIndex() - lastSpanEnd);
                // previous node
                builder.add(styleForDepth(overlappingNodes.size(), previous.getNode()), previous.getLength());
                lastSpanEnd = previous.getEndIndex();
                previous = current;
            }

            // first check whether some of the enclosing spans end between the end of the previous and the beginning of the current
            Iterator<PositionSnapshot> overlaps = overlappingNodes.iterator();
            while (overlaps.hasNext()) {
                PositionSnapshot enclosing = overlaps.next();
                if (enclosing.getEndIndex() < current.getBeginIndex()) {
                    overlaps.remove();
                    // this is the underscored part [ [ ]_]
                    builder.add(styleForDepth(overlappingNodes.size(), enclosing.getNode()), enclosing.getEndIndex() - lastSpanEnd);
                    lastSpanEnd = enclosing.getEndIndex();
                }
            }
        }

        builder.add(styleForDepth(overlappingNodes.size() - 1), previous.getBeginIndex() - lastSpanEnd);
        // last node
        builder.add(styleForDepth(overlappingNodes.size(), previous.getNode()), previous.getLength());
        lastSpanEnd = previous.getEndIndex();

        // close the enclosing contexts
        int depth = overlappingNodes.size();
        for (PositionSnapshot enclosing : overlappingNodes) {
            depth--;
            builder.add(styleForDepth(depth, enclosing.getNode()), enclosing.getEndIndex() - lastSpanEnd);
            lastSpanEnd = enclosing.getEndIndex();
        }

        return builder.create();
    }


    public static UniformStyleCollection empty() {
        return new UniformStyleCollection(Collections.emptySet(), Collections.emptySet());
    }


}
