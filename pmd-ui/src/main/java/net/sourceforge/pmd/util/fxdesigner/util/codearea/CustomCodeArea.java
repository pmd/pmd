/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.fxmisc.richtext.CodeArea;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;

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
    private boolean isSyntaxHighlightingEnabled;
    private StyleContext styleContext;
    private SyntaxHighlighter highlightingComputer;


    public CustomCodeArea() {
        super();
        styleContext = new StyleContext(this);
        styleContext.addLayer(PRIMARY_HIGHLIGHT_LAYER_ID, new StyleLayer(PRIMARY_HIGHLIGHT_LAYER_ID, this));
    }


    public void styleCss(int beginLine, int beginColumn, int endLine, int endColumn, Set<String> cssClasses) {
        Set<String> fullClasses = new HashSet<>(cssClasses);
        fullClasses.add("text");
        fullClasses.add("styled-text-area");
        styleContext.getLayer(PRIMARY_HIGHLIGHT_LAYER_ID).style(beginLine, beginColumn, endLine, endColumn, fullClasses);
    }


    public void styleCss(Node node, Set<String> cssClasses) {
        this.styleCss(node.getBeginLine(), node.getBeginColumn(), node.getEndLine(), node.getEndColumn(), cssClasses);
    }


    public void positionCaret(int line, int column) {
        this.positionCaret(DesignerUtil.lengthUntil(line, column, this));
    }


    public void restylePrimaryStyleLayer(Node node, Set<String> cssClasses) {
        clearPrimaryStyleLayer();
        styleCss(node, cssClasses);
    }


    public void clearPrimaryStyleLayer() {
        styleContext.getLayer(PRIMARY_HIGHLIGHT_LAYER_ID).clearStyles();
    }


    public void clearStyleLayers() {
        for (StyleLayer layer : styleContext) {
            layer.clearStyles();
        }
    }


    public void setSyntaxHighlightingEnabled(SyntaxHighlighter computer) {
        this.setSyntaxHighlighting(computer);
        this.replaceText(0, 0, " ");
        this.undo();
    }


    public boolean isSyntaxHighlightingEnabled() {
        return isSyntaxHighlightingEnabled;
    }


    public void paintCss() {
        this.setStyleSpans(0, styleContext.getStyleSpans());
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
            StyleLayer syntaxHighlightLayer = styleContext.getLayer(SYNTAX_HIGHLIGHT_LAYER_ID);
            if (syntaxHighlightLayer != null) {
                syntaxHighlightLayer.clearStyles();
            }
        }
        paintCss();
    }


    /**
     * Enables syntax highlighting if disabled and sets it to use the given computer.
     *
     * @param computer The computer to use
     */
    public void setSyntaxHighlighting(SyntaxHighlighter computer) {
        isSyntaxHighlightingEnabled = true;
        Objects.requireNonNull(computer, "The syntax highlighting computer cannot be null");

        StyleLayer syntaxHighlightLayer = styleContext.getLayer(SYNTAX_HIGHLIGHT_LAYER_ID);
        if (syntaxHighlightLayer == null) {
            styleContext.addLayer(SYNTAX_HIGHLIGHT_LAYER_ID, new StyleLayer(SYNTAX_HIGHLIGHT_LAYER_ID, this));
        }

        setSyntaxHighlightingComputer(computer);
    }


    private void setSyntaxHighlightingComputer(SyntaxHighlighter computer) {
        this.highlightingComputer = computer;
        if (executorService != null) {
            executorService.shutdown();
        }
        if (isSyntaxHighlightingEnabled && highlightingComputer != null) {
            executorService = Executors.newSingleThreadExecutor();
            this.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .successionEnds(Duration.ofMillis(500))
                .supplyTask(() -> highlightingComputer.computeHighlightingAsync(this.getText(), executorService))
                .awaitLatest(this.richChanges())
                .filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(bounds -> {
                    StyleLayer layer = styleContext.getLayer(SYNTAX_HIGHLIGHT_LAYER_ID);
                    assert layer != null;
                    layer.setBounds(bounds);
                    this.paintCss();
                });
            this.getStylesheets().add(computer.getCssFileIdentifier());
        }
    }


}
