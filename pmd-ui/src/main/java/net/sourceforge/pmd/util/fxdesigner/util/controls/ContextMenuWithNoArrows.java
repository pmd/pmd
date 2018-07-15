/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.controls;

import javafx.scene.control.ContextMenu;


/**
 * Context menu which has no scroll arrows (which by default appear on the top and bottom element).
 * Implemented with simple CSS.
 *
 * @author Clément Fournier
 * @since 6.6.0
 */
public class ContextMenuWithNoArrows extends ContextMenu {

    public ContextMenuWithNoArrows() {
        getStyleClass().add("no-scroll-arrows"); // sync with designer.less
    }
}
