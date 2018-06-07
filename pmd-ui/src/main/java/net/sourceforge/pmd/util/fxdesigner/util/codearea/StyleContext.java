/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.value.Var;

import net.sourceforge.pmd.util.fxdesigner.util.codearea.NodeStyleSpan.PositionSnapshot;


/**
 * Stores the current style layers and can overlay them into a {@link StyleSpans} to style the text.
 */
class StyleContext {


    /** Contains the highlighting layers. */
    private final Map<String, StyleLayer> layersById;
    private final CustomCodeArea codeArea;

    private final Var<StyleSpans<Collection<String>>> syntaxHighlight = Var.newSimpleVar(null);


    StyleContext(Set<String> ids, CustomCodeArea codeArea) {

        layersById = ids.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toConcurrentMap(id -> id, id -> new StyleLayer()));
        this.codeArea = codeArea;
    }


    public Optional<StyleLayer> getLayer(String id) {
        return Optional.ofNullable(layersById.get(id));
    }


    /** Performs the side effects specified by the given update. */
    public void executeUpdate(ContextUpdate update) {
        update.apply(this);
    }


    public void setSyntaxHighlight(StyleSpans<Collection<String>> spans) {
        syntaxHighlight.setValue(spans);
    }


    /**
     * Computes the style spans corresponding to the styling of the given nodes
     *
     * @param highlightedNodes Nodes to highlight, mapped to their highlight class, sorted in document order
     */
    private List<StyleSpans<Collection<String>>> nodeHighlight(StyleCollection highlightedNodes) {

        if (highlightedNodes.isEmpty()) {
            return Collections.emptyList();
        }

        List<StyleSpans<Collection<String>>> result = new Stack<>();

        final StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<>();

        PositionSnapshot previous = null;
        StyleCollection nextPass = new StyleCollection();

        // Sorted in document order
        for (NodeStyleSpan coord : highlightedNodes) {

            // snapshots the node's position, which factors in changes to the nodes's position
            // that occurred since the node was parsed
            PositionSnapshot snapshot = coord.snapshot();
            if (snapshot == null) {
                continue;
            }
            int previousEnd = previous == null ? 0 : previous.getEndIndex();

            if (snapshot.getBeginIndex() < previousEnd) {
                // This node overlaps with the previous one
                // TODO This is a lazy technique to have the overlay work done by RichtextFX instead of here
                nextPass.add(coord);
                continue;
            }

            // This is the in-between, empty span
            builder.add(Collections.emptySet(), snapshot.getBeginIndex() - previousEnd);
            builder.add(snapshot.toSpan());
            previous = snapshot;
        }

        result.add(builder.create());
        result.addAll(nodeHighlight(nextPass));

        return result;
    }


    private StyleSpans<Collection<String>> emptySpan() {
        return StyleSpans.singleton(Collections.emptyList(), codeArea.getLength());
    }


    /**
     * Recomputes a single style spans from the syntax highlighting layer and nodes to highlight.
     *
     */
    public StyleSpans<Collection<String>> recomputePainting() {

        final StyleCollection allCoordinates = new StyleCollection();

        layersById.values().stream()
                  .map(StyleLayer::getStyleSpansCoordinates)
                  .forEach(allCoordinates::addAll);

        List<StyleSpans<Collection<String>>> allSpans = nodeHighlight(allCoordinates);

        if (allSpans.isEmpty()) {
            return syntaxHighlight.getOrElse(emptySpan());
        }

        syntaxHighlight.ifPresent(allSpans::add);

        final StyleSpans<Collection<String>> base = allSpans.get(0);

        return allSpans.stream()
                       .filter(spans -> spans != base)
                       .filter(spans -> spans.length() <= codeArea.getLength())
                       .reduce(base,
                               (accumulator, elt) -> accumulator.overlay(elt, (style1, style2) -> {
                                   Set<String> styles = new HashSet<>(style1);
                                   styles.addAll(style2);
                                   return styles;
                               })
                       );


    }
}
