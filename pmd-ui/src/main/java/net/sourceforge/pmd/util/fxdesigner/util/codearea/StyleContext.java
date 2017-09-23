/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.fxmisc.richtext.StyleSpan;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

/**
 * Stores the current style layers and flattens them into a {@link StyleSpans} to style the text.
 */
class StyleContext implements Iterable<StyleLayer> {

    private static final String SYNTAX_HIGHLIGHT_LAYER_ID = "syntax";

    private final CustomCodeArea codeArea;

    /** Contains the primary highlighting layers. */
    private Map<String, StyleLayer> layersById = new HashMap<>();

    private SyntaxHighlightingComputer highlightingComputer;
    private ExecutorService executorService;
    private boolean isSyntaxHighlightingEnabled;


    StyleContext(CustomCodeArea codeArea) {
        this.codeArea = codeArea;
    }


    /**
     * Disables syntax highlighting if enabled.
     */
    public void disableSyntaxHighlighting() {
        if (isSyntaxHighlightingEnabled) {
            isSyntaxHighlightingEnabled = false;
            if (executorService != null) {
                executorService.shutdown();
            }
            StyleLayer syntaxHighlightLayer = layersById.get(SYNTAX_HIGHLIGHT_LAYER_ID);
            if (syntaxHighlightLayer != null) {
                syntaxHighlightLayer.clearStyles();
            }
        }
    }


    /**
     * Enables syntax highlighting if disabled and sets it to use the given computer.
     *
     * @param computer The computer to use
     */
    public void setSyntaxHighlighting(SyntaxHighlightingComputer computer) {
        isSyntaxHighlightingEnabled = true;
        Objects.requireNonNull(computer, "The syntax highlighting computer cannot be null");

        StyleLayer syntaxHighlightLayer = layersById.get(SYNTAX_HIGHLIGHT_LAYER_ID);
        if (syntaxHighlightLayer == null) {
            layersById.put(SYNTAX_HIGHLIGHT_LAYER_ID, new StyleLayer(SYNTAX_HIGHLIGHT_LAYER_ID, codeArea));
        }

        setSyntaxHighlightingComputer(computer);
    }


    void addLayer(String id, StyleLayer layer) {
        layersById.put(id, layer);
    }


    StyleLayer getLayer(String id) {
        return layersById.get(id);
    }


    private void setSyntaxHighlightingComputer(SyntaxHighlightingComputer computer) {
        this.highlightingComputer = computer;
        if (executorService != null) {
            executorService.shutdown();
        }
        if (isSyntaxHighlightingEnabled && highlightingComputer != null) {
            executorService = Executors.newSingleThreadExecutor();
            codeArea.richChanges()
                    .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                    .successionEnds(Duration.ofMillis(500))
                    .supplyTask(() -> highlightingComputer.computeHighlightingAsync(codeArea.getText(), executorService))
                    .awaitLatest(codeArea.richChanges())
                    .filterMap(t -> {
                        if (t.isSuccess()) {
                            return Optional.of(t.get());
                        } else {
                            t.getFailure().printStackTrace();
                            return Optional.empty();
                        }
                    })
                    .subscribe(bounds -> {
                        StyleLayer layer = layersById.get(SYNTAX_HIGHLIGHT_LAYER_ID);
                        assert layer != null;
                        layer.setBounds(bounds);
                        codeArea.paintCss(); // TODO too high level method
                    });
            codeArea.getStylesheets().add(computer.getCssFileIdentifier());
        }
    }


    /** Clears the bounds of a layer. */
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
     * Turns the currently stored bounds into a collection of style spans for use in the code area.
     *
     * @return The style spans
     */
    StyleSpans<Collection<String>> getStyleSpans() {

        List<SpanBound> spanBounds = layersById.values().stream()
                                               .filter(Objects::nonNull)
                                               .flatMap(layer -> layer.getBounds().stream())
                                               .filter(Objects::nonNull)
                                               .sorted()
                                               .collect(Collectors.toList());

        List<String> currentCssClasses = new ArrayList<>();
        int lastOffset = 0;

        StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<>();

        for (SpanBound bound : spanBounds) {
            int lengthFromLastOffset = bound.getOffset() - lastOffset;

            if (bound.isBeginBound()) {
                builder.add(new StyleSpan<>(new HashSet<>(currentCssClasses), lengthFromLastOffset));
                currentCssClasses.addAll(bound.getCssClasses());
            } else {
                builder.add(new StyleSpan<>(new HashSet<>(currentCssClasses), lengthFromLastOffset));
                for (String css : bound.getCssClasses()) { // remove only first
                    currentCssClasses.remove(css);
                }
            }

            lastOffset = bound.getOffset();
        }

        int totalLength = codeArea.getLength();
        if (lastOffset > totalLength) {
            throw new IllegalArgumentException("StyleSpans too long bitch");
        } else if (lastOffset < totalLength) {
            builder.add(new StyleSpan<>(Collections.emptySet(), totalLength - lastOffset));
        }
        return builder.create();
    }


    @Override
    public Iterator<StyleLayer> iterator() {
        return layersById.values().iterator();
    }
}
