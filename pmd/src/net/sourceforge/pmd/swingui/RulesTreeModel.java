package net.sourceforge.pmd.swingui;

import java.util.Comparator;
import java.util.Enumeration;

import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.pmd.swingui.event.ListenerList;
import net.sourceforge.pmd.swingui.event.RulesTreeModelEvent;
import net.sourceforge.pmd.swingui.event.RulesTreeModelEventListener;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
class RulesTreeModel
    extends DefaultTreeModel
{

    private RulesTreeModelEventHandler m_rulesTreeModelEventHandler;

    /**
     ****************************************************************************
     *
     * @param rootNode
     */
    protected RulesTreeModel(RulesTreeNode rootNode)
    {
        super(rootNode);

        m_rulesTreeModelEventHandler = new RulesTreeModelEventHandler();
    }

    /**
     ***************************************************************************
     *
     * @param ruleSetName
     *
     * @return
     */
    protected RulesTreeNode getRuleSetNode(String ruleSetName)
    {
        if (ruleSetName != null)
        {
            RulesTreeNode rootNode = (RulesTreeNode) getRoot();
            Enumeration ruleSetNodes = rootNode.children();

            while (ruleSetNodes.hasMoreElements())
            {
                RulesTreeNode ruleSetNode = (RulesTreeNode) ruleSetNodes.nextElement();

                if (ruleSetNode.getName().equalsIgnoreCase(ruleSetName))
                {
                    return ruleSetNode;
                }
            }
        }

        return null;
    }

    /**
     ***************************************************************************
     *
     * @param ruleSetName
     * @param ruleName
     *
     * @return
     */
    protected RulesTreeNode getRuleNode(String ruleSetName, String ruleName)
    {
        if ((ruleSetName != null) && (ruleName != null))
        {
            RulesTreeNode rootNode = (RulesTreeNode) getRoot();
            Enumeration ruleSetNodes = rootNode.children();

            while (ruleSetNodes.hasMoreElements())
            {
                RulesTreeNode ruleSetNode = (RulesTreeNode) ruleSetNodes.nextElement();

                if (ruleSetNode.getName().equalsIgnoreCase(ruleSetName))
                {
                    Enumeration ruleNodes = ruleSetNode.children();

                    while (ruleNodes.hasMoreElements())
                    {
                        RulesTreeNode ruleNode = (RulesTreeNode) ruleNodes.nextElement();

                        if (ruleNode.getName().equalsIgnoreCase(ruleName))
                        {
                            return ruleNode;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     ****************************************************************************
     ****************************************************************************
     ****************************************************************************
     */
    private class RulesTreeModelEventHandler implements RulesTreeModelEventListener
    {

        /**
         ***********************************************************************
         *
         */
        private RulesTreeModelEventHandler()
        {
            ListenerList.addListener((RulesTreeModelEventListener) this);
        }

        /**
         ************************************************************************
         *
         * @param parentNode
         */
        public void reload(RulesTreeModelEvent event)
        {
            RulesTreeNode parentNode = event.getParentNode();
            RulesTreeModel.this.reload(parentNode);
        }
    }
}