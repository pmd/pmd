/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import static net.sourceforge.pmd.util.fxdesigner.util.codearea.CustomCodeArea.LayerId.FOCUS;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.CustomCodeArea.LayerId.SECONDARY;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.CustomCodeArea.LayerId.SYNTAX_HIGHLIGHT_LAYER_ID;
import static net.sourceforge.pmd.util.fxdesigner.util.codearea.CustomCodeArea.LayerId.XPATH_RESULTS;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;

import net.sourceforge.pmd.lang.ast.Node;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;


/**
 * Code area that can handle syntax highlighting as well as regular node highlighting. Regular node highlighting is
 * handled in several {@link StyleLayer}s, which you can affect with {@link #styleCss(Node, LayerId, String...)},
 * {@link #clearStyleLayer(LayerId)} and the like. Highlighting
 *
 * <p>Syntax highlighting uses another internal style layer. Syntax highlighting
 * is performed asynchronously by another thread. You must shut down the executor
 * gracefully by calling {@link #disableSyntaxHighlighting()} before exiting the application.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class CustomCodeArea extends CodeArea {

    private ExecutorService executorService;
    private Subscription syntaxAutoRefresh;
    private BooleanProperty isSyntaxHighlightingEnabled = new SimpleBooleanProperty(false);
    private StyleContext styleContext;
    private SyntaxHighlighter syntaxHighlighter;


    public CustomCodeArea() {
        super();
        styleContext = new StyleContext(this);
        styleContext.addLayer(SYNTAX_HIGHLIGHT_LAYER_ID, new StyleLayer(SYNTAX_HIGHLIGHT_LAYER_ID, this));
        styleContext.addLayer(XPATH_RESULTS.id, new StyleLayer(XPATH_RESULTS.id, this));
        styleContext.addLayer(FOCUS.id, new StyleLayer(FOCUS.id, this));
        styleContext.addLayer(SECONDARY.id, new StyleLayer(SECONDARY.id, this));
    }

    // FIXME the highlighting offsets to the left (right) when characters are deleted (inserted)
    // Probably, auto-parsing of the source will fix that to some extent.

    /**
     * Styles the node in the given layer.
     *
     * <p>The focus layer is meant for the node in primary focus, in contrast
     * with the secondary layer, which highlights some nodes that are related
     * to a specific selection (eg error nodes correspond to an error, name
     * occurrences correspond to a name declaration). The XPath result layer
     * highlights nodes that are independent from any selection (they depend
     * on the xpath results).
     *
     * @param node       node to style
     * @param layerId    Layer id
     * @param cssClasses css classes to apply
     */
    // TODO we can probably be more efficient if we style batches of n nodes, and create only one span for them instead of overlaying n spans
    public void styleCss(Node node, LayerId layerId, String... cssClasses) {
        Set<String> fullClasses = new HashSet<>(Arrays.asList(cssClasses));
        fullClasses.add("text");
        fullClasses.add("styled-text-area");
        fullClasses.add(layerId.id + "-highlight"); // focus-highlight, xpath-highlight, secondary-highlight

        if (layerId == XPATH_RESULTS && useInlineHighlight(node)) {
            fullClasses.add("inline-highlight");
        }

        styleContext.getLayer(layerId.id).style(node.getBeginLine(), node.getBeginColumn(), node.getEndLine(), node.getEndColumn(), fullClasses);
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
     * <p>This is a pretty dumb heuristic, it would be better
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
     * Clears a style layer.
     *
     * @param id layer id.
     */
    public void clearStyleLayer(LayerId id) {
        styleContext.getLayer(id.id).clearStyles();
    }


    /**
     * Returns true if the node is in the range of the current text.
     *
     * @param n The node to check
     *
     * @return True or false
     */
    public boolean isInRange(Node n) {
        return n.getEndLine() <= getParagraphs().size()
                && (n.getEndLine() != getParagraphs().size()
                || n.getEndColumn() <= getParagraph(n.getEndLine() - 1).length());
    }



    /**
     * Clears all style layers from their contents.
     */
    public void clearStyleLayers() {
        for (StyleLayer layer : styleContext) {
            layer.clearStyles();
        }
    }


    public boolean isSyntaxHighlightingEnabled() {
        return isSyntaxHighlightingEnabled.get();
    }


    /**
     * Enables syntax highlighting if disabled and sets it to use the given highlighter.
     *
     * @param highlighter The highlighter to use (not null)
     */
    public void setSyntaxHighlightingEnabled(SyntaxHighlighter highlighter) {
        this.setSyntaxHighlighter(highlighter);

        try { // refresh the highlighting.
            Task<StyleSpans<Collection<String>>> t = computeHighlightingAsync(this.getText());
            t.setOnSucceeded(e -> {
                StyleLayer layer = styleContext.getLayer(SYNTAX_HIGHLIGHT_LAYER_ID);
                layer.reset(t.getValue());
                this.paintCss();
            });
        } catch (Exception ignored) {
            // nevermind
        }
    }


    public BooleanProperty syntaxHighlightingEnabledProperty() {
        return isSyntaxHighlightingEnabled;
    }


    /**
     * Forcefully applies the possibly updated css classes.
     * This operation is expensive, and should not be executed
     * in a loop for example.
     */
    public void paintCss() {
        this.setStyleSpans(0, styleContext.getStyleSpans());
    }


    /**
     * Disables syntax highlighting gracefully, if enabled.
     */
    public void disableSyntaxHighlighting() {
        if (isSyntaxHighlightingEnabled.get()) {
            isSyntaxHighlightingEnabled.set(false);

            if (syntaxAutoRefresh != null) {
                syntaxAutoRefresh.unsubscribe();
            }

            if (executorService != null) {
                executorService.shutdown();
            }
            StyleLayer syntaxHighlightLayer = styleContext.getLayer(SYNTAX_HIGHLIGHT_LAYER_ID);
            if (syntaxHighlightLayer != null) {
                syntaxHighlightLayer.clearStyles();
            }
        }
        paintCss();
    }


    private void setSyntaxHighlighter(SyntaxHighlighter newHighlighter) {
        isSyntaxHighlightingEnabled.set(true);
        Objects.requireNonNull(newHighlighter, "The syntax highlighting highlighter cannot be null");

        ObservableList<String> styleClasses = this.getStyleClass();
        if (syntaxHighlighter != null) {
            styleClasses.remove("." + syntaxHighlighter.getLanguageTerseName());
        }
        styleClasses.add("." + newHighlighter.getLanguageTerseName());

        launchAsyncSyntaxHighlighting(newHighlighter);
    }


    private synchronized void launchAsyncSyntaxHighlighting(SyntaxHighlighter computer) {

        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }

        this.syntaxHighlighter = computer;

        if (isSyntaxHighlightingEnabled.get() && syntaxHighlighter != null) {
            executorService = Executors.newSingleThreadExecutor();
            //            syntaxAutoRefresh = this.richChanges().filter(ch -> !ch.isIdentity())
            syntaxAutoRefresh = EventStreams.valuesOf(textProperty()).distinct()
                                            .successionEnds(Duration.ofMillis(100))
                                            .supplyTask(() -> computeHighlightingAsync(this.getText()))
                                            .awaitLatest(this.richChanges().filter(ch -> !ch.isIdentity()))
                                            .filterMap(t -> {
                                                t.ifFailure(Throwable::printStackTrace);
                                                return t.toOptional();
                                            })
                                            .subscribe(spans -> {
                                                StyleLayer layer = styleContext.getLayer(SYNTAX_HIGHLIGHT_LAYER_ID);
                                                layer.reset(spans);
                                                this.paintCss();
                                            });
        }
    }


    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync(String text) {
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() {
                return syntaxHighlighter.computeHighlighting(text);
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

        private final String id;

        LayerId(String id) {
            this.id = id;
        }
    }
}
