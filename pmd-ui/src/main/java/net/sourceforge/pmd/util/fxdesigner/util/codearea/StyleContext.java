/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import net.sourceforge.pmd.util.fxdesigner.util.codearea.ContextUpdate.LayerUpdate;


/**
 * Stores the current style layers and can overlay them into a {@link StyleSpans} to style the text.
 */
class StyleContext {


    private final CustomCodeArea codeArea;

    private final Object updateLock = new Object();
    /** Contains the highlighting layers. */
    private Map<String, StyleLayer> layersById = new HashMap<>();


    StyleContext(CustomCodeArea codeArea) {
        this.codeArea = codeArea;
    }


    public void addLayer(String id, StyleLayer layer) {
        layersById.put(id, layer);
    }


    /** Removes a layer entirely. */
    public void dropLayer(String id) {
        layersById.remove(id);
    }


    /** Performs the side effects specified by the given update. */
    public void executeUpdate(ContextUpdate update) {
        synchronized (updateLock) {
            for (String layerId : update.getSpansById().keySet()) {
                StyleLayer layer = layersById.get(layerId);
                if (layer == null) {
                    throw new IllegalStateException("Non-existent layer!");
                }
                LayerUpdate up = update.getSpansById().get(layerId);
                if (up.isReset()) {
                    layer.clearStyles();
                }

                layer.addSpans(up.getUpdates());
            }
        }
    }


    /**
     * Overlays every style layer and returns the bounds. Has no side effect on the layers.
     *
     * @return The style spans
     */
    public StyleSpans<Collection<String>> getStyleSpans() {

        List<StyleSpans<Collection<String>>> allSpans = layersById.values()
                                                                  .stream()
                                                                  .filter(Objects::nonNull)
                                                                  .map(StyleLayer::getSpans)
                                                                  .flatMap(Collection::stream)
                                                                  .filter(Objects::nonNull)
                                                                  .collect(Collectors.toList());

        if (allSpans.isEmpty()) {
            return new StyleSpansBuilder<Collection<String>>()
                .add(Collections.emptySet(), codeArea.getLength())
                .create();
        }

        final StyleSpans<Collection<String>> base = allSpans.get(0);


        return allSpans.stream()
                       .filter(spans -> spans != base)
                       .filter(spans -> spans.length() <= codeArea.getLength())
                       .reduce(allSpans.get(0),
                           (accumulator, elt) -> accumulator.overlay(elt, (style1, style2) -> {
                               Set<String> styles = new HashSet<>(style1);
                               styles.addAll(style2);
                               return styles;
                           })
                       );

    }
}
