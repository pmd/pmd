package net.sourceforge.pmd.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JComponent;
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
public class RulePropertyEditingPanel extends JPanel
{

    private JLabel m_nameLabel;
    private JTextField m_name;
    private JLabel m_valueLabel;
    private JTextField m_value;
    private boolean m_enabled;
    private IRulesEditingData m_currentData;

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
        panel.add(m_nameLabel);

        // Property Name Text
        m_name = new JTextField();
        m_name.setFont(UIManager.getFont("dataFont"));
        panel.add(m_name);

        // Property Value Label
        m_valueLabel = new JLabel("Value");
        m_valueLabel.setFont(UIManager.getFont("labelFont"));
        m_valueLabel.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(m_valueLabel);

        // Property Value Text
        m_value = new JTextField();
        m_value.setFont(UIManager.getFont("dataFont"));
        panel.add(m_value);

        disableData();
    }

    /**
     *******************************************************************************
     *
     * @param event
     */
    public void valueChanged(IRulesEditingData data)
    {
        saveData();

        if (data.isRuleSet())
        {
            disableData();
        }
        else if (data.isRule())
        {
            disableData();
        }
        else if (data.isProperty())
        {
            setData(data);
        }
        else
        {
            disableData();
        }
    }

    /**
     *******************************************************************************
     *
     */
    private void saveData()
    {
        if (m_currentData != null)
        {
            m_currentData.setName(m_name.getText());
            m_currentData.setPropertyValue(m_value.getText());
        }
    }

    /**
     *******************************************************************************
     *
     * @param treeNode
     */
    private void setData(IRulesEditingData data)
    {
        if (data != null)
        {
            if (m_enabled == false)
            {
                m_name.setEnabled(true);
                m_name.setBackground(Color.white);
                m_value.setEnabled(true);
                m_value.setBackground(Color.white);
                m_enabled = true;
            }

            m_name.setText(data.getName());
            m_value.setText(data.getPropertyValue());

            m_currentData = data;
        }
    }

    /**
     *******************************************************************************
     *
     */
    private void disableData()
    {
        Color background = UIManager.getColor("disabledTextBackground");

        m_name.setText("");
        m_name.setEnabled(false);
        m_name.setBackground(background);

        m_value.setText("");
        m_value.setEnabled(false);
        m_value.setBackground(background);

        m_enabled = false;
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
            return new Dimension(100, 100);
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
            return new Dimension(100, 100);
        }

        /**
         **************************************************************************
         * Lays out the container in the specified panel.
         *
         * @param parent The component which needs to be laid out.
         */
        public void layoutContainer(Container parent)
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

            // Calculate first column width.
            int firstColumnWidth = nameLabelWidth;

            if (valueLabelWidth > firstColumnWidth)
            {
                firstColumnWidth = valueLabelWidth;
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

            // Calculate second column width.
            int secondColumnWidth = containerSize.width
                                  - containerInsets.left
                                  - containerInsets.right
                                  - firstColumnWidth
                                  - columnSpacing;

            // Calculate Line Heights
            int firstLineHeight = (nameHeight > nameLabelHeight) ? nameHeight : nameLabelHeight;
            int secondLineHeight = (valueHeight > valueLabelHeight) ? valueHeight : valueLabelHeight;

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
        }
    }
}