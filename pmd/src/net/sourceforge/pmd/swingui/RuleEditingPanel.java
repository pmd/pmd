package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.swingui.event.ListenerList;
import net.sourceforge.pmd.swingui.event.RulesEditingEvent;
import net.sourceforge.pmd.swingui.event.RulesEditingEventListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.MessageFormat;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
class RuleEditingPanel extends JPanel {

    private JLabel m_nameLabel;
    private JTextField m_name;
    private JLabel m_classNameLabel;
    private JTextField m_className;
    private JLabel m_messageLabel;
    private JTextArea m_message;
    private JScrollPane m_messageScrollPane;
    private JLabel m_descriptionLabel;
    private JTextArea m_description;
    private JScrollPane m_descriptionScrollPane;
    private JLabel m_exampleLabel;
    private JTextArea m_example;
    private JScrollPane m_exampleScrollPane;
    private JLabel m_priorityLabel;
    private JComboBox m_priority;
    private boolean m_enabled;
    private RulesTreeNode m_currentDataNode;
    private boolean m_isEditing;
    private String m_originalName;
    private FocusListener m_focusListener = new RuleNameFocusListener();

    /**
     *******************************************************************************
     *
     * @return
     */
    protected RuleEditingPanel() {
        super(new BorderLayout());

        EmptyBorder emptyBorder = new EmptyBorder(5, 5, 5, 5);

        setBorder(emptyBorder);

        JPanel panel;
        TitledBorder titledBorder;
        GridBagLayout layout;
        GridBagConstraints constraints;

        int[] columnWidths = {50, 100, 100, 100, 100, 100};

        layout = new GridBagLayout();
        layout.columnWidths = columnWidths;
        panel = new JPanel(layout);
        titledBorder = ComponentFactory.createTitledBorder("  Rule  ");

        panel.setBorder(titledBorder);
        add(panel, BorderLayout.CENTER);

        Font labelFont = UIManager.getFont("labelFont");

        // Rule Name Label
        m_nameLabel = new JLabel("Name");
        m_nameLabel.setFont(labelFont);
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

        // Rule Name Text
        m_name = new JTextField();
        m_name.setFont(UIManager.getFont("dataFont"));
        m_name.addFocusListener(m_focusListener);
        m_name.setRequestFocusEnabled(true);
        m_name.setOpaque(true);
        constraints = layout.getConstraints(m_name);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_name, constraints);

        // Rule Class Name Label
        m_classNameLabel = new JLabel("Class Name");
        m_classNameLabel.setFont(labelFont);
        m_classNameLabel.setHorizontalAlignment(JLabel.RIGHT);
        constraints = layout.getConstraints(m_name);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_classNameLabel, constraints);

        // Rule Class Name Text
        m_className = new JTextField();
        m_className.setFont(UIManager.getFont("dataFont"));
        m_className.setBackground(UIManager.getColor("disabledTextBackground"));
        m_className.setForeground(Color.black);
        m_className.setSelectedTextColor(Color.black);
        m_className.setDisabledTextColor(Color.black);
        m_className.setEditable(false);
        m_className.setEnabled(false);
        m_className.setOpaque(true);
        constraints = layout.getConstraints(m_name);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_className, constraints);

        // Rule Message Label
        m_messageLabel = new JLabel("Message");
        m_messageLabel.setFont(labelFont);
        m_messageLabel.setHorizontalAlignment(JLabel.RIGHT);
        constraints = layout.getConstraints(m_name);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_messageLabel, constraints);

        // Rule Message Text
        m_message = ComponentFactory.createTextArea("");

        // Rule Message Scroll Pane;
        m_messageScrollPane = ComponentFactory.createScrollPane(m_message);
        constraints = layout.getConstraints(m_name);
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridheight = 1;
        constraints.ipady = 4 * 20;  // 4 lines * 20 pixels/line
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_messageScrollPane, constraints);

        // Rule Description Label
        m_descriptionLabel = new JLabel("Description");
        m_descriptionLabel.setFont(labelFont);
        m_descriptionLabel.setHorizontalAlignment(JLabel.RIGHT);
        constraints = layout.getConstraints(m_name);
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_descriptionLabel, constraints);

        // Rule Description Text
        m_description = ComponentFactory.createTextArea("");

        // Rule Description Scroll Pane;
        m_descriptionScrollPane = ComponentFactory.createScrollPane(m_description);
        constraints = layout.getConstraints(m_name);
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridheight = 1;
        constraints.ipady = 4 * 20;  // 4 lines * 20 pixels/line
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_descriptionScrollPane, constraints);

        // Rule Example Label
        m_exampleLabel = new JLabel("Example");
        m_exampleLabel.setFont(labelFont);
        m_exampleLabel.setHorizontalAlignment(JLabel.RIGHT);
        constraints = layout.getConstraints(m_name);
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_exampleLabel, constraints);

        // Rule Example Text
        m_example = ComponentFactory.createTextArea("");
        m_example.setFont(UIManager.getFont("codeFont"));

        // Rule Example Scroll Pane;
        m_exampleScrollPane = ComponentFactory.createScrollPane(m_example);
        constraints = layout.getConstraints(m_name);
        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridheight = 1;
        constraints.ipady = 6 * 20;  // 6 lines * 20 pixels/line
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_exampleScrollPane, constraints);

        // Rule Priority Label
        m_priorityLabel = new JLabel("Priority");
        m_priorityLabel.setFont(labelFont);
        m_priorityLabel.setHorizontalAlignment(JLabel.RIGHT);
        constraints = layout.getConstraints(m_name);
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_priorityLabel, constraints);

        // Rule Priority
        m_priority = new JComboBox(Rule.PRIORITIES);
        constraints = layout.getConstraints(m_name);
        constraints.gridx = 1;
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_priority, constraints);

        enableData(false);

        ListenerList.addListener(new RulesEditingEventHandler());
    }

    /**
     *******************************************************************************
     *
     * @param dataNode
     */
    private void saveData(RulesTreeNode dataNode) {
        if ((dataNode != null) && m_isEditing) {
            if (dataNode.isRule() || dataNode.isProperty()) {
                String ruleName = m_name.getText().trim();

                if (ruleName.length() == 0) {
                    String message = "The rule name is missing.  The change will not be applied.";

                    m_name.removeFocusListener(m_focusListener);
                    MessageDialog.show(getParentWindow(), message);
                    m_name.addFocusListener(m_focusListener);

                    if (m_name.hasFocus()) {
                        m_name.requestFocus();
                    }

                    ruleName = m_originalName;
                } else if (ruleName.equalsIgnoreCase(m_originalName) == false) {
                    if (dataNode.getSibling(ruleName) != null) {
                        String template = "Another rule already has the name \"{0}\".  The change will not be applied.";
                        String[] args = {ruleName};
                        String message = MessageFormat.format(template, args);

                        m_name.removeFocusListener(m_focusListener);
                        MessageDialog.show(getParentWindow(), message);
                        m_name.addFocusListener(m_focusListener);

                        if (m_name.hasFocus()) {
                            m_name.requestFocus();
                        }

                        ruleName = m_originalName;
                    }
                }

                dataNode.setName(ruleName);
                dataNode.setClassName(m_className.getText());
                dataNode.setMessage(m_message.getText());
                dataNode.setDescription(m_description.getText());
                dataNode.setExample(m_example.getText());
                dataNode.setPriority(m_priority.getSelectedIndex() + 1);
            }
        }
    }

    /**
     *******************************************************************************
     *
     * @param isEditing
     */
    protected void setIsEditing(boolean isEditing) {
        m_isEditing = isEditing;
    }

    /**
     *******************************************************************************
     *
     * @param dataNode
     */
    private void loadData(RulesTreeNode dataNode) {
        if (dataNode == null) {
            enableData(false);
        } else if (dataNode.isRuleSet()) {
            enableData(false);
        } else if (dataNode.isRule()) {
            loadData_(dataNode);
        } else if (dataNode.isProperty()) {
            loadData_(dataNode.getParentRuleData());
        } else {
            enableData(false);
        }
    }

    /**
     *******************************************************************************
     *
     * @param dataNode
     */
    private void loadData_(RulesTreeNode dataNode) {
        if (m_enabled == false) {
            enableData(true);
        }

        m_name.setText(dataNode.getName());
        m_className.setText(dataNode.getClassName());
        m_message.setText(dataNode.getMessage());
        m_description.setText(dataNode.getDescription());
        m_example.setText(dataNode.getExample());
        m_priority.setSelectedIndex(dataNode.getPriority() - 1);
        m_originalName = dataNode.getName();
        m_currentDataNode = dataNode;
    }

    /**
     *******************************************************************************
     *
     */
    private void enableData(boolean enable) {
        if (enable) {
            // Just to be sure the focus listener isn't already set.
            m_name.removeFocusListener(m_focusListener);
            m_name.addFocusListener(m_focusListener);

            m_nameLabel.setEnabled(true);

            m_name.setEnabled(true);
            m_name.setBackground(Color.white);

            m_messageLabel.setEnabled(true);

            m_message.setEnabled(true);
            m_message.setBackground(Color.white);

            m_classNameLabel.setEnabled(true);

            m_descriptionLabel.setEnabled(true);

            m_description.setEnabled(true);
            m_description.setBackground(Color.white);

            m_exampleLabel.setEnabled(true);

            m_example.setEnabled(true);
            m_example.setBackground(Color.white);

            m_priorityLabel.setEnabled(true);

            m_priority.setEnabled(true);
            m_priority.setBackground(Color.white);
        } else {
            m_name.removeFocusListener(m_focusListener);

            Color background = UIManager.getColor("disabledTextBackground");

            m_nameLabel.setEnabled(false);

            m_name.setText("");
            m_name.setEnabled(false);
            m_name.setBackground(background);

            m_messageLabel.setEnabled(false);

            m_message.setText("");
            m_message.setEnabled(false);
            m_message.setBackground(background);

            m_classNameLabel.setEnabled(false);

            m_className.setText("");

            m_descriptionLabel.setEnabled(false);

            m_description.setText("");
            m_description.setEnabled(false);
            m_description.setBackground(background);

            m_exampleLabel.setEnabled(false);

            m_example.setText("");
            m_example.setEnabled(false);
            m_example.setBackground(background);

            m_priorityLabel.setEnabled(false);

            m_priority.setSelectedIndex(0);
            m_priority.setEnabled(false);
            m_priority.setBackground(background);

            m_currentDataNode = null;
        }

        m_enabled = enable;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    private Window getParentWindow() {
        Component component = getParent();

        while ((component != null) && ((component instanceof Window) == false)) {
            component = component.getParent();
        }

        return (Window) component;
    }

    /**
     ************************************************************************************
     ************************************************************************************
     ************************************************************************************
     */

    private class RuleNameFocusListener implements FocusListener {

        /**
         **************************************************************************
         *
         * @param event
         */
        public void focusGained(FocusEvent event) {
        }

        /**
         **************************************************************************
         *
         * @param event
         */
        public void focusLost(FocusEvent event) {
            String ruleName = m_name.getText().trim();

            if (ruleName.length() == 0) {
                String message = "The rule name is missing.";
                m_name.removeFocusListener(this);
                MessageDialog.show(getParentWindow(), message);
                m_name.addFocusListener(this);
                m_name.requestFocus();
            } else if (ruleName.equalsIgnoreCase(m_originalName) == false) {
                if (m_currentDataNode.getSibling(ruleName) != null) {
                    String template = "Another rule already has the name \"{0}\".";
                    String[] args = {ruleName};
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
    private class RulesEditingEventHandler implements RulesEditingEventListener {

        /**
         *************************************************************************
         *
         * @param event
         */
        public void loadData(RulesEditingEvent event) {
            RuleEditingPanel.this.loadData(event.getDataNode());
        }

        /**
         *************************************************************************
         *
         * @param event
         */
        public void saveData(RulesEditingEvent event) {
            RuleEditingPanel.this.saveData(event.getDataNode());
        }
    }
}