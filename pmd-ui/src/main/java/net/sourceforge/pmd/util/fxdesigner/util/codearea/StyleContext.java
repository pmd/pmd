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
import java.util.SortedSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;


/**
 * Stores the current style layers and can overlay them into a {@link StyleSpans} to style the text.
 */
class StyleContext {


    /** Contains the highlighting layers. */
    private final Map<String, StyleLayer> layersById;


    StyleContext(SortedSet<String> ids) {
        layersById = ids.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toConcurrentMap(id -> id, id -> new StyleLayer()));
    }


    public Optional<StyleLayer> getLayer(String id) {
        return Optional.ofNullable(layersById.get(id));
    }


    /** Performs the side effects specified by the given update. */
    public void executeUpdate(ContextUpdate update) {
        update.apply(this);
    }


    /**
     * Overlays every style layer and returns the bounds. Has no side effect on the layers.
     *
     * @param lengthCallBack A callback to get the current length of the text in the code area.
     *                       Evaluating it each time catches some bugs when the source changes quickly.
     *
     * @return The style spans
     */
    public StyleSpans<Collection<String>> getStyleSpans(Supplier<Integer> lengthCallBack) {

        List<StyleSpans<Collection<String>>> allSpans = layersById.values()
                                                                  .stream()
                                                                  .filter(Objects::nonNull)
                                                                  .map(StyleLayer::getSpans)
                                                                  .flatMap(Collection::stream)
                                                                  .filter(Objects::nonNull)
                                                                  .collect(Collectors.toList());

        if (allSpans.isEmpty()) {
            return new StyleSpansBuilder<Collection<String>>()
                    .add(Collections.emptySet(), lengthCallBack.get())
                    .create();
        }

        final StyleSpans<Collection<String>> base = allSpans.get(0);


        return allSpans.stream()
                       .filter(spans -> spans != base)
                       .filter(spans -> spans.length() <= lengthCallBack.get())
                       .reduce(allSpans.get(0),
                           (accumulator, elt) -> accumulator.overlay(elt, (style1, style2) -> {
                               Set<String> styles = new HashSet<>(style1);
                               styles.addAll(style2);
                               return styles;
                           })
                       );

    }
}
