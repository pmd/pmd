/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.StyleSpans;
import org.reactfx.Subscription;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 * Code area that can handle syntax highlighting as well as regular node highlighting. Regular node highlighting is
 * handled in the "primary" {@link StyleLayer}, which you can affect with {@link #styleCss(Node, Set)}, {@link
 * #clearPrimaryStyleLayer()} and the like. Syntax highlighting uses another internal style layer. Syntax highlighting
 * is performed asynchronously by another thread. You must shut down the executor gracefully by calling {@link
 * #disableSyntaxHighlighting()} before exiting the application.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class CustomCodeArea extends CodeArea {

    private static final String SYNTAX_HIGHLIGHT_LAYER_ID = "syntax";
    private static final String PRIMARY_HIGHLIGHT_LAYER_ID = "primary";
    private ExecutorService executorService;
    private Subscription syntaxAutoRefresh;
    private BooleanProperty isSyntaxHighlightingEnabled = new SimpleBooleanProperty(false);
    private StyleContext styleContext;
    private SyntaxHighlighter syntaxHighlighter;


    public CustomCodeArea() {
        super();
        styleContext = new StyleContext(this);
        styleContext.addLayer(PRIMARY_HIGHLIGHT_LAYER_ID, new StyleLayer(PRIMARY_HIGHLIGHT_LAYER_ID, this));
    }


    /**
     * Styles the region delimited by the coordinates with the given css classes. Should be followed by a call to {@link
     * #paintCss()} to update the visual appearance.
     *
     * @param beginLine   Begin line
     * @param beginColumn Begin column
     * @param endLine     End line
     * @param endColumn   End column
     * @param cssClasses  The css classes to apply
     *
     * @throws IllegalArgumentException if the region identified by the coordinates is out of bounds
     */
    public void styleCss(int beginLine, int beginColumn, int endLine, int endColumn, Set<String> cssClasses) {
        Set<String> fullClasses = new HashSet<>(cssClasses);
        fullClasses.add("text");
        fullClasses.add("styled-text-area");
        styleContext.getLayer(PRIMARY_HIGHLIGHT_LAYER_ID).style(beginLine, beginColumn, endLine, endColumn, fullClasses);
    }


    /**
     * Styles the node's position with the given css classes.
     *
     * @param node       The node to style
     * @param cssClasses The css classes to apply
     *
     * @throws IllegalArgumentException if the node's coordinates are out of bounds
     */
    public void styleCss(Node node, Set<String> cssClasses) {
        this.styleCss(node.getBeginLine(), node.getBeginColumn(), node.getEndLine(), node.getEndColumn(), cssClasses);
    }


    /**
     * Positions the caret at the specified coordinates.
     *
     * @param line   Line
     * @param column Column
     */
    public void positionCaret(int line, int column) {
        this.positionCaret(DesignerUtil.lengthUntil(line, column, this));
    }


    /**
     * Replaces the styling of the primary layer by styling the node's position with the given css classes.
     *
     * @param node       The node to style
     * @param cssClasses The css classes to apply
     *
     * @throws IllegalArgumentException if the node's coordinates are out of bounds
     */
    public void restylePrimaryStyleLayer(Node node, Set<String> cssClasses) {
        clearPrimaryStyleLayer();
        styleCss(node, cssClasses);
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
            || n.getEndColumn() <= getParagraph(n.getEndLine() - 2).length());
    }


    /**
     * Clears the primary style layer from its contents.
     */
    public void clearPrimaryStyleLayer() {
        styleContext.getLayer(PRIMARY_HIGHLIGHT_LAYER_ID).clearStyles();
    }


    /**
     * Clears all style layers from their contents.
     */
    public void clearStyleLayers() {
        for (StyleLayer layer : styleContext) {
            layer.clearStyles();
        }
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
            t.setOnSucceeded((e) -> {
                StyleLayer layer = styleContext.getLayer(SYNTAX_HIGHLIGHT_LAYER_ID);
                layer.reset(t.getValue());
                this.paintCss();
            });
        } catch (Exception e) {
            // nevermind
        }
    }


    public boolean isSyntaxHighlightingEnabled() {
        return isSyntaxHighlightingEnabled.get();
    }


    public BooleanProperty syntaxHighlightingEnabledProperty() {
        return isSyntaxHighlightingEnabled;
    }


    /**
     * Forcefully applies the possibly updated css classes.
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

        StyleLayer syntaxHighlightLayer = styleContext.getLayer(SYNTAX_HIGHLIGHT_LAYER_ID);
        if (syntaxHighlightLayer == null) {
            styleContext.addLayer(SYNTAX_HIGHLIGHT_LAYER_ID, new StyleLayer(SYNTAX_HIGHLIGHT_LAYER_ID, this));
        }


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
            syntaxAutoRefresh = this.richChanges()
                                    .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                                    .successionEnds(Duration.ofMillis(100))
                                    .supplyTask(() -> computeHighlightingAsync(this.getText()))
                                    .awaitLatest(this.richChanges())
                                    .filterMap(t -> {
                                        if (t.isSuccess()) {
                                            return Optional.of(t.get());
                                        } else {
                                            t.getFailure().printStackTrace();
                                            return Optional.empty();
                                        }
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
            protected StyleSpans<Collection<String>> call() throws Exception {
                return syntaxHighlighter.computeHighlighting(text);
            }
        };
        if (!executorService.isShutdown()) {
            executorService.execute(task);
        }
        return task;
    }


}
