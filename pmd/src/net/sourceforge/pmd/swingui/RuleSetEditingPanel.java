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
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import net.sourceforge.pmd.RuleSet;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
public class RuleSetEditingPanel extends JPanel
{
    private JLabel m_nameLabel;
    private JTextField m_name;
    private JLabel m_descriptionLabel;
    private JTextArea m_description;
    private JScrollPane m_descriptionScrollPane;
    private JLabel m_includeLabel;
    private JCheckBox m_include;
    private boolean m_enabled;
    private IRulesEditingData m_currentData;

    /**
     *******************************************************************************
     *
     */
    public RuleSetEditingPanel()
    {
        super(new BorderLayout());

        EmptyBorder emptyBorder = new EmptyBorder(15, 15, 15, 15);

        setBorder(emptyBorder);

        JPanel panel = new JPanel(new RuleSetLayout());
        TitledBorder titledBorder = ComponentFactory.createTitledBorder("  Rule Set  ");

        panel.setBorder(titledBorder);
        add(panel, BorderLayout.CENTER);

        // Rule Set Name Label
        m_nameLabel = new JLabel("Name");
        m_nameLabel.setFont(UIManager.getFont("labelFont"));
        m_nameLabel.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(m_nameLabel);

        // Rule Set Name Text
        m_name = new JTextField();
        m_name.setFont(UIManager.getFont("dataFont"));
        panel.add(m_name);

        // Rule Set Description Label
        m_descriptionLabel = new JLabel("Description");
        m_descriptionLabel.setFont(UIManager.getFont("labelFont"));
        m_descriptionLabel.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(m_descriptionLabel);

        // Rule Set Description Text
        m_description = ComponentFactory.createTextArea("");

        // Rule Set Description Scroll Pane;
        m_descriptionScrollPane = ComponentFactory.createScrollPane(m_description);
        panel.add(m_descriptionScrollPane);

        // Rule Set Active Label
        m_includeLabel = new JLabel("Active");
        m_includeLabel.setFont(UIManager.getFont("labelFont"));
        m_includeLabel.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(m_includeLabel);

        // Rule Set Active
        m_include = new JCheckBox("");
        panel.add(m_include);

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
            setData(data);
        }
        else if (data.isRule())
        {
            setData(data.getParentRuleSetData());
        }
        else if (data.isProperty())
        {
            setData(data.getParentRuleSetData());
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
    protected void saveData()
    {
        if (m_currentData != null)
        {
            m_currentData.setName(m_name.getText());
            m_currentData.setDescription(m_description.getText());
            m_currentData.setInclude(m_include.isSelected());
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
                m_description.setEnabled(true);
                m_description.setBackground(Color.white);
                m_include.setEnabled(true);
                m_enabled = true;
            }

            m_name.setText(data.getName());
            m_description.setText(data.getDescription());
            m_include.setSelected(data.include());

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

        m_description.setText("");
        m_description.setEnabled(false);
        m_description.setBackground(background);

        m_include.setSelected(false);
        m_include.setEnabled(false);

        m_currentData = null;
        m_enabled = false;
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class RuleSetLayout implements LayoutManager
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

            // Calculate Description Label
            int descriptionLabelWidth;
            int descriptionLabelHeight;

            font = m_descriptionLabel.getFont();
            fontMetrics = m_descriptionLabel.getFontMetrics(font);
            descriptionLabelWidth = fontMetrics.stringWidth(m_descriptionLabel.getText());
            descriptionLabelHeight = fontMetrics.getHeight();

            // Calculate Active Label
            int activeLabelWidth;
            int activeLabelHeight;

            font = m_includeLabel.getFont();
            fontMetrics = m_includeLabel.getFontMetrics(font);
            activeLabelWidth = fontMetrics.stringWidth(m_includeLabel.getText());
            activeLabelHeight = fontMetrics.getHeight();

            // Calculate first column width.
            int firstColumnWidth = nameLabelWidth;

            if (descriptionLabelWidth > firstColumnWidth)
            {
                firstColumnWidth = descriptionLabelWidth;
            }

            if (activeLabelWidth > firstColumnWidth)
            {
                firstColumnWidth = activeLabelWidth;
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

            // Calculate Description Field
            int descriptionWidth;
            int descriptionHeight;
            Border border;

            font = m_descriptionScrollPane.getFont();
            fontMetrics = m_descriptionScrollPane.getFontMetrics(font);
            border = m_descriptionScrollPane.getBorder();
            insets = border.getBorderInsets(m_descriptionScrollPane);
            descriptionWidth = insets.left + insets.right;
            descriptionHeight = fontMetrics.getHeight() * 7 + insets.top + insets.bottom;

            // Calculate Active CheckBox
            Icon checkBoxIcon;
            int activeWidth;
            int activeHeight;

            checkBoxIcon = m_include.getIcon();

            if (checkBoxIcon != null)
            {
                activeWidth = checkBoxIcon.getIconWidth();
                activeHeight = checkBoxIcon.getIconHeight();
            }
            else
            {
                activeWidth = 16;
                activeHeight = 16;
            }

            // Calculate second column width.
            int secondColumnWidth = containerSize.width
                                  - containerInsets.left
                                  - containerInsets.right
                                  - firstColumnWidth
                                  - columnSpacing;

            // Calculate Line Heights
            int firstLineHeight = (nameHeight > nameLabelHeight) ? nameHeight : nameLabelHeight;
            int secondLineHeight = (descriptionHeight > descriptionLabelHeight)
                                 ? descriptionHeight
                                 : descriptionLabelHeight;
            int thirdLineHeight = (activeLabelHeight > activeHeight)
                                ? activeLabelHeight
                                : activeHeight;

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

            // Layout Description Label
            x = containerInsets.left;
            y = containerInsets.top + firstLineHeight + lineSpacing;
            m_descriptionLabel.setBounds(x, y, firstColumnWidth, descriptionLabelHeight);

            // Layout Description
            x = containerInsets.left + firstColumnWidth + columnSpacing;
            y = containerInsets.top + firstLineHeight + lineSpacing;
            m_descriptionScrollPane.setBounds(x, y, secondColumnWidth, descriptionHeight);

            // Layout Active Label
            x = containerInsets.left;
            y = containerInsets.top + firstLineHeight + secondLineHeight + (lineSpacing * 2);
            yOffset = (thirdLineHeight - activeLabelHeight) / 2;
            m_includeLabel.setBounds(x, y + yOffset, firstColumnWidth, activeLabelHeight);

            // Layout Active Checkbox
            x = containerInsets.left + firstColumnWidth + columnSpacing;
            y = containerInsets.top + firstLineHeight + secondLineHeight + (lineSpacing * 2);
            yOffset = (thirdLineHeight - activeHeight) / 2;
            m_include.setBounds(x, y + yOffset, activeWidth, activeHeight);

            return null;
        }
    }
}