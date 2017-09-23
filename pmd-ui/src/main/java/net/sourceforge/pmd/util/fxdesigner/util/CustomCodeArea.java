/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util;

import java.util.Set;

import org.fxmisc.richtext.CodeArea;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class CustomCodeArea extends CodeArea {

    private CssTextStyler styler = new CssTextStyler(this);


    public void styleCss(int beginLine, int beginColumn, int endLine, int endColumn, Set<String> cssClasses) {
        styler.style(beginLine, beginColumn, endLine, endColumn, cssClasses);
    }


    public void styleCss(Node node, Set<String> cssClasses) {
        styler.style(node.getBeginLine(), node.getBeginColumn(), node.getEndLine(), node.getEndColumn(), cssClasses);
    }


    public void clearStyles() {
        styler.clearStyles();
    }


    public void paintCss() {
        this.setStyleSpans(0, styler.getStyleSpans());
    }


}
