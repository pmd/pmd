package net.sourceforge.pmd.swingui;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import javax.swing.UIManager;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
public class RuleEditingTabbedPane extends JTabbedPane
{

    private RulesTree m_rulesTree;
    private RuleSetEditingPanel m_ruleSetPanel;
    private RuleEditingPanel m_rulePanel;
    private RulePropertyEditingPanel m_rulePropertyPanel;
    private RuleAllEditingPanel m_ruleAllPanel;
    private boolean m_changeListenerIsEnabled;
    private boolean m_selectionListenerIsEnabled;

    /**
     *******************************************************************************
     *
     * @return
     */
    public RuleEditingTabbedPane(RulesTree rulesTree)
    {
        super(JTabbedPane.BOTTOM);

        m_rulesTree = rulesTree;
        m_ruleAllPanel = new RuleAllEditingPanel();
        m_ruleSetPanel = new RuleSetEditingPanel();
        m_rulePanel = new RuleEditingPanel();
        m_rulePropertyPanel = new RulePropertyEditingPanel();
        m_changeListenerIsEnabled = true;
        m_selectionListenerIsEnabled = true;

        addTab("All", m_ruleAllPanel);
        addTab("Rule Set", m_ruleSetPanel);
        addTab("Rule", m_rulePanel);
        addTab("Property", m_rulePropertyPanel);
        setFont(UIManager.getFont("tabFont"));

        m_ruleAllPanel.setIsEditing(true);

        rulesTree.addTreeSelectionListener(new RulesTreeSelectionListener());
        addChangeListener(new RulesTreeChangeListener());
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected RulesTreeNode getSelectedTreeNode()
    {
        return m_rulesTree.getSelectedNode();
    }

    /**
     *******************************************************************************
     *
     */
    protected void saveData()
    {
        m_ruleAllPanel.saveData();
        m_ruleSetPanel.saveData();
        m_rulePanel.saveData();
        m_rulePropertyPanel.saveData();
    }

    /**
     *******************************************************************************
     *
     */
    protected void setData(RulesTreeNode treeNode)
    {
        m_ruleAllPanel.setData(treeNode);
        m_ruleSetPanel.setData(treeNode);
        m_rulePanel.setData(treeNode);
        m_rulePropertyPanel.setData(treeNode);
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    private IRulesEditingData[] getData()
    {
        IRulesEditingData[] data;

        if (m_ruleAllPanel.isEditing())
        {
            data = m_ruleAllPanel.getData();
        }
        else if (m_ruleSetPanel.isEditing())
        {
            data = new IRulesEditingData[1];
            data[0] = m_ruleSetPanel.getData();
        }
        else if (m_rulePanel.isEditing())
        {
            data = new IRulesEditingData[1];
            data[0] = m_rulePanel.getData();
        }
        else if (m_rulePropertyPanel.isEditing())
        {
            data = new IRulesEditingData[1];
            data[0] = m_rulePropertyPanel.getData();
        }
        else
        {
            data = new IRulesEditingData[0];
        }

        return data;
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class RulesTreeChangeListener implements ChangeListener
    {

        /**
         *******************************************************************************
         *
         * @param event
         */
        public void stateChanged(ChangeEvent event)
        {
            if (m_changeListenerIsEnabled)
            {
                saveData();

                Object selectedComponent = getSelectedComponent();
                m_ruleAllPanel.setIsEditing(selectedComponent == m_ruleAllPanel);
                m_ruleSetPanel.setIsEditing(selectedComponent == m_ruleSetPanel);
                m_rulePanel.setIsEditing(selectedComponent == m_rulePanel);
                m_rulePropertyPanel.setIsEditing(selectedComponent == m_rulePropertyPanel);
                setData(getSelectedTreeNode());
                SwingUtilities.invokeLater(new SortChildren());
            }
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class RulesTreeSelectionListener implements TreeSelectionListener
    {

        /**
         *******************************************************************************
         *
         * @param event
         */
        public void valueChanged(TreeSelectionEvent event)
        {
            if (m_selectionListenerIsEnabled)
            {
                TreePath treePath = event.getPath();
                Object component = treePath.getLastPathComponent();

                if (component instanceof RulesTreeNode)
                {
                    saveData();

                    RulesTreeNode treeNode = (RulesTreeNode) component;

                    if (getSelectedComponent() != m_ruleAllPanel)
                    {
                        if (treeNode.isRuleSet())
                        {
                            setSelectedComponent(m_ruleSetPanel);
                        }
                        else if (treeNode.isRule())
                        {
                            setSelectedComponent(m_rulePanel);
                        }
                        else if (treeNode.isProperty())
                        {
                            setSelectedComponent(m_rulePropertyPanel);
                        }
                    }

                    if (treeNode.getParent() == null)
                    {
                        // Get next to last tree node in tree path.
                        int pathIndex = treePath.getPathCount() - 2;

                        if (pathIndex >= 0)
                        {
                            treeNode = (RulesTreeNode) treePath.getPathComponent(pathIndex);
                        }
                    }

                    setData(treeNode);
                    SwingUtilities.invokeLater(new SortChildren());
                }
            }
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class SortChildren implements Runnable
    {

        /**
         ***************************************************************************
         *
         */
        public void run()
        {
            IRulesEditingData[] childData = RuleEditingTabbedPane.this.getData();

            if (childData != null)
            {
                TreePath selectedPath;

                selectedPath = m_rulesTree.getSelectionPath();
                m_changeListenerIsEnabled = false;
                m_selectionListenerIsEnabled = false;
                m_rulesTree.removeSelectionPath(selectedPath);

                for (int n = 0; n < childData.length; n++)
                {
                    RulesTreeNode childNode = (RulesTreeNode) childData[n];

                    if (childNode != null)
                    {
                        m_rulesTree.sortChildren((RulesTreeNode) childNode.getParent());
                    }
                }

                m_rulesTree.setSelectionPath(selectedPath);
                m_changeListenerIsEnabled = true;
                m_selectionListenerIsEnabled = true;
            }
        }
    }
}