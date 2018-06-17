/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.fxmisc.richtext.model.StyleSpans;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.HighlightLayerCodeArea.LayerId;

import javafx.beans.NamedArg;


/**
 * Code area that can manipulate different layers of styling independently,
 * in addition to syntax highlighting. Layers are identified by a {@link LayerId},
 * which are listed in an enum.
 *
 * @param <K> Enum type listing the layer ids to use
 * @author Clément Fournier
 * @since 6.5.0
 */
public class HighlightLayerCodeArea<K extends Enum<K> & LayerId> extends SyntaxHighlightingCodeArea {


    /** Contains the highlighting layers. */
    private final Map<K, StyleLayer> layersById;


    /**
     * Builds a new code area with the given enum type as layer id provider.
     * Constants of the enum will identify layers of the code area.
     *
     * @param idEnum Enum type
     */
    // the annotation lets the value be passed from FXML
    public HighlightLayerCodeArea(@NamedArg("idEnum") Class<K> idEnum) {
        super();

        this.layersById = EnumSet.allOf(idEnum)
                                 .stream()
                                 .collect(Collectors.toConcurrentMap(id -> id, id -> new StyleLayer()));
    }


    /**
     * Styles some nodes in a given layer and updates the visual appearance of the area.
     *
     * <p>Each layer has its own style class, that is assigned to the nodes
     * that belong to it.
     *
     * @param nodes      Nodes to style
     * @param layerId    Id of the layer in which to save the node highlight
     * @param resetLayer Whether to replace the contents of the layer with the
     *                   styling for these nodes, or just add them.
     */
    // TODO there's no reason to only be able to style nodes, in fact, this causes problem
    // to highlight errors that are not bound to a node, eg parsing errors
    // We'll need to abstract away NodeStyleSpan
    public void styleNodes(Collection<? extends Node> nodes, K layerId, boolean resetLayer) {
        Objects.requireNonNull(nodes, "Pass an empty collection to represent absence, not null!");

        if (nodes.isEmpty() && resetLayer) {
            clearStyleLayer(layerId);
            return;
        }

        List<NodeStyleSpan> wrappedNodes = nodes.stream().map(n -> NodeStyleSpan.fromNode(n, this)).collect(Collectors.toList());

        UniformStyleCollection collection = new UniformStyleCollection(Collections.singleton(layerId.getStyleClass()), wrappedNodes);

        updateStyling(() -> layersById.get(layerId).styleNodes(resetLayer, collection));
    }


    /**
     * Applies the given update and applies the styling to the code area.
     * We use a closure parameter to encapsulate the application of the
     * update inside the restyling procedure, and mostly to make obvious
     * that each update needs restyling, and each restyling needs an update.
     *
     * @param update Update to carry out
     */
    private void updateStyling(Runnable update) {
        update.run();

        try {
            this.setStyleSpans(0, recomputePainting());
        } catch (Exception e) {
            // we ignore these particular exceptions because they're
            // commonly thrown when the text is being edited while
            // the layering algorithm runs, and it doesn't matter
            if ("StyleSpan's length cannot be negative".equals(e.getMessage())
                    || e.getMessage().contains("is not a valid range within")) {
                return;
            }
            throw new RuntimeException("Unhandled error while recomputing the styling", e);
        }
    }


    /**
     * Clears all style layers from their contents, including syntax highlighting.
     */
    public void clearStyleLayers() {
        updateStyling(() -> {
            layersById.values().forEach(StyleLayer::clearStyles);
            clearSyntaxHighlighting();
        });
    }


    /**
     * Clears a style layer.
     *
     * @param id layer id.
     */
    public void clearStyleLayer(K id) {
        updateStyling(layersById.get(id)::clearStyles);
    }


    /**
     * Recomputes a single style spans from the syntax highlighting layer and nodes to highlight.
     */
    private StyleSpans<Collection<String>> recomputePainting() {

        List<StyleSpans<Collection<String>>> allSpans = layersById.values().stream()
                                                                  .flatMap(layer -> layer.getCollections().stream())
                                                                  .filter(c -> !c.isEmpty())
                                                                  .map(UniformStyleCollection::toSpans)
                                                                  .collect(Collectors.toList());

        if (allSpans.isEmpty()) {
            return syntaxHighlight.getOrElse(emptySpan());
        }

        if (syntaxHighlight.getOpt().map(StyleSpans::length).filter(l -> l != getLength()).isPresent()) {
            // This is only executed if the text has changed (we use the length as an approximation)
            // This makes the highlighting much more resilient to staccato code changes,
            // which previously would have overlaid an outdated syntax highlighting layer on the
            // up-to-date node highlights, making the highlighting twitch briefly before the
            // asynchronous syntax highlight catches up
            updateSyntaxHighlightingSynchronously();
        }

        syntaxHighlight.ifPresent(allSpans::add);

        final StyleSpans<Collection<String>> base = allSpans.get(0);

        return allSpans.stream()
                       .filter(spans -> spans != base)
                       .filter(spans -> spans.length() <= getLength())
                       .reduce(base, (accumulator, elt) -> accumulator.overlay(elt, SyntaxHighlightingCodeArea::additiveOverlay));


    }


    /** Identifier for a highlighting layer. */
    public interface LayerId {
        /**
         * Returns the style class associated with that layer.
         * Nodes styled in that layer will have this style class.
         *
         * @return The style class
         */
        String getStyleClass();
    }
}
