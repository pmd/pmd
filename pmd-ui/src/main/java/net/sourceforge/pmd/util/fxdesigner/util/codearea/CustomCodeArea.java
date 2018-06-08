/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.reactfx.EventSource;
import org.reactfx.EventStream;
import org.reactfx.Subscription;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.util.ConvenienceNodeWrapper;

import javafx.concurrent.Task;


/**
 * Code area that can handle syntax highlighting as well as regular node highlighting.
 * Regular node highlighting is handled in several layers, identified by a {@link LayerId}.
 * The contents of these layers can be handled independently with {@link #styleCss(Collection, LayerId, boolean, String...)},
 * {@link #clearStyleLayer(LayerId)} and the like.
 *
 * <p>Syntax highlighting uses another internal style layer. Syntax highlighting
 * is performed asynchronously by another thread.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class CustomCodeArea extends CodeArea {

    /** Minimum delay between each style update. Updates are reduced together until then. */
    private static final Duration UPDATE_DELAY = Duration.ofMillis(50);
    /** Minimum delay between each code highlighting recomputation. Changes are ignored until then. */
    private static final Duration TEXT_CHANGE_DELAY = Duration.ofMillis(30);

    /** Stacks styling updates and reduces them together. */
    private final EventSource<ContextUpdate> styleContextUpdateQueue = new EventSource<>();

    private Subscription syntaxAutoRefresh = () -> { };

    private final StyleContext styleContext;
    private final Var<SyntaxHighlighter> syntaxHighlighter = Var.newSimpleVar(null);


    public CustomCodeArea() {
        super();

        Set<String> collect = Arrays.stream(LayerId.values())
                                    .map(LayerId::getId)
                                    .collect(Collectors.toCollection(LinkedHashSet::new));

        styleContext = new StyleContext(collect, this);

        styleContextUpdateQueue.reduceSuccessions(ContextUpdate::unit, ContextUpdate::reduce, UPDATE_DELAY)
                               .hook(styleContext::executeUpdate)
                               .subscribe(update -> this.paintCss());
    }


    /**
     * Styles some nodes in a given layer.
     *
     * <p>The focus layer is meant for the node in primary focus, in contrast
     * with the secondary layer, which highlights some nodes that are related
     * to a specific selection (eg error nodes correspond to an error, name
     * occurrences correspond to a name declaration). The XPath result layer
     * highlights nodes that are independent from any selection (they depend
     * on the xpath results).
     *
     * @param nodes      Nodes to style
     * @param layerId    Id of the layer in which to save the node highlight
     * @param resetLayer Whether to replace the contents of the layer with the
     *                   styling for these nodes
     * @param cssClasses CSS classes to apply
     */
    public void styleCss(Collection<? extends Node> nodes, LayerId layerId, boolean resetLayer, String... cssClasses) {
        Set<String> fullClasses = new HashSet<>(Arrays.asList(cssClasses));
        fullClasses.add("text");
        fullClasses.add("styled-text-area");
        fullClasses.add(layerId.id + "-highlight"); // focus-highlight, xpath-highlight, secondary-highlight

        List<NodeStyleSpan> wrappedNodes = nodes.stream().map(n -> NodeStyleSpan.fromNode(n, this)).collect(Collectors.toList());
        UniformStyleCollection collection = new UniformStyleCollection(fullClasses, wrappedNodes);

        ContextUpdate update = ContextUpdate.layerUpdate(layerId.id, resetLayer, collection);
        styleContextUpdateQueue.push(update);
    }


    /**
     * Forcefully applies the possibly updated css classes.
     * This operation is expensive, and should not be executed
     * in a loop for example.
     */
    private void paintCss() {
        try {
            this.setStyleSpans(0, styleContext.recomputePainting());
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
        for (LayerId id : LayerId.values()) {
            clearStyleLayer(id);
        }

        styleContext.setSyntaxHighlight(null);
        paintCss();
    }


    /**
     * Clears a style layer.
     *
     * @param id layer id.
     */
    public void clearStyleLayer(LayerId id) {
        styleContextUpdateQueue.push(ContextUpdate.clearUpdate(id.id));
    }


    /**
     * Enables syntax highlighting if disabled and sets it to use the given highlighter.
     * If null, then this method disables syntax highlighting.
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


    public ConvenienceNodeWrapper wrapNode(Node node) {
        return NodeStyleSpan.fromNode(node, this).snapshot();
    }


    /** Public style layers of the code area. */
    public enum LayerId {
        /** For the currently selected node. */
        FOCUS("focus"),
        // TODO using a specific layer for each of those may be a better idea
        /** For nodes in error, declaration usages. */
        SECONDARY("secondary"),
        /** For xpath results. */
        XPATH_RESULTS("xpath");


        String getId() {
            return id;
        }


        private final String id;

        LayerId(String id) {
            this.id = id;
        }
    }
}
