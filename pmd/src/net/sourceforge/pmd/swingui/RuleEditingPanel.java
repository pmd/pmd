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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.tree.TreePath;
import javax.swing.UIManager;

import net.sourceforge.pmd.Rule;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
public class RuleEditingPanel extends JPanel implements TreeSelectionListener
{

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
    private JLabel m_includeLabel;
    private JCheckBox m_include;
    private boolean m_enabled;

    /**
     *******************************************************************************
     *
     * @return
     */
    public RuleEditingPanel()
    {
        super(new BorderLayout());

        EmptyBorder emptyBorder = new EmptyBorder(15, 15, 15, 15);

        setBorder(emptyBorder);

        JPanel panel;
        TitledBorder titledBorder;

        panel = new JPanel(new RuleLayout());
        titledBorder = ComponentFactory.createTitledBorder("  Rule  ");

        panel.setBorder(titledBorder);
        add(panel, BorderLayout.CENTER);

        // Rule Name Label
        m_nameLabel = new JLabel("Name");
        m_nameLabel.setFont(UIManager.getFont("labelFont"));
        m_nameLabel.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(m_nameLabel);

        // Rule Name Text
        m_name = new JTextField();
        m_name.setFont(UIManager.getFont("dataFont"));
        panel.add(m_name);

        // Rule Class Name Label
        m_classNameLabel = new JLabel("Class Name");
        m_classNameLabel.setFont(UIManager.getFont("labelFont"));
        m_classNameLabel.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(m_classNameLabel);

        // Rule Class Name Text
        m_className = new JTextField();
        m_className.setFont(UIManager.getFont("dataFont"));
        panel.add(m_className);

        // Rule Message Label
        m_messageLabel = new JLabel("Message");
        m_messageLabel.setFont(UIManager.getFont("labelFont"));
        m_messageLabel.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(m_messageLabel);

        // Rule Message Text
        m_message = ComponentFactory.createTextArea();

        // Rule Message Scroll Pane;
        m_messageScrollPane = ComponentFactory.createScrollPane(m_message);
        panel.add(m_messageScrollPane);

        // Rule Description Label
        m_descriptionLabel = new JLabel("Description");
        m_descriptionLabel.setFont(UIManager.getFont("labelFont"));
        m_descriptionLabel.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(m_descriptionLabel);

        // Rule Description Text
        m_description = ComponentFactory.createTextArea();

        // Rule Description Scroll Pane;
        m_descriptionScrollPane = ComponentFactory.createScrollPane(m_description);
        panel.add(m_descriptionScrollPane);

        // Rule Example Label
        m_exampleLabel = new JLabel("Example");
        m_exampleLabel.setFont(UIManager.getFont("labelFont"));
        m_exampleLabel.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(m_exampleLabel);

        // Rule Example Text
        m_example = ComponentFactory.createTextArea();
        m_example.setFont(UIManager.getFont("codeFont"));

        // Rule Example Scroll Pane;
        m_exampleScrollPane = ComponentFactory.createScrollPane(m_example);
        panel.add(m_exampleScrollPane);

        // Rule Set Active Label
        m_includeLabel = new JLabel("Include");
        m_includeLabel.setFont(UIManager.getFont("labelFont"));
        m_includeLabel.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(m_includeLabel);

        // Rule Set Active
        m_include = new JCheckBox("");
        panel.add(m_include);
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

        if (component instanceof IRulesEditingData)
        {
            IRulesEditingData data = (IRulesEditingData) component;

            if (data.isRuleSet())
            {
                disableData();
            }
            else if (data.isRule())
            {
                setData(data);
            }
            else if (data.isProperty())
            {
                setData(data.getParentRuleData());
            }
            else
            {
                disableData();
            }
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
                m_message.setEnabled(true);
                m_message.setBackground(Color.white);
                m_description.setEnabled(true);
                m_description.setBackground(Color.white);
                m_example.setEnabled(true);
                m_example.setBackground(Color.white);
                m_include.setEnabled(true);
            }

            m_name.setText(data.getName());
            m_message.setText(data.getMessage());
            m_description.setText(data.getDescription());
            m_example.setText(data.getExample());
            m_include.setSelected(data.include());

            m_enabled = true;
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

        m_message.setText("");
        m_message.setEnabled(false);
        m_message.setBackground(background);

        m_description.setText("");
        m_description.setEnabled(false);
        m_description.setBackground(background);

        m_example.setText("");
        m_example.setEnabled(false);
        m_example.setBackground(background);

        m_include.setSelected(false);
        m_include.setEnabled(false);

        m_enabled = false;
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class RuleLayout implements LayoutManager
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
            return parent.getSize();
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

            // Calculate Class Name Label
            int classNameLabelWidth;
            int classNameLabelHeight;

            font = m_classNameLabel.getFont();
            fontMetrics = m_classNameLabel.getFontMetrics(font);
            classNameLabelWidth = fontMetrics.stringWidth(m_classNameLabel.getText());
            classNameLabelHeight = fontMetrics.getHeight();

            // Calculate Message Label
            int messageLabelWidth;
            int messageLabelHeight;

            font = m_messageLabel.getFont();
            fontMetrics = m_messageLabel.getFontMetrics(font);
            messageLabelWidth = fontMetrics.stringWidth(m_messageLabel.getText());
            messageLabelHeight = fontMetrics.getHeight();

            // Calculate Description Label
            int descriptionLabelWidth;
            int descriptionLabelHeight;

            font = m_descriptionLabel.getFont();
            fontMetrics = m_descriptionLabel.getFontMetrics(font);
            descriptionLabelWidth = fontMetrics.stringWidth(m_descriptionLabel.getText());
            descriptionLabelHeight = fontMetrics.getHeight();

            // Calculate Example Label
            int exampleLabelWidth;
            int exampleLabelHeight;

            font = m_exampleLabel.getFont();
            fontMetrics = m_exampleLabel.getFontMetrics(font);
            exampleLabelWidth = fontMetrics.stringWidth(m_exampleLabel.getText());
            exampleLabelHeight = fontMetrics.getHeight();

            // Calculate Active Label
            int activeLabelWidth;
            int activeLabelHeight;

            font = m_includeLabel.getFont();
            fontMetrics = m_includeLabel.getFontMetrics(font);
            activeLabelWidth = fontMetrics.stringWidth(m_includeLabel.getText());
            activeLabelHeight = fontMetrics.getHeight();

            // Calculate first column width.
            int firstColumnWidth = nameLabelWidth;

            if (classNameLabelWidth > firstColumnWidth)
            {
                firstColumnWidth = classNameLabelWidth;
            }

            if (messageLabelWidth > firstColumnWidth)
            {
                firstColumnWidth = messageLabelWidth;
            }

            if (descriptionLabelWidth > firstColumnWidth)
            {
                firstColumnWidth = descriptionLabelWidth;
            }

            if (exampleLabelWidth > firstColumnWidth)
            {
                firstColumnWidth = exampleLabelWidth;
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

            // Calculate Class Name Field
            int classNameWidth;
            int classNameHeight;

            font = m_name.getFont();
            fontMetrics = m_className.getFontMetrics(font);
            insets = m_className.getBorder().getBorderInsets(m_className);
            classNameWidth = insets.left + insets.right;
            classNameHeight = fontMetrics.getHeight() + insets.top + insets.bottom;

            // Calculate Message Field
            int messageWidth;
            int messageHeight;
            Border border;

            font = m_messageScrollPane.getFont();
            fontMetrics = m_messageScrollPane.getFontMetrics(font);
            border = m_messageScrollPane.getBorder();
            insets = border.getBorderInsets(m_messageScrollPane);
            messageWidth = insets.left + insets.right;
            messageHeight = fontMetrics.getHeight() * 3 + insets.top + insets.bottom;

            // Calculate Description Field
            int descriptionWidth;
            int descriptionHeight;

            font = m_descriptionScrollPane.getFont();
            fontMetrics = m_descriptionScrollPane.getFontMetrics(font);
            border = m_descriptionScrollPane.getBorder();
            insets = border.getBorderInsets(m_descriptionScrollPane);
            descriptionWidth = insets.left + insets.right;
            descriptionHeight = fontMetrics.getHeight() * 7 + insets.top + insets.bottom;

            // Calculate Example Field
            int exampleWidth;
            int exampleHeight;

            font = m_exampleScrollPane.getFont();
            fontMetrics = m_exampleScrollPane.getFontMetrics(font);
            border = m_exampleScrollPane.getBorder();
            insets = border.getBorderInsets(m_exampleScrollPane);
            exampleWidth = insets.left + insets.right;
            exampleHeight = fontMetrics.getHeight() * 20 + insets.top + insets.bottom;

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
            int secondLineHeight = (classNameHeight > classNameHeight)
                                 ? classNameHeight
                                 : classNameLabelHeight;
            int thirdLineHeight = (messageHeight > messageLabelHeight)
                                 ? messageHeight
                                 : messageLabelHeight;
            int fourthLineHeight = (descriptionHeight > descriptionLabelHeight)
                                ? descriptionHeight
                                : descriptionLabelHeight;
            int fifthLineHeight = (exampleHeight > exampleLabelHeight)
                                 ? exampleHeight
                                 : exampleLabelHeight;
            int sixthLineHeight = (activeLabelHeight > activeHeight)
                                ? activeLabelHeight
                                : activeHeight;

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
            yOffset = (firstLineHeight - nameHeight) / 2;
            m_name.setBounds(x, y + yOffset, secondColumnWidth, nameHeight);

            // Layout Class Name Label
            x = containerInsets.left;
            y += firstLineHeight + lineSpacing;
            yOffset = (firstLineHeight - classNameLabelHeight) / 2;
            m_classNameLabel.setBounds(x, y + yOffset, firstColumnWidth, classNameLabelHeight);

            // Layout Class Name
            x = containerInsets.left + firstColumnWidth + columnSpacing;
            yOffset = (firstLineHeight - classNameHeight) / 2;
            m_className.setBounds(x, y + yOffset, secondColumnWidth, classNameHeight);

            // Layout Message Label
            x = containerInsets.left;
            y += secondLineHeight + lineSpacing;
            m_messageLabel.setBounds(x, y, firstColumnWidth, messageLabelHeight);

            // Layout Message
            x = containerInsets.left + firstColumnWidth + columnSpacing;
            m_messageScrollPane.setBounds(x, y, secondColumnWidth, messageHeight);

            // Layout Description Label
            x = containerInsets.left;
            y += thirdLineHeight + lineSpacing;
            m_descriptionLabel.setBounds(x, y, firstColumnWidth, descriptionLabelHeight);

            // Layout Description
            x = containerInsets.left + firstColumnWidth + columnSpacing;
            m_descriptionScrollPane.setBounds(x, y, secondColumnWidth, descriptionHeight);

            // Layout Example Label
            x = containerInsets.left;
            y += fourthLineHeight + lineSpacing;
            m_exampleLabel.setBounds(x, y, firstColumnWidth, exampleLabelHeight);

            // Layout Example
            x = containerInsets.left + firstColumnWidth + columnSpacing;
            m_exampleScrollPane.setBounds(x, y, secondColumnWidth, exampleHeight);

            // Layout Active Label
            x = containerInsets.left;
            y += fifthLineHeight + lineSpacing;
            yOffset = (sixthLineHeight - activeLabelHeight) / 2;
            m_includeLabel.setBounds(x, y + yOffset, firstColumnWidth, activeLabelHeight);

            // Layout Active Checkbox
            x = containerInsets.left + firstColumnWidth + columnSpacing;
            yOffset = (sixthLineHeight - activeHeight) / 2;
            m_include.setBounds(x, y + yOffset, activeWidth, activeHeight);
        }
    }
}