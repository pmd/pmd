/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.CustomCodeArea.LayerId.SYNTAX_HIGHLIGHT_LAYER_ID;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.CustomCodeArea.LayerId.XPATH_RESULTS;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.RichTextChange;
import org.fxmisc.richtext.model.StyleSpans;
import org.reactfx.EventSource;
import org.reactfx.Subscription;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

import net.sourceforge.pmd.lang.ast.Node;

import javafx.application.Platform;
import javafx.collections.ObservableList;
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
    private static final Duration TEXT_CHANGE_DELAY = Duration.ofMillis(20);

    /** Stacks styling updates and reduces them together. */
    private final EventSource<ContextUpdate> styleContextUpdateQueue = new EventSource<>();

    private Subscription syntaxAutoRefresh;

    private StyleContext styleContext;
    private Var<SyntaxHighlighter> syntaxHighlighter = Var.newSimpleVar(null);


    /** Used to schedule tasks that must be restarted upon new changes, eg syntax highlighting */
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Map<Integer, Integer> offsetAccumulator = new TreeMap<>();

    public CustomCodeArea() {
        super();

        Set<String> collect = Arrays.stream(LayerId.values())
                                    .map(LayerId::getId)
                                    .collect(Collectors.toCollection(LinkedHashSet::new));

        styleContext = new StyleContext(collect, this);

        richChanges().map(RichTextChange::toPlainTextChange)
                     .filter(ch -> ch.getNetLength() != 0)
                     .subscribe(ch -> offsetAccumulator.put(ch.getPosition(), ch.getNetLength()));

        styleContextUpdateQueue.reduceSuccessions(ContextUpdate::unit, ContextUpdate::reduce, UPDATE_DELAY)
                               .hook(styleContext::executeUpdate)
                               .subscribe(update -> Platform.runLater(this::paintCss));
    }

    // FIXME the highlighting is offset to the left (right) each time we insert some text before (after)
    // This is because all layers stay the same, while the syntax highlighting is updated more often and
    // overlaid with those outdated spans, whose indices are offset by the length of the insertion

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

        List<NodeStyleSpan> wrappedNodes = nodes.stream().map(n -> NodeStyleSpan.fromNode(n, fullClasses, this)).collect(Collectors.toList());
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
        this.setStyleSpans(0, styleContext.recomputePainting()); // TODO
    }


    public void resetOffsets() {
        offsetAccumulator.clear();
        paintCss();
    }


    int getAccumulatedOffsetSinceLastAstRefresh(int pos) {
        int accumulatedOffset = 0;
        for (Entry<Integer, Integer> entry : offsetAccumulator.entrySet()) {
            if (entry.getKey() >= pos) {
                break;
            }
            accumulatedOffset += entry.getValue();
        }
        return accumulatedOffset;
    }


    // TODO
    private Function<Node, Set<String>> extraClassesFinder(LayerId layerId) {
        return layerId == XPATH_RESULTS
                ? n -> useInlineHighlight(n) ? singleton("inline-highlight") : emptySet()
                : n -> emptySet();
    }


    /**
     * Returns whether the node should be styled inline or not.
     * Inline highlight basically adds a border around a node.
     * Borders are only used around short (text-wise) nodes,
     * since otherwise the background variation is enough to
     * spot the node. Also, borders can't stack, so having only
     * short nodes bear the border reduces the chance that
     * another node that should be bordered is included inside
     * the node, in which case it would be unnoticeable.
     *
     * <p>TODO This is a pretty dumb heuristic, it would be better
     * to have a way to know if a node is included in another
     * highlighted node and in that case display the border...
     *
     * @param node node to test
     */
    private boolean useInlineHighlight(Node node) {
        final int maxInlineLength = 10;
        return node.getBeginLine() == node.getEndLine() && (node.getEndColumn() - node.getBeginColumn()) <= maxInlineLength;
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
        clearStyleLayer(id.id);
    }


    private void clearStyleLayer(String id) {
        styleContextUpdateQueue.push(ContextUpdate.clearUpdate(id));
    }


    /**
     * Enables syntax highlighting if disabled and sets it to use the given highlighter.
     *
     * @param highlighter The highlighter to use (not null)
     */
    public void setSyntaxHighlighter(SyntaxHighlighter highlighter) {

        ObservableList<String> styleClasses = this.getStyleClass();

        syntaxHighlighter.ifPresent(previous -> styleClasses.remove("." + previous.getLanguageTerseName()));

        syntaxHighlighter.setValue(highlighter);

        if (syntaxHighlighter.isEmpty()) {
            disableSyntaxHighlighting();
        }

        styleClasses.add("." + highlighter.getLanguageTerseName());

        syntaxAutoRefresh = launchAsyncSyntaxHighlighting();

        try { // refresh the highlighting.
            Task<StyleSpans<Collection<String>>> t = computeHighlightingAsync(this.getText());
            t.setOnSucceeded(e -> styleContext.setSyntaxHighlight(t.getValue()));
        } catch (Exception ignored) {
            // nevermind
        }
    }


    public Val<Boolean> syntaxHighlightingEnabledProperty() {
        return syntaxHighlighter.map(Objects::nonNull);
    }



    /**
     * Disables syntax highlighting gracefully, if enabled.
     */
    public void disableSyntaxHighlighting() {
        if (syntaxHighlightingEnabledProperty().getValue()) {

            if (syntaxAutoRefresh != null) {
                syntaxAutoRefresh.unsubscribe();
            }

            if (executorService != null) {
                executorService.shutdown();
            }

            syntaxHighlighter.setValue(null);

            styleContextUpdateQueue.push(ContextUpdate.clearUpdate(SYNTAX_HIGHLIGHT_LAYER_ID));
        }
    }


    private synchronized Subscription launchAsyncSyntaxHighlighting() {

        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }

        if (syntaxHighlighter.isPresent()) {
            executorService = Executors.newSingleThreadExecutor();
            return this.richChanges()
                       .map(RichTextChange::toPlainTextChange)
                       .filter(ch -> !ch.isIdentity())
                       .distinct()
                       .successionEnds(UPDATE_DELAY)
                       .supplyTask(() -> computeHighlightingAsync(this.getText()))
                       .awaitLatest(this.richChanges())
                       .filterMap(t -> {
                           t.ifFailure(Throwable::printStackTrace);
                           return t.toOptional();
                       })
                       .hook(styleContext::setSyntaxHighlight)
                       .subscribe(spans -> paintCss());
        }
        return null;
    }


    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync(String text) {
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() {
                return syntaxHighlighter.getValue().computeHighlighting(text);
            }
        };
        if (!executorService.isShutdown()) {
            executorService.execute(task);
        }
        return task;
    }


    /** Public style layers of the code area. */
    public enum LayerId {
        /** For the currently selected node. */
        FOCUS("focus"),
        /** For nodes in error, declaration usages. */
        SECONDARY("secondary"),
        /** For xpath results. */
        XPATH_RESULTS("xpath");

        static final String SYNTAX_HIGHLIGHT_LAYER_ID = "syntax";


        String getId() {
            return id;
        }


        private final String id;

        LayerId(String id) {
            this.id = id;
        }
    }
}
