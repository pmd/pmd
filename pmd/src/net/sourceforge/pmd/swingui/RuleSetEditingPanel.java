package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.swingui.event.ListenerList;
import net.sourceforge.pmd.swingui.event.RulesEditingEvent;
import net.sourceforge.pmd.swingui.event.RulesEditingEventListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.MessageFormat;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
class RuleSetEditingPanel extends JPanel
{
    private JLabel m_nameLabel;
    private JTextField m_name;
    private JLabel m_descriptionLabel;
    private JTextArea m_description;
    private JScrollPane m_descriptionScrollPane;
    private boolean m_enabled;
    private RulesTreeNode m_currentDataNode;
    private boolean m_isEditing;
    private String m_originalName;
    private FocusListener m_focusListener = new RuleSetNameFocusListener();

    /**
     *******************************************************************************
     *
     */
    protected RuleSetEditingPanel()
    {
        super(new BorderLayout());

        EmptyBorder emptyBorder = new EmptyBorder(5, 5, 5, 5);

        setBorder(emptyBorder);

        GridBagLayout layout;
        GridBagConstraints constraints;
        JPanel panel;
        TitledBorder titledBorder;

        int[] columnWidths = {25, 100, 100, 100, 100, 100};
        layout = new GridBagLayout();
        layout.columnWidths = columnWidths;
        panel = new JPanel(layout);
        titledBorder = ComponentFactory.createTitledBorder("  Rule Set  ");

        panel.setBorder(titledBorder);
        add(panel, BorderLayout.CENTER);

        // Rule Set Name Label
        m_nameLabel = new JLabel("Name");
        m_nameLabel.setFont(UIManager.getFont("labelFont"));
        m_nameLabel.setHorizontalAlignment(JLabel.RIGHT);
        constraints = layout.getConstraints(m_nameLabel);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_nameLabel, constraints);

        // Rule Set Name Text
        m_name = new JTextField();
        m_name.setFont(UIManager.getFont("dataFont"));
        m_name.addFocusListener(m_focusListener);
        m_name.setRequestFocusEnabled(true);
        constraints = layout.getConstraints(m_name);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_name, constraints);

        // Rule Set Description Label
        m_descriptionLabel = new JLabel("Description");
        m_descriptionLabel.setFont(UIManager.getFont("labelFont"));
        m_descriptionLabel.setHorizontalAlignment(JLabel.RIGHT);
        constraints = layout.getConstraints(m_nameLabel);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_descriptionLabel, constraints);

        // Rule Set Description Text
        m_description = ComponentFactory.createTextArea("");

        // Rule Set Description Scroll Pane;
        m_descriptionScrollPane = ComponentFactory.createScrollPane(m_description);
        constraints = layout.getConstraints(m_name);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(4, 2, 4, 2);
        constraints.ipady = 4 * 20;  // 4 lines * 20 pixels/line
        panel.add(m_descriptionScrollPane, constraints);

        enableData(false);

        ListenerList.addListener(new RulesEditingEventHandler());
    }

    /**
     *******************************************************************************
     *
     * @param dataNode
     */
    private void saveData(RulesTreeNode dataNode)
    {
        if ((dataNode != null) && m_isEditing)
        {
            if (dataNode.isRuleSet() || dataNode.isRule() || dataNode.isProperty())
            {
                String ruleSetName = m_name.getText();

                if (ruleSetName.length() == 0)
                {
                    String message = "The rule set name is missing.  The change will not be applied.";
                    boolean hasFocus = m_name.hasFocus();

                    m_name.removeFocusListener(m_focusListener);
                    MessageDialog.show(getParentWindow(), message);
                    m_name.addFocusListener(m_focusListener);

                    if (hasFocus)
                    {
                        m_name.requestFocus();
                    }

                    ruleSetName = m_originalName;
                }
                else if (ruleSetName.equalsIgnoreCase(m_originalName) == false)
                {
                    if (dataNode.getSibling(ruleSetName) != null)
                    {
                        String template = "Another rule set already has the name \"{0}\".  The change will not be applied.";
                        String[] args = {ruleSetName};
                        String message = MessageFormat.format(template, args);
                        boolean hasFocus = m_name.hasFocus();

                        m_name.removeFocusListener(m_focusListener);
                        MessageDialog.show(getParentWindow(), message);
                        m_name.addFocusListener(m_focusListener);

                        if (hasFocus)
                        {
                            m_name.requestFocus();
                        }

                        ruleSetName = m_originalName;
                    }
                }

                dataNode.setName(ruleSetName);
                dataNode.setDescription(m_description.getText());
            }
        }
    }

    /**
     *******************************************************************************
     *
     * @param isEditing
     */
    protected void setIsEditing(boolean isEditing)
    {
        m_isEditing = isEditing;
    }

    /**
     *******************************************************************************
     *
     * @param dataNode
     */
    private void loadData(RulesTreeNode dataNode)
    {
        if (dataNode == null)
        {
            enableData(false);
        }
        else if (dataNode.isRuleSet())
        {
            loadData_(dataNode);
        }
        else if (dataNode.isRule())
        {
            loadData_(dataNode.getParentRuleSetData());
        }
        else if (dataNode.isProperty())
        {
            loadData_(dataNode.getParentRuleSetData());
        }
        else
        {
            enableData(false);
        }
    }

    /**
     *******************************************************************************
     *
     * @param dataNode
     */
    private void loadData_(RulesTreeNode dataNode)
    {
        if (m_enabled == false)
        {
            enableData(true);
        }

        m_name.setText(dataNode.getName());
        m_description.setText(dataNode.getDescription());
        m_originalName = dataNode.getName();
        m_currentDataNode = dataNode;
    }

    /**
     *******************************************************************************
     *
     */
    private void enableData(boolean enable)
    {
        if (enable)
        {
            // Just to be sure the focus listener isn't set.
            m_name.removeFocusListener(m_focusListener);
            m_name.addFocusListener(m_focusListener);

            m_nameLabel.setEnabled(true);

            m_name.setEnabled(true);
            m_name.setBackground(Color.white);

            m_descriptionLabel.setEnabled(true);

            m_description.setEnabled(true);
            m_description.setBackground(Color.white);
        }
        else
        {
            m_name.removeFocusListener(m_focusListener);

            Color background = UIManager.getColor("disabledTextBackground");

            m_nameLabel.setEnabled(false);

            m_name.setText("");
            m_name.setEnabled(false);
            m_name.setBackground(background);

            m_descriptionLabel.setEnabled(false);

            m_description.setText("");
            m_description.setEnabled(false);
            m_description.setBackground(background);

            m_currentDataNode = null;
        }

        m_enabled = enable;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    private Window getParentWindow()
    {
        Component component = getParent();

        while ((component != null) && ((component instanceof Window) == false))
        {
            component = component.getParent();
        }

        return (Window) component;
    }

    /**
     ************************************************************************************
     ************************************************************************************
     ************************************************************************************
     */
     private class RuleSetNameFocusListener implements FocusListener
     {

        /**
         **************************************************************************
         *
         * @param event
         */
        public void focusGained(FocusEvent event)
        {
        }

        /**
         **************************************************************************
         *
         * @param event
         */
        public void focusLost(FocusEvent event)
        {
            String ruleSetName = m_name.getText().trim();

            if (ruleSetName.length() == 0)
            {
                String message = "The rule set name is missing.";
                m_name.removeFocusListener(this);
                MessageDialog.show(getParentWindow(), message);
                m_name.addFocusListener(this);
                m_name.requestFocus();
            }
            else if (ruleSetName.equalsIgnoreCase(m_originalName) == false)
            {
                if (m_currentDataNode.getSibling(ruleSetName) != null)
                {
                    String template = "Another rule set already has the name \"{0}\".";
                    String[] args = {ruleSetName};
                    String message = MessageFormat.format(template, args);
                    m_name.removeFocusListener(this);
                    MessageDialog.show(getParentWindow(), message);
                    m_name.addFocusListener(this);
                    m_name.requestFocus();
                }
            }
        }
     }

    /**
     ************************************************************************************
     ************************************************************************************
     ************************************************************************************
     */
    private class RulesEditingEventHandler implements RulesEditingEventListener
    {

        /**
         *************************************************************************
         *
         * @param event
         */
        public void loadData(RulesEditingEvent event)
        {
            RuleSetEditingPanel.this.loadData(event.getDataNode());
        }

        /**
         *************************************************************************
         *
         * @param event
         */
        public void saveData(RulesEditingEvent event)
        {
            RuleSetEditingPanel.this.saveData(event.getDataNode());
        }
    }
}