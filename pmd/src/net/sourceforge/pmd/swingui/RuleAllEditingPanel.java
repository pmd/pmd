package net.sourceforge.pmd.swingui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.border.EmptyBorder;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Donald A. Leckie
 * @since September 8, 2002
 * @version $Revision$, $Date$
 */
class RuleAllEditingPanel extends JPanel
{
    private RuleSetEditingPanel m_ruleSetPanel;
    private RuleEditingPanel m_rulePanel;
    private RulePropertyEditingPanel m_rulePropertyPanel;

    /**
     *******************************************************************************
     *
     * @return
     */
    public RuleAllEditingPanel()
    {
        super(new BorderLayout());

        EmptyBorder emptyBorder = new EmptyBorder(15, 15, 15, 15);

        setBorder(emptyBorder);

        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        m_ruleSetPanel = new RuleSetEditingPanel();
        m_rulePanel = new RuleEditingPanel();
        m_rulePropertyPanel = new RulePropertyEditingPanel();

        JScrollPane ruleSetScrollPane = ComponentFactory.createScrollPane(m_ruleSetPanel);
        JScrollPane ruleScrollPane = ComponentFactory.createScrollPane(m_rulePanel);
        JScrollPane rulePropertyScrollPane = ComponentFactory.createScrollPane(m_rulePropertyPanel);

        mainPanel.add(ruleSetScrollPane, BorderLayout.NORTH);
        mainPanel.add(ruleScrollPane, BorderLayout.CENTER);
        mainPanel.add(rulePropertyScrollPane, BorderLayout.SOUTH);
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected RuleSetEditingPanel getRuleSetEditingPanel()
    {
        return m_ruleSetPanel;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected RuleEditingPanel getRuleEditingPanel()
    {
        return m_rulePanel;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected RulePropertyEditingPanel getRulePropertyEditingPanel()
    {
        return m_rulePropertyPanel;
    }

    /**
     *******************************************************************************
     *
     */
    protected void saveData()
    {
        m_ruleSetPanel.saveData();
        m_rulePanel.saveData();
        m_rulePropertyPanel.saveData();
    }
}