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

    private RuleSetEditingPanelGUI m_ruleSetEditingPanel;
    private RuleEditingPanelGUI m_ruleEditingPanel;
    private RulePropertyEditingPanelGUI m_rulePropertyEditingPanel;

    /**
     *******************************************************************************
     *
     * @return
     */
    public RuleEditingTabbedPane(RulesTree rulesTree)
    {
        super(JTabbedPane.BOTTOM);

        m_ruleSetEditingPanel = new RuleSetEditingPanelGUI();
        m_ruleEditingPanel = new RuleEditingPanelGUI();
        m_rulePropertyEditingPanel = new RulePropertyEditingPanelGUI();

        addTab("Rule Set", m_ruleSetEditingPanel);
        addTab("Rule", m_ruleEditingPanel);
        addTab("Property", m_rulePropertyEditingPanel);
        setFont(UIManager.getFont("tabFont"));

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