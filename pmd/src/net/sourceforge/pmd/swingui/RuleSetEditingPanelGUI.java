package net.sourceforge.pmd.swingui;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
class RuleSetEditingPanelGUI extends RuleSetEditingPanel implements TreeSelectionListener
{


    /**
     *******************************************************************************
     *
     * @return
     */
    protected RuleSetEditingPanelGUI()
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
            valueChanged((IRulesEditingData) object);
        }
    }

}