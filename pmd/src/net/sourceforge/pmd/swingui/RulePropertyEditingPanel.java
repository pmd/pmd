package net.sourceforge.pmd.swingui;

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
class RulePropertyEditingPanel extends JPanel implements Constants {

    private JLabel m_nameLabel;
    private JTextField m_name;
    private JLabel m_valueLabel;
    private JTextField m_value;
    private JLabel m_valueTypeLabel;
    private JComboBox m_valueType;
    private boolean m_enabled;
    private RulesTreeNode m_currentDataNode;
    private boolean m_isEditing;
    private String m_originalName;
    private String m_originalValue;
    private FocusListener m_focusListener = new PropertyNameFocusListener();

    /**
     *******************************************************************************
     *
     * @return
     */
    protected RulePropertyEditingPanel() {
        super(new BorderLayout());

        EmptyBorder emptyBorder = new EmptyBorder(5, 5, 5, 5);

        setBorder(emptyBorder);

        JPanel panel;
        TitledBorder titledBorder;
        GridBagLayout layout;
        GridBagConstraints constraints;

        int[] columnWidths = {50, 100, 100};
        layout = new GridBagLayout();
        layout.columnWidths = columnWidths;
        panel = new JPanel(layout);
        titledBorder = ComponentFactory.createTitledBorder("  Property  ");

        panel.setBorder(titledBorder);
        add(panel, BorderLayout.CENTER);

        // Property Name Label
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

        // Property Name Text
        m_name = new JTextField();
        m_name.setFont(UIManager.getFont("dataFont"));
        m_name.addFocusListener(m_focusListener);
        m_name.setRequestFocusEnabled(true);
        constraints = layout.getConstraints(m_name);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_name, constraints);

        // Property Value Label
        m_valueLabel = new JLabel("Value");
        m_valueLabel.setFont(UIManager.getFont("labelFont"));
        m_valueLabel.setHorizontalAlignment(JLabel.RIGHT);
        constraints = layout.getConstraints(m_nameLabel);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_valueLabel, constraints);

        // Property Value Text
        m_value = new JTextField();
        m_value.setFont(UIManager.getFont("dataFont"));
        m_value.setOpaque(true);
        constraints = layout.getConstraints(m_name);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_value, constraints);

        // Property Value Type Label
        m_valueTypeLabel = new JLabel("Type");
        m_valueTypeLabel.setFont(UIManager.getFont("labelFont"));
        m_valueTypeLabel.setHorizontalAlignment(JLabel.RIGHT);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_valueTypeLabel, constraints);

        // Property Value Type
        String[] items = {STRING, BOOLEAN, DECIMAL_NUMBER, INTEGER};
        m_valueType = new JComboBox(items);
        m_valueType.setEditable(false);
        m_valueType.setOpaque(true);
        constraints = layout.getConstraints(m_name);
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(m_valueType, constraints);

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
            if (dataNode.isProperty()) {
                // Test for valid property name.
                String propertyName = m_name.getText();

                if (propertyName.equalsIgnoreCase(m_originalName) == false) {
                    if (dataNode.getSibling(propertyName) != null) {
                        String template = "Another property already has the name \"{0}\".  The change will not be applied.";
                        String[] args = {propertyName};
                        String message = MessageFormat.format(template, args);
                        boolean hasFocus = m_name.hasFocus();

                        m_name.removeFocusListener(m_focusListener);
                        MessageDialog.show(getParentWindow(), message);
                        m_name.addFocusListener(m_focusListener);

                        if (hasFocus) {
                            m_name.requestFocus();
                        }

                        propertyName = m_originalName;
                    }
                }

                // Test for valid value.
                String valueText = m_value.getText();
                String selectedItem = (String) m_valueType.getSelectedItem();

                if (selectedItem.equalsIgnoreCase(BOOLEAN)) {
                    valueText = saveBoolean(valueText);
                } else if (selectedItem.equalsIgnoreCase(DECIMAL_NUMBER)) {
                    valueText = saveDecimalNumber(valueText);
                } else if (selectedItem.equalsIgnoreCase(INTEGER)) {
                    valueText = saveInteger(valueText);
                }

                dataNode.setName(propertyName);
                dataNode.setPropertyValue(valueText);
                dataNode.setPropertyValueType(selectedItem);
            }
        }
    }

    /**
     *******************************************************************************
     *
     * @param valueText
     */
    private String saveBoolean(String valueText) {
        boolean originalValue;
        boolean newValue;

        try {
            originalValue = Boolean.getBoolean(m_originalValue);
        } catch (NumberFormatException exception) {
            originalValue = true;
        }

        try {
            newValue = Boolean.getBoolean(valueText);
            valueText = String.valueOf(newValue);
        } catch (NumberFormatException exception) {
            String template = "New property of \"{0}\" is not a boolean.  The change will not be applied.";
            String[] args = {valueText};
            String message = MessageFormat.format(template, args);

            m_name.removeFocusListener(m_focusListener);
            MessageDialog.show(getParentWindow(), message);
            m_name.addFocusListener(m_focusListener);

            newValue = originalValue;
            valueText = m_originalValue;
        }

        return valueText;
    }

    /**
     *******************************************************************************
     *
     * @param valueText
     */
    private String saveDecimalNumber(String valueText) {
        double originalValue;
        double newValue;

        try {
            originalValue = Double.parseDouble(m_originalValue);
        } catch (NumberFormatException exception) {
            originalValue = 0.0;
        }

        try {
            newValue = Double.parseDouble(valueText);
            valueText = String.valueOf(newValue);
        } catch (NumberFormatException exception) {
            String template = "New property of \"{0}\" is not a decimal number.  The change will not be applied.";
            String[] args = {valueText};
            String message = MessageFormat.format(template, args);

            m_name.removeFocusListener(m_focusListener);
            MessageDialog.show(getParentWindow(), message);
            m_name.addFocusListener(m_focusListener);

            newValue = originalValue;
            valueText = m_originalValue;
        }

        return valueText;
    }

    /**
     *******************************************************************************
     *
     * @param valueText
     */
    private String saveInteger(String valueText) {
        int originalValue;
        int newValue;

        try {
            originalValue = Integer.parseInt(m_originalValue);
        } catch (NumberFormatException exception) {
            originalValue = 0;
        }

        try {
            newValue = Integer.parseInt(valueText);
            valueText = String.valueOf(newValue);
        } catch (NumberFormatException exception) {
            String template = "New property of \"{0}\" is not an integer.  The change will not be applied.";
            String[] args = {valueText};
            String message = MessageFormat.format(template, args);

            m_name.removeFocusListener(m_focusListener);
            MessageDialog.show(getParentWindow(), message);
            m_name.addFocusListener(m_focusListener);

            newValue = originalValue;
            valueText = m_originalValue;
        }

        return valueText;
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
            enableData(false);
        } else if (dataNode.isProperty()) {
            loadData_(dataNode);
        } else {
            enableData(false);
        }
    }

    /**
     *******************************************************************************
     *
     * @param data
     */
    private void loadData_(RulesTreeNode dataNode) {
        if (m_enabled == false) {
            enableData(true);
        }

        String name = dataNode.getName();
        String valueType = dataNode.getPropertyValueType();

        m_name.setText(name);
        m_value.setText(dataNode.getPropertyValue());
        m_valueType.setSelectedItem(valueType);

        m_originalName = name;
        m_originalValue = valueType;
        m_currentDataNode = dataNode;
    }

    /**
     *******************************************************************************
     *
     */
    private void enableData(boolean enable) {
        if (enable) {
            // Just to be sure the focus listener isn't set.
            m_name.removeFocusListener(m_focusListener);
            m_name.addFocusListener(m_focusListener);

            m_nameLabel.setEnabled(true);

            m_name.setEnabled(true);
            m_name.setBackground(Color.white);

            m_valueLabel.setEnabled(true);

            m_value.setEnabled(true);
            m_value.setBackground(Color.white);

            m_valueTypeLabel.setEnabled(true);

            m_valueType.setEnabled(true);
            m_valueType.setBackground(Color.white);
        } else {
            m_name.removeFocusListener(m_focusListener);

            Color background = UIManager.getColor("disabledTextBackground");

            m_nameLabel.setEnabled(false);

            m_name.setText("");
            m_name.setEnabled(false);
            m_name.setBackground(background);

            m_valueLabel.setEnabled(false);

            m_value.setText("");
            m_value.setEnabled(false);
            m_value.setBackground(background);

            m_valueTypeLabel.setEnabled(false);

            m_valueType.setSelectedIndex(0);
            m_valueType.setEnabled(false);
            m_valueType.setBackground(background);

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
    private class PropertyNameFocusListener implements FocusListener {

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
            String propertyName = m_name.getText().trim();

            if (propertyName.length() == 0) {
                String message = "The property name is missing.";
                m_name.removeFocusListener(this);
                MessageDialog.show(getParentWindow(), message);
                m_name.addFocusListener(this);
                m_name.requestFocus();
            } else if (propertyName.equalsIgnoreCase(m_originalName) == false) {
                if (m_currentDataNode.getSibling(propertyName) != null) {
                    String template = "Another property already has the name \"{0}\".";
                    String[] args = {propertyName};
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
            RulePropertyEditingPanel.this.loadData(event.getDataNode());
        }

        /**
         *************************************************************************
         *
         * @param event
         */
        public void saveData(RulesEditingEvent event) {
            RulePropertyEditingPanel.this.saveData(event.getDataNode());
        }
    }
}
