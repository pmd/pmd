/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.HashSet;
import java.util.Set;

import org.fxmisc.richtext.CodeArea;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;

/**
 * Code area that can handle syntax highlighting as well as regular node highlighting. Regular node highlighting is
 * handled in the "primary" {@link StyleLayer}, which you can affect with {@link #styleCss(Node, Set)}, {@link
 * #clearPrimaryStyleLayer()} and the like. Syntax highlighting uses another internal style layer.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class CustomCodeArea extends CodeArea {

    private static final String PRIMARY_HIGHLIGHT_LAYER_ID = "primary";
    private StyleContext styleContext;


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
        styleContext.setSyntaxHighlighting(computer);
        this.replaceText(0, 0, " ");
        this.undo();
    }


    public boolean isSyntaxHighlightingEnabled() {
        return styleContext.isSyntaxHighlightingEnabled();
    }


    public void disableSyntaxHighlighting() {
        styleContext.disableSyntaxHighlighting();
        paintCss();
    }


    public void paintCss() {
        this.setStyleSpans(0, styleContext.getStyleSpans());
    }


}
