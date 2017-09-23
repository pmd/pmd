/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Set;

import org.fxmisc.richtext.CodeArea;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Code area that can handle syntax highlighting as well as regular node highlighting.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class CustomCodeArea extends CodeArea {

    private static final String PRIMARY_HIGHLIGHT_LAYER_ID = "primary";
    private static final String SYNTAX_HIGHLIGHT_LAYER_ID = "syntax";
    private StyleContext styleContext = new StyleContext(this);


    public CustomCodeArea() {
        super();
        styleContext.addLayer(PRIMARY_HIGHLIGHT_LAYER_ID, new StyleLayer(PRIMARY_HIGHLIGHT_LAYER_ID, this));
    }


    public void styleCss(int beginLine, int beginColumn, int endLine, int endColumn, Set<String> cssClasses) {
        styleContext.getLayer(PRIMARY_HIGHLIGHT_LAYER_ID).style(beginLine, beginColumn, endLine, endColumn, cssClasses);
    }


    public void styleCss(Node node, Set<String> cssClasses) {
        styleContext.getLayer(PRIMARY_HIGHLIGHT_LAYER_ID).style(node.getBeginLine(), node.getBeginColumn(), node.getEndLine(), node.getEndColumn(), cssClasses);
    }


    public void clearStyleLayers() {
        for (StyleLayer layer : styleContext) {
            layer.clearStyles();
        }
    }


    public void setSyntaxHighlightingEnabled(boolean isEnabled) {
        StyleLayer syntaxHighlightLayer = styleContext.getLayer(SYNTAX_HIGHLIGHT_LAYER_ID);
        if (isEnabled && syntaxHighlightLayer == null) {
            styleContext.addLayer(SYNTAX_HIGHLIGHT_LAYER_ID, new StyleLayer(SYNTAX_HIGHLIGHT_LAYER_ID, this));
        } else if (!isEnabled && syntaxHighlightLayer != null) {
            styleContext.dropLayer(SYNTAX_HIGHLIGHT_LAYER_ID);
        }
    }


    public void paintCss() {
        this.setStyleSpans(0, styleContext.getStyleSpans());
    }


}
