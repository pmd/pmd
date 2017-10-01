/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

/**
 * Stores the current style layers and can overlay them into a {@link StyleSpans} to style the text.
 */
class StyleContext implements Iterable<StyleLayer> {


    private final CustomCodeArea codeArea;

    /** Contains the highlighting layers. */
    private Map<String, StyleLayer> layersById = new HashMap<>();


    StyleContext(CustomCodeArea codeArea) {
        this.codeArea = codeArea;
    }


    void addLayer(String id, StyleLayer layer) {
        layersById.put(id, layer);
    }


    StyleLayer getLayer(String id) {
        return layersById.get(id);
    }


    /** Clears the spans of a layer. */
    public void clearLayer(String id) {
        StyleLayer layer = layersById.get(id);
        if (layer != null) {
            layer.clearStyles();
        }
    }


    /** Removes a layer entirely. */
    void dropLayer(String id) {
        layersById.remove(id);
    }


    /**
     * Overlays every style layer and returns the bounds. Has no side effect on the layers.
     *
     * @return The style spans
     */
    StyleSpans<Collection<String>> getStyleSpans() {

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
                       .reduce(allSpans.get(0),
                               (accumulator, elt) -> accumulator.overlay(elt, (style1, style2) -> {
                                   Set<String> styles = new HashSet<>(style1);
                                   styles.addAll(style2);
                                   return styles;
                               })
                       );

    }


    @Override
    public Iterator<StyleLayer> iterator() {
        return layersById.values().iterator();
    }


}
