package net.sourceforge.pmd.swingui;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.JTabbedPane;
import javax.swing.tree.TreePath;
import javax.swing.UIManager;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
public class RuleEditingTabbedPane extends JTabbedPane implements TreeSelectionListener, ChangeListener
{

    private RulesTree m_rulesTree;
    private RuleSetEditingPanel m_ruleSetPanel;
    private RuleEditingPanel m_rulePanel;
    private RulePropertyEditingPanel m_rulePropertyPanel;
    private RuleAllEditingPanel m_ruleAllPanel;

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

        addTab("All", m_ruleAllPanel);
        addTab("Rule Set", m_ruleSetPanel);
        addTab("Rule", m_rulePanel);
        addTab("Property", m_rulePropertyPanel);
        setFont(UIManager.getFont("tabFont"));

        m_ruleAllPanel.setIsEditing(true);
        rulesTree.addTreeSelectionListener(this);
        addChangeListener(this);
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
     * @param event
     */
    public void stateChanged(ChangeEvent event)
    {
        saveData();
        Object selectedComponent = getSelectedComponent();
        m_ruleAllPanel.setIsEditing(selectedComponent == m_ruleAllPanel);
        m_ruleSetPanel.setIsEditing(selectedComponent == m_ruleSetPanel);
        m_rulePanel.setIsEditing(selectedComponent == m_rulePanel);
        m_rulePropertyPanel.setIsEditing(selectedComponent == m_rulePropertyPanel);
        setData(getSelectedTreeNode());
    }

    /**
     *******************************************************************************
     *
     * @param event
     */
    public void valueChanged(TreeSelectionEvent event)
    {
        TreePath treePath = event.getPath();
        Object component = treePath.getLastPathComponent();

        if (component instanceof RulesTreeNode)
        {
            RulesTreeNode treeNode = (RulesTreeNode) component;

            saveData();

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

            setData(treeNode);
        }
    }
}