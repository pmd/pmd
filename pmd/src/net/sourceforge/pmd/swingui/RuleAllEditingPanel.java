package net.sourceforge.pmd.swingui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;

/**
 *
 * @author Donald A. Leckie
 * @since September 8, 2002
 * @version $Revision$, $Date$
 */
public class RuleAllEditingPanel extends JPanel
{
    private RuleSetEditingPanel m_ruleSetPanel;
    private RuleEditingPanel m_rulePanel;
    private RulePropertyEditingPanel m_rulePropertyPanel;
    private boolean m_isEditing;

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
    public RuleSetEditingPanel getRuleSetEditingPanel()
    {
        return m_ruleSetPanel;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    public RuleEditingPanel getRuleEditingPanel()
    {
        return m_rulePanel;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    public RulePropertyEditingPanel getRulePropertyEditingPanel()
    {
        return m_rulePropertyPanel;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected boolean isEditing()
    {
        return m_isEditing;
    }

    /**
     *******************************************************************************
     *
     * @param isEditing
     */
    protected void setIsEditing(boolean isEditing)
    {
        m_isEditing = isEditing;
        m_ruleSetPanel.setIsEditing(isEditing);
        m_rulePanel.setIsEditing(isEditing);
        m_rulePropertyPanel.setIsEditing(isEditing);
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    public IRulesEditingData[] getData()
    {
        IRulesEditingData ruleSetData;
        IRulesEditingData ruleData;
        IRulesEditingData propertyData;
        IRulesEditingData[] data;

        ruleSetData = m_ruleSetPanel.getData();
        ruleData = m_rulePanel.getData();
        propertyData = m_rulePropertyPanel.getData();

        if (propertyData != null)
        {
            data = new IRulesEditingData[3];
            data[0] = ruleSetData;
            data[1] = ruleData;
            data[2] = propertyData;
        }
        else if (ruleData != null)
        {
            data = new IRulesEditingData[2];
            data[0] = ruleSetData;
            data[1] = ruleData;
        }
        else if (ruleSetData != null)
        {
            data = new IRulesEditingData[1];
            data[0] = ruleSetData;
        }
        else
        {
            data = new IRulesEditingData[0];
        }

        return data;
    }

    /**
     *******************************************************************************
     *
     * @param data
     */
    public void setData(IRulesEditingData data)
    {
        m_ruleSetPanel.setData(data);
        m_rulePanel.setData(data);
        m_rulePropertyPanel.setData(data);
    }

    /**
     *******************************************************************************
     *
     */
    public void saveData()
    {
        if (m_isEditing)
        {
            m_ruleSetPanel.saveData();
            m_rulePanel.saveData();
            m_rulePropertyPanel.saveData();
        }
    }
}