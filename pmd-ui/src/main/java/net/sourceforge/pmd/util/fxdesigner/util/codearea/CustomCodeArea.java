/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.reactfx.EventStream;
import org.reactfx.Subscription;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.util.TextAwareNodeWrapper;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.StyleContext.ContextUpdate;

import javafx.concurrent.Task;


/**
 * Code area that can handle syntax highlighting as well as regular node highlighting.
 * Regular node highlighting is handled in several layers, identified by a {@link LayerId}.
 * The contents of these layers can be handled independently with
 * {@link #styleNodes(Collection, LayerId, boolean)}, {@link #clearStyleLayer(LayerId)} and the like.
 *
 * <p>Syntax highlighting is performed asynchronously by another thread. It can be enabled
 * by providing a {@link SyntaxHighlighter} to {@link #setSyntaxHighlighter(SyntaxHighlighter)},
 * and disabled by passing a {@code null} reference to that method.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class CustomCodeArea extends CodeArea {
    // TODO specialize this code area into
    // * a general purpose one that can syntax highlight + error highlight
    // * a more specialized one that is made to display *nodes* (for the main editor mainly)
    // The XPath area can't handle nodes as XPath is not supported by pmd... TODO?

    /** Minimum delay between each code highlighting recomputation. Changes are ignored until then. */
    private static final Duration TEXT_CHANGE_DELAY = Duration.ofMillis(30);

    /** Current subscription to syntax highlighting auto-refresh. Never null (noop when absent). */
    private Subscription syntaxAutoRefresh = () -> { };

    private final StyleContext styleContext;
    private final Var<SyntaxHighlighter> syntaxHighlighter = Var.newSimpleVar(null);


    public CustomCodeArea() {
        super();

        Set<String> collect = Arrays.stream(LayerId.values())
                                    .map(LayerId::getId)
                                    .collect(Collectors.toCollection(LinkedHashSet::new));

        styleContext = new StyleContext(collect, this);
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
    public void styleNodes(Collection<? extends Node> nodes, LayerId layerId, boolean resetLayer) {
        Objects.requireNonNull(nodes, "Pass an empty collection to represent absence, not null!");
        
        if (nodes.isEmpty() && resetLayer) {
            clearStyleLayer(layerId);
            return;
        }

        List<NodeStyleSpan> wrappedNodes = nodes.stream().map(n -> NodeStyleSpan.fromNode(n, this)).collect(Collectors.toList());

        UniformStyleCollection collection = new UniformStyleCollection(Collections.singleton(layerId.getStyleClass()), wrappedNodes);

        updateStyling(styleContext.layerUpdate(layerId.id, resetLayer, collection));
    }


    /**
     * Applies the given update and applies the styling to the code area.
     * @param update Update to carry out
     */
    private void updateStyling(ContextUpdate update) {
        try {
            this.setStyleSpans(0, styleContext.recomputePainting(update));
        } catch (IllegalArgumentException e) {
            // we ignore this particular exception because it's
            // commonly thrown when the text is being edited while
            // the layering algorithm runs, and it doesn't matter
            if (!"StyleSpan's length cannot be negative".equals(e.getMessage())) {
                throw new RuntimeException("Unhandled error while recomputing the styling", e);
            }
        }
    }


    /**
     * Clears all style layers from their contents.
     */
    public void clearStyleLayers() {
        updateStyling(styleContext.resetAllUpdate());
    }


    /**
     * Clears a style layer.
     *
     * @param id layer id.
     */
    public void clearStyleLayer(LayerId id) {
        updateStyling(styleContext.clearUpdate(id.id));
    }


    /**
     * Enables syntax highlighting if disabled and sets it to use the given highlighter.
     * If the argument is null, then this method disables syntax highlighting.
     */
    public void setSyntaxHighlighter(SyntaxHighlighter highlighter) {

        if (Objects.equals(highlighter, syntaxHighlighter.getValue())) {
            return;
        }

        syntaxHighlighter.ifPresent(previous -> getStyleClass().remove("." + previous.getLanguageTerseName()));
        syntaxAutoRefresh.unsubscribe();

        if (highlighter == null) {
            syntaxAutoRefresh = () -> { };
            styleContext.setSyntaxHighlight(null);
            return;
        }

        syntaxHighlighter.setValue(highlighter);

        getStyleClass().add("." + highlighter.getLanguageTerseName());
        syntaxAutoRefresh = subscribeSyntaxHighlighting(defaultHighlightingTicks(), highlighter);

        try { // refresh the highlighting once.
            Task<StyleSpans<Collection<String>>> t = computeHighlightingAsync(Executors.newSingleThreadExecutor(), highlighter, getText());
            t.setOnSucceeded(e -> styleContext.setSyntaxHighlight(t.getValue()));
        } catch (Exception ignored) {
            // nevermind
        }
    }


    public Val<Boolean> syntaxHighlightingEnabledProperty() {
        return syntaxHighlighter.map(Objects::nonNull);
    }


    private EventStream<?> defaultHighlightingTicks() {
        return this.plainTextChanges()
                   .filter(ch -> !ch.isIdentity())
                   .distinct();
    }


    private Subscription subscribeSyntaxHighlighting(EventStream<?> ticks, SyntaxHighlighter highlighter) {
        // captured in the closure, shutdown when unsubscribing
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        return ticks.successionEnds(TEXT_CHANGE_DELAY)
                    .supplyTask(() -> computeHighlightingAsync(executorService, highlighter, this.getText()))
                    .awaitLatest(ticks)
                    .filterMap(t -> {
                        t.ifFailure(Throwable::printStackTrace);
                        return t.toOptional();
                    })
                    .subscribe(styleContext::setSyntaxHighlight)
                    .and(executorService::shutdown);
    }


    private static Task<StyleSpans<Collection<String>>> computeHighlightingAsync(ExecutorService service, SyntaxHighlighter highlighter, String text) {
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() {
                return highlighter.computeHighlighting(text);
            }
        };
        if (!service.isShutdown()) {
            service.execute(task);
        }
        return task;
    }


    /**
     * Gets the most up to date syntax highlighting layer, synchronously.
     * Returns empty if there is not syntax highlighter.
     */
    Optional<StyleSpans<Collection<String>>> getUpToDateSyntaxHighlighting() {
        return syntaxHighlighter.getOpt().map(h -> h.computeHighlighting(getText()));
    }


    /** Wraps a node into a convenience layer that can for example provide the rich text associated with it. */
    public TextAwareNodeWrapper wrapNode(Node node) {
        return NodeStyleSpan.fromNode(node, this).snapshot();
    }


    /** Public style layers of the code area. */
    public enum LayerId {
        /** For the currently selected node. */
        FOCUS("focus"),
        /** For declaration usages. */
        NAME_OCCURENCES("name-occurrence"),
        /** For nodes in error. */
        ERROR("error"),
        /** For xpath results. */
        XPATH_RESULTS("xpath");

        private final String id; // the id will be used as a style class

        String getId() {
            return id;
        }


        /** focus-highlight, xpath-highlight, error-highlight, name-occurrence-highlight */
        String getStyleClass() {
            return id + "-highlight";
        }

        LayerId(String id) {
            this.id = id;
        }
    }
}
