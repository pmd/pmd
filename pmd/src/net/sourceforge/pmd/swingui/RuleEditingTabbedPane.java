package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.swingui.event.RulesEditingEvent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
class RuleEditingTabbedPane extends JTabbedPane {

    private RulesTree m_rulesTree;
    private RuleAllEditingPanel m_ruleAllPanel;
    private RuleSetEditingPanel m_ruleSetPanel;
    private RuleEditingPanel m_rulePanel;
    private RulePropertyEditingPanel m_rulePropertyPanel;
    private RulesTreeNode m_currentTreeNode;

    /**
     *******************************************************************************
     *
     * @return
     */
    protected RuleEditingTabbedPane(RulesTree rulesTree) {
        super(JTabbedPane.TOP);

        m_rulesTree = rulesTree;
        m_ruleAllPanel = new RuleAllEditingPanel();
        m_ruleSetPanel = new RuleSetEditingPanel();
        m_rulePanel = new RuleEditingPanel();
        m_rulePropertyPanel = new RulePropertyEditingPanel();

        addTab("All", m_ruleAllPanel);
        addTab("Rule Set", m_ruleSetPanel);
        addTab("Rule", m_rulePanel);
        addTab("Property", m_rulePropertyPanel);
        setFont(UIManager.getFont("tabFont"));

        m_ruleAllPanel.setIsEditing(true);

        rulesTree.addTreeSelectionListener(new RulesTreeSelectionListener());
        addChangeListener(new TabChangeListener());
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected RulesTreeNode getSelectedTreeNode() {
        return m_rulesTree.getSelectedNode();
    }

    /**
     *******************************************************************************
     *
     */
    protected void saveData() {
        if (m_currentTreeNode != null) {
            RulesEditingEvent.notifySaveData(this, m_currentTreeNode);
        }
    }

    /**
     *******************************************************************************
     *
     */
    private void loadData(RulesTreeNode treeNode) {
        JPanel editingTab = (JPanel) getSelectedComponent();

        if (editingTab == m_ruleAllPanel) {
            m_ruleAllPanel.setIsEditing(true);
            m_rulePanel.setIsEditing(false);
            m_rulePropertyPanel.setIsEditing(false);
        } else if (editingTab == m_rulePanel) {
            m_ruleAllPanel.setIsEditing(false);
            m_rulePanel.setIsEditing(true);
            m_rulePropertyPanel.setIsEditing(false);
        } else {
            m_ruleAllPanel.setIsEditing(false);
            m_rulePanel.setIsEditing(false);
            m_rulePropertyPanel.setIsEditing(true);
        }

        m_currentTreeNode = treeNode;
        RulesEditingEvent.notifyLoadData(this, treeNode);
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class TabChangeListener implements ChangeListener {

        /**
         *******************************************************************************
         *
         * @param event
         */
        public void stateChanged(ChangeEvent event) {
            RulesTreeNode selectedTreeNode = getSelectedTreeNode();
            saveData();
            loadData(selectedTreeNode);
            SwingUtilities.invokeLater(new SortChildren(selectedTreeNode));
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class RulesTreeSelectionListener implements TreeSelectionListener {

        /**
         *******************************************************************************
         *
         * @param event
         */
        public void valueChanged(TreeSelectionEvent event) {
            TreePath treePath = event.getPath();
            Object component = treePath.getLastPathComponent();

            if (component instanceof RulesTreeNode) {
                saveData();

                RulesTreeNode treeNode = (RulesTreeNode) component;

                if (getSelectedComponent() != m_ruleAllPanel) {
                    if (treeNode.isRuleSet()) {
                        setSelectedComponent(m_ruleSetPanel);
                    } else if (treeNode.isRule()) {
                        setSelectedComponent(m_rulePanel);
                    } else if (treeNode.isProperty()) {
                        setSelectedComponent(m_rulePropertyPanel);
                    }
                }

                if (treeNode.getParent() == null) {
                    // Get next to last tree node in tree path.
                    int pathIndex = treePath.getPathCount() - 2;

                    if (pathIndex >= 0) {
                        treeNode = (RulesTreeNode) treePath.getPathComponent(pathIndex);
                    }
                }

                loadData(treeNode);
                SwingUtilities.invokeLater(new SortChildren(treeNode));
            }
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class SortChildren implements Runnable {

        private RulesTreeNode m_parentNode;

        /**
         ***************************************************************************
         *
         * @param parentNode
         */
        private SortChildren(RulesTreeNode parentNode) {
            m_parentNode = parentNode;
        }

        /**
         ***************************************************************************
         *
         */
        public void run() {
            if (m_parentNode != null) {
                m_parentNode.sortChildren();
            }
        }
    }
}