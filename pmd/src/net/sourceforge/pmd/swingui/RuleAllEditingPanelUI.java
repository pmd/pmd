package net.sourceforge.pmd.swingui;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 *
 * @author Donald A. Leckie
 * @since September 8, 2002
 * @version $Revision$, $Date$
 */
class RuleAllEditingPanelUI extends RuleAllEditingPanel implements TreeSelectionListener
{

    /**
     *******************************************************************************
     *
     * @return
     */
    protected RuleAllEditingPanelUI()
    {
        super();
    }

    /**
     *******************************************************************************
     *
     * @param event
     */
    public void valueChanged(TreeSelectionEvent event)
    {
        TreePath treePath = event.getPath();
        Object object = treePath.getLastPathComponent();

        if (object instanceof IRulesEditingData)
        {
            getRuleSetEditingPanel().valueChanged((IRulesEditingData) object);
            getRuleEditingPanel().valueChanged((IRulesEditingData) object);
            getRulePropertyEditingPanel().valueChanged((IRulesEditingData) object);
        }
    }
}