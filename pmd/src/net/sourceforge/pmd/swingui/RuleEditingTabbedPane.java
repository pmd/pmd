package net.sourceforge.pmd.swingui;

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
public class RuleEditingTabbedPane extends JTabbedPane implements TreeSelectionListener
{

    private RuleAllEditingPanelUI m_ruleAllEditingPanel;
    private RuleSetEditingPanelUI m_ruleSetEditingPanel;
    private RuleEditingPanelUI m_ruleEditingPanel;
    private RulePropertyEditingPanelUI m_rulePropertyEditingPanel;

    /**
     *******************************************************************************
     *
     * @return
     */
    public RuleEditingTabbedPane(RulesTree rulesTree)
    {
        super(JTabbedPane.BOTTOM);

        m_ruleAllEditingPanel = new RuleAllEditingPanelUI();
        m_ruleSetEditingPanel = new RuleSetEditingPanelUI();
        m_ruleEditingPanel = new RuleEditingPanelUI();
        m_rulePropertyEditingPanel = new RulePropertyEditingPanelUI();

        addTab("All", m_ruleAllEditingPanel);
        addTab("Rule Set", m_ruleSetEditingPanel);
        addTab("Rule", m_ruleEditingPanel);
        addTab("Property", m_rulePropertyEditingPanel);
        setFont(UIManager.getFont("tabFont"));

        rulesTree.addTreeSelectionListener(m_ruleAllEditingPanel);
        rulesTree.addTreeSelectionListener(m_ruleSetEditingPanel);
        rulesTree.addTreeSelectionListener(m_ruleEditingPanel);
        rulesTree.addTreeSelectionListener(m_rulePropertyEditingPanel);
        rulesTree.addTreeSelectionListener(this);
    }

    /**
     *******************************************************************************
     *
     * @param event
     */
    public void valueChanged(TreeSelectionEvent event)
    {
        if (this.getSelectedComponent() != m_ruleAllEditingPanel)
        {
            TreePath treePath = event.getPath();
            Object component = treePath.getLastPathComponent();

            if (component instanceof RulesTreeNode)
            {
                RulesTreeNode treeNode = (RulesTreeNode) component;

                if (treeNode.isRuleSet())
                {
                    setSelectedComponent(m_ruleSetEditingPanel);
                }
                else if (treeNode.isRule())
                {
                    setSelectedComponent(m_ruleEditingPanel);
                }
                else if (treeNode.isProperty())
                {
                    setSelectedComponent(m_rulePropertyEditingPanel);
                }
            }
        }
    }
}