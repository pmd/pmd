package net.sourceforge.pmd.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Window;
import java.text.MessageFormat;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
public class RulePropertyEditingPanel extends JPanel implements IConstants
{

    private JLabel m_nameLabel;
    private JTextField m_name;
    private JLabel m_valueLabel;
    private JTextField m_value;
    private JLabel m_valueTypeLabel;
    private JComboBox m_valueType;
    private boolean m_enabled;
    private IRulesEditingData m_currentData;
    private boolean m_isEditing;
    private String m_originalName;
    private String m_originalValue;
    private FocusListener m_focusListener = new PropertyNameFocusListener();

    /**
     *******************************************************************************
     *
     * @return
     */
    public RulePropertyEditingPanel()
    {
        super(new BorderLayout());

        EmptyBorder emptyBorder = new EmptyBorder(15, 15, 15, 15);

        setBorder(emptyBorder);

        JPanel panel;
        TitledBorder titledBorder;

        panel = new JPanel(new PropertyLayout());
        titledBorder = ComponentFactory.createTitledBorder("  Property  ");

        panel.setBorder(titledBorder);
        add(panel, BorderLayout.CENTER);

        // Property Name Label
        m_nameLabel = new JLabel("Name");
        m_nameLabel.setFont(UIManager.getFont("labelFont"));
        m_nameLabel.setHorizontalAlignment(JLabel.RIGHT);
        m_nameLabel.setOpaque(true);
        panel.add(m_nameLabel);

        // Property Name Text
        m_name = new JTextField();
        m_name.setFont(UIManager.getFont("dataFont"));
        m_name.addFocusListener(m_focusListener);
        m_name.setRequestFocusEnabled(true);
        m_name.setOpaque(true);
        panel.add(m_name);

        // Property Value Label
        m_valueLabel = new JLabel("Value");
        m_valueLabel.setFont(UIManager.getFont("labelFont"));
        m_valueLabel.setHorizontalAlignment(JLabel.RIGHT);
        m_valueLabel.setOpaque(true);
        panel.add(m_valueLabel);

        // Property Value Text
        m_value = new JTextField();
        m_value.setFont(UIManager.getFont("dataFont"));
        m_value.setOpaque(true);
        panel.add(m_value);

        // Property Value Type Label
        m_valueTypeLabel = new JLabel("Type");
        m_valueTypeLabel.setFont(UIManager.getFont("labelFont"));
        m_valueTypeLabel.setHorizontalAlignment(JLabel.RIGHT);
        m_valueTypeLabel.setOpaque(true);
        panel.add(m_valueTypeLabel);

        // Property Value Type
        String[] items = {STRING, BOOLEAN, DECIMAL_NUMBER, INTEGER};
        m_valueType = new JComboBox(items);
        m_valueType.setEditable(false);
        m_valueType.setOpaque(true);
        panel.add(m_valueType);

        enableData(false);
    }

    /**
     *******************************************************************************
     *
     */
    public void saveData()
    {
        if (m_isEditing && (m_currentData != null))
        {
            // Test for valid property name.
            String propertyName = m_name.getText();

            if (propertyName.equalsIgnoreCase(m_originalName) == false)
            {
                if (m_currentData.getSibling(propertyName) != null)
                {
                    String template = "Another property already has the name \"{0}\".  The change will not be applied.";
                    String[] args = {propertyName};
                    String message = MessageFormat.format(template, args);
                    boolean hasFocus = m_name.hasFocus();

                    m_name.removeFocusListener(m_focusListener);
                    MessageDialog.show(getParentWindow(), message);
                    m_name.addFocusListener(m_focusListener);

                    if (hasFocus)
                    {
                        m_name.requestFocus();
                    }

                    propertyName = m_originalName;
                }
            }

            // Test for valid value.
            String valueText = m_value.getText();
            String selectedItem = (String) m_valueType.getSelectedItem();

            if (selectedItem.equalsIgnoreCase(BOOLEAN))
            {
                valueText = saveBoolean(valueText);
            }
            else if (selectedItem.equalsIgnoreCase(DECIMAL_NUMBER))
            {
                valueText = saveDecimalNumber(valueText);
            }
            else if (selectedItem.equalsIgnoreCase(INTEGER))
            {
                valueText = saveInteger(valueText);
            }

            m_currentData.setName(propertyName);
            m_currentData.setPropertyValue(valueText);
            m_currentData.setPropertyValueType(selectedItem);
            enableData(false);
        }
    }

    /**
     *******************************************************************************
     *
     * @param valueText
     */
    private String saveBoolean(String valueText)
    {
        boolean originalValue;
        boolean newValue;

        try
        {
            originalValue = Boolean.getBoolean(m_originalValue);
        }
        catch (NumberFormatException exception)
        {
            originalValue = true;
        }

        try
        {
            newValue = Boolean.getBoolean(valueText);
            valueText = String.valueOf(newValue);
        }
        catch (NumberFormatException exception)
        {
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
    private String saveDecimalNumber(String valueText)
    {
        double originalValue;
        double newValue;

        try
        {
            originalValue = Double.parseDouble(m_originalValue);
        }
        catch (NumberFormatException exception)
        {
            originalValue = 0.0;
        }

        try
        {
            newValue = Double.parseDouble(valueText);
            valueText = String.valueOf(newValue);
        }
        catch (NumberFormatException exception)
        {
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
    private String saveInteger(String valueText)
    {
        int originalValue;
        int newValue;

        try
        {
            originalValue = Integer.parseInt(m_originalValue);
        }
        catch (NumberFormatException exception)
        {
            originalValue = 0;
        }

        try
        {
            newValue = Integer.parseInt(valueText);
            valueText = String.valueOf(newValue);
        }
        catch (NumberFormatException exception)
        {
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
    protected void setIsEditing(boolean isEditing)
    {
        m_isEditing = isEditing;
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
     * @return
     */
    public IRulesEditingData getData()
    {
        return m_currentData;
    }

    /**
     *******************************************************************************
     *
     * @param data
     */
    public void setData(IRulesEditingData data)
    {
        if (data == null)
        {
            enableData(false);
        }
        else if (data.isRuleSet())
        {
            enableData(false);
        }
        else if (data.isRule())
        {
            enableData(false);
        }
        else if (data.isProperty())
        {
            setData_(data);
        }
        else
        {
            enableData(false);
        }
    }

    /**
     *******************************************************************************
     *
     * @param data
     */
    private void setData_(IRulesEditingData data)
    {
        if (data != null)
        {
            if (m_enabled == false)
            {
                enableData(true);
            }

            String name = data.getName();
            String valueType = data.getPropertyValueType();

            m_name.setText(name);
            m_value.setText(data.getPropertyValue());
            m_valueType.setSelectedItem(valueType);

            m_originalName = name;
            m_originalValue = valueType;
            m_currentData = data;
        }
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
            m_name.setEnabled(true);
            m_name.setBackground(Color.white);
            m_value.setEnabled(true);
            m_value.setBackground(Color.white);
            m_valueType.setEnabled(true);
            m_valueType.setBackground(Color.white);
        }
        else
        {
            m_name.removeFocusListener(m_focusListener);

            Color background = UIManager.getColor("disabledTextBackground");

            m_name.setText("");
            m_name.setEnabled(false);
            m_name.setBackground(background);

            m_value.setText("");
            m_value.setEnabled(false);
            m_value.setBackground(background);

            m_valueType.setSelectedIndex(0);
            m_valueType.setEnabled(false);
            m_valueType.setBackground(background);

            m_currentData = null;
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
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class PropertyLayout implements LayoutManager
    {

        /**
         ***************************************************************************
         * Adds the specified component with the specified name to the layout.
         *
         * @param name The component name.
         * @param component The component to be added.
         */
        public void addLayoutComponent(String name, Component component)
        {
        }

        /**
         ***************************************************************************
         * Removes the specified component from the layout.
         *
         * @param component The component to be removed.
         */
        public void removeLayoutComponent(Component component)
        {
        }

        /**
         ***************************************************************************
         * Calculates the preferred size dimensions for the specified panel given the
         * components in the specified parent container.
         *
         * @param parent The component to be laid out.
         */
        public Dimension preferredLayoutSize(Container parent)
        {
            Dimension size;
            int parentWidth;

            size = layoutContainer(parent, true);
            parentWidth = parent.getWidth();

            if (size.width > parentWidth)
            {
                size.width = parentWidth;
            }

            return size;
        }

        /**
         **************************************************************************
         * Calculates the minimum size dimensions for the specified panel given the
         * components in the specified parent container.
         *
         * @param parent The component to be laid out.
         */
        public Dimension minimumLayoutSize(Container parent)
        {
            Dimension size;

            size = layoutContainer(parent, true);
            size.width = 50;

            return size;
        }

        /**
         **************************************************************************
         * Lays out the container in the specified panel.
         *
         * @param parent The component which needs to be laid out.
         */
        public void layoutContainer(Container parent)
        {
            layoutContainer(parent, false);
        }

        /**
         **************************************************************************
         * Lays out the container in the specified panel.
         *
         * @param parent The component which needs to be laid out.
         * @param computePanelSize
         *
         * @return
         */
        private Dimension layoutContainer(Container parent, boolean computePanelSize)
        {
            Dimension containerSize;
            Insets containerInsets;
            Font font;
            FontMetrics fontMetrics;

            containerSize = parent.getSize();
            containerInsets = new Insets(10, 10, 10, 10);

            if (parent instanceof JComponent)
            {
                Border border = ((JComponent) parent).getBorder();

                if (border != null)
                {
                    Insets borderInsets;

                    borderInsets = border.getBorderInsets(parent);
                    containerInsets.left += borderInsets.left;
                    containerInsets.top += borderInsets.top;
                    containerInsets.right += borderInsets.right;
                    containerInsets.bottom += borderInsets.bottom;
                }
            }

            //
            // Calculate the first column that contains the labels.
            //

            // Calculate Name Label
            int nameLabelWidth;
            int nameLabelHeight;

            font = m_nameLabel.getFont();
            fontMetrics = m_nameLabel.getFontMetrics(font);
            nameLabelWidth = fontMetrics.stringWidth(m_nameLabel.getText());
            nameLabelHeight = fontMetrics.getHeight();

            // Calculate Value Label
            int valueLabelWidth;
            int valueLabelHeight;

            font = m_valueLabel.getFont();
            fontMetrics = m_valueLabel.getFontMetrics(font);
            valueLabelWidth = fontMetrics.stringWidth(m_valueLabel.getText());
            valueLabelHeight = fontMetrics.getHeight();

            // Calculate Value Type Label
            int valueTypeLabelWidth;
            int valueTypeLabelHeight;

            font = m_valueTypeLabel.getFont();
            fontMetrics = m_valueTypeLabel.getFontMetrics(font);
            valueTypeLabelWidth = fontMetrics.stringWidth(m_valueTypeLabel.getText());
            valueTypeLabelHeight = fontMetrics.getHeight();

            // Calculate first column width.
            int firstColumnWidth = nameLabelWidth;

            if (valueLabelWidth > firstColumnWidth)
            {
                firstColumnWidth = valueLabelWidth;
            }

            if (valueTypeLabelWidth > firstColumnWidth)
            {
                firstColumnWidth = valueTypeLabelWidth;
            }

            //
            // Set the margin between label and data.
            //
            int columnSpacing = 5;
            int lineSpacing = 10;

            //
            // Calculate the second column that contains the data fields.
            //
            Insets insets;

            // Calculate Name Field
            int nameWidth;
            int nameHeight;

            font = m_name.getFont();
            fontMetrics = m_name.getFontMetrics(font);
            insets = m_name.getBorder().getBorderInsets(m_name);
            nameWidth = insets.left + insets.right;
            nameHeight = fontMetrics.getHeight() + insets.top + insets.bottom;

            // Calculate Value Field
            int valueWidth;
            int valueHeight;

            font = m_value.getFont();
            fontMetrics = m_value.getFontMetrics(font);
            insets = m_value.getBorder().getBorderInsets(m_value);
            valueWidth = insets.left + insets.right;
            valueHeight = fontMetrics.getHeight() + insets.top + insets.bottom;

            // Calculate Value Type Popup Menu
            int valueTypeWidth;
            int valueTypeHeight;

            font = m_value.getFont();
            fontMetrics = m_valueType.getFontMetrics(font);
            insets = m_valueType.getBorder().getBorderInsets(m_valueType);
            valueTypeWidth = insets.left + insets.right;
            valueTypeHeight = 20;

            // Calculate second column width.
            int secondColumnWidth = containerSize.width
                                  - containerInsets.left
                                  - containerInsets.right
                                  - firstColumnWidth
                                  - columnSpacing;

            // Calculate Line Heights
            int firstLineHeight = (nameHeight > nameLabelHeight) ? nameHeight : nameLabelHeight;
            int secondLineHeight = (valueHeight > valueLabelHeight) ? valueHeight : valueLabelHeight;
            int thirdLineHeight = (valueTypeHeight > valueTypeLabelHeight) ? valueTypeHeight : valueTypeLabelHeight;

            if (computePanelSize)
            {
                int panelWidth = containerInsets.left
                               + firstColumnWidth
                               + columnSpacing
                               + secondColumnWidth
                               + containerInsets.right;
                int panelHeight = containerInsets.top
                                + firstLineHeight
                                + lineSpacing
                                + secondLineHeight
                                + lineSpacing
                                + thirdLineHeight
                                + containerInsets.bottom;

                return new Dimension(panelWidth, panelHeight);
            }

            // Layout components
            int x;
            int y;
            int yOffset;

            // Layout Name Label
            x = containerInsets.left;
            y = containerInsets.top;
            yOffset = (firstLineHeight - nameLabelHeight) / 2;
            m_nameLabel.setBounds(x, y + yOffset, firstColumnWidth, nameLabelHeight);

            // Layout Name
            x = containerInsets.left + firstColumnWidth + columnSpacing;
            y = containerInsets.top;
            yOffset = (firstLineHeight - nameHeight) / 2;
            m_name.setBounds(x, y + yOffset, secondColumnWidth, nameHeight);

            // Layout Value Label
            x = containerInsets.left;
            y = containerInsets.top + firstLineHeight + lineSpacing;
            m_valueLabel.setBounds(x, y, firstColumnWidth, valueLabelHeight);

            // Layout Value
            x = containerInsets.left + firstColumnWidth + columnSpacing;
            y = containerInsets.top + firstLineHeight + lineSpacing;
            m_value.setBounds(x, y, secondColumnWidth, valueHeight);

            // Layout Value Type Label
            x = containerInsets.left;
            y = containerInsets.top + firstLineHeight + lineSpacing + secondLineHeight + lineSpacing;
            m_valueTypeLabel.setBounds(x, y, firstColumnWidth, valueTypeLabelHeight);

            // Layout Value Type Popup Menu
            x = containerInsets.left + firstColumnWidth + columnSpacing;
            y = containerInsets.top + firstLineHeight + lineSpacing + secondLineHeight + lineSpacing;
            m_valueType.setBounds(x, y, secondColumnWidth, valueTypeHeight);

            return null;
        }
    }

    /**
     ************************************************************************************
     ************************************************************************************
     ************************************************************************************
     */
     private class PropertyNameFocusListener implements FocusListener
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
            String propertyName = m_name.getText().trim();

            if (propertyName.length() == 0)
            {
                String message = "The property name is missing.";
                m_name.removeFocusListener(this);
                MessageDialog.show(getParentWindow(), message);
                m_name.addFocusListener(this);
                m_name.requestFocus();
            }
            else if (propertyName.equalsIgnoreCase(m_originalName) == false)
            {
                if (m_currentData.getSibling(propertyName) != null)
                {
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
}