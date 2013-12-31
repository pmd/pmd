package net.sourceforge.pmd.jdeveloper;

import oracle.ide.Context;
import oracle.ide.controller.ContextMenu;
import oracle.ide.controller.ContextMenuListener;
import oracle.ide.controller.IdeAction;


/**
 *
 * NOTE: This listener is no longer used. The context menu is now added
 * declaratively, via the 'extension.xml' file. The 'menuWillShow' method
 * used to contain logic that the extension would use to decided whether
 * a menu should be added or not depending on the context. These are now
 * replaced by 'rule' which are declared in the extension.xml and evaluated
 * at run-time to decide whether or not this menu should be added.
 *
 * Please see the extension.xml file to see how a context menu is now added.
 *
 */
public final class PmdContextMenuListener implements ContextMenuListener {
    // First, retrieve our pmdAction using the ID we specified in the extension
    // manifest.
    // TODO CPD
    transient IdeAction pmdAction = IdeAction.find(PmdController.RUN_PMD_CMD_ID);

    public void menuWillShow(final ContextMenu contextMenu) {

        pmdAction.updateAction(contextMenu.getContext());
        if (pmdAction.isEnabled()) {
            contextMenu.add(contextMenu.createMenuItem(pmdAction));
            // TODO CPD
            // contextMenu.add(cpdMenuItem);
        }
    }

    public void menuWillHide(final ContextMenu contextMenu) {
        // Most context menu listeners will do nothing in this method. In
        // particular, you should *not* remove menu items in this method.
            // Nothing to do
    }

    public boolean handleDefaultAction(final Context context) {
        // You can implement this method if you want to handle the default
        // action (usually double click) for some context.
        return false;
    }
}
