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
import java.util.stream.Collectors;

import org.fxmisc.richtext.model.StyleSpans;
import org.reactfx.value.Var;


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



    private StyleSpans<Collection<String>> emptySpan() {
        return StyleSpans.singleton(Collections.emptyList(), codeArea.getLength());
    }


    /**
     * Recomputes a single style spans from the syntax highlighting layer and nodes to highlight.
     *
     */
    public StyleSpans<Collection<String>> recomputePainting() {

        List<StyleSpans<Collection<String>>> allSpans = layersById.values().stream()
                                                                  .flatMap(layer -> layer.getCollections().stream())
                                                                  .map(UniformStyleCollection::toSpans)
                                                                  .collect(Collectors.toList());

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
