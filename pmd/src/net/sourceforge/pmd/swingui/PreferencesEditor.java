package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Rule;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

/*
*/

/**
 *
 * @author Donald A. Leckie
 * @since September 8, 2002
 * @version $Revision$, $Date$
 */
class PreferencesEditor extends JPanel
{
    private JTextArea m_currentPathToPMD;
    private JTextArea m_userPathToPMD;
    private JTextArea m_sharedPathToPMD;
    private JTextArea m_analysisResultsPath;
    private JComboBox m_lowestPriorityForAnalysis;
    private JMenuBar m_menuBar;

    /**
     ********************************************************************************
     *
     * @pmdViewer
     */
    protected PreferencesEditor() throws PMDException
    {
        super(new BorderLayout());

        add(createContentPanel(), BorderLayout.CENTER);
        createMenuBar();
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    private JScrollPane createContentPanel() throws PMDException
    {
        JPanel contentPanel = new JPanel(new BorderLayout());
        EmptyBorder emptyBorder = new EmptyBorder(100, 100, 100, 100);
        EtchedBorder etchedBorder = new EtchedBorder(EtchedBorder.LOWERED);
        CompoundBorder compoundBorder = new CompoundBorder(etchedBorder, emptyBorder);
        contentPanel.setBorder(compoundBorder);
        contentPanel.add(createDataPanel(), BorderLayout.NORTH);

        return ComponentFactory.createScrollPane(contentPanel);
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    private JPanel createDataPanel() throws PMDException
    {
        JPanel dataPanel;
        int row;
        Preferences preferences;
        EmptyBorder emptyBorder;
        EtchedBorder etchedBorder;
        CompoundBorder compoundBorder;

        dataPanel = new JPanel(new GridBagLayout());
        emptyBorder = new EmptyBorder(1, 1, 1, 1);
        etchedBorder = new EtchedBorder(EtchedBorder.RAISED);
        compoundBorder = new CompoundBorder(etchedBorder, emptyBorder);
        compoundBorder = new CompoundBorder(compoundBorder, etchedBorder);
        emptyBorder = new EmptyBorder(10, 10, 10, 10);
        compoundBorder = new CompoundBorder(compoundBorder, emptyBorder);
        dataPanel.setBorder(compoundBorder);
        preferences = Preferences.getPreferences();

        row = 0;
        createLabel("Current Path to PMD Directory", dataPanel, row, 0);
        String currentPath = preferences.getCurrentPathToPMD();
        m_currentPathToPMD = createTextArea(currentPath, dataPanel, row, 1);
        createFileButton(dataPanel, row, 2, m_currentPathToPMD);

        row++;
        createLabel("User Path to PMD Directory", dataPanel, row, 0);
        String userPath = preferences.getUserPathToPMD();
        m_userPathToPMD = createTextArea(userPath, dataPanel, row, 1);
        createFileButton(dataPanel, row, 2, m_userPathToPMD);

        row++;
        createLabel("Shared Path to PMD Directory", dataPanel, row, 0);
        String sharedPath = preferences.getSharedPathToPMD();
        m_sharedPathToPMD = createTextArea(sharedPath, dataPanel, row, 1);
        createFileButton(dataPanel, row, 2, m_sharedPathToPMD);

        row++;
        createLabel("Analysis Results Files Path", dataPanel, row, 0);
        String analysisResultsPath = preferences.getAnalysisResultsPath();
        m_analysisResultsPath = createTextArea(analysisResultsPath, dataPanel, row, 1);
        createFileButton(dataPanel, row, 2, m_analysisResultsPath);

        row++;
        createLabel("Lowest Priority for Analysis", dataPanel, row, 0);
        int priority = preferences.getLowestPriorityForAnalysis();
        m_lowestPriorityForAnalysis = createPriorityDropDownList(priority, dataPanel, row, 1);

        return dataPanel;
    }

    /**
     *******************************************************************************
     *
     * @param text
     * @param dataPanel
     * @param row
     * @param column
     */
    private void createLabel(String text, JPanel dataPanel, int row, int column)
    {
        JLabel label = new JLabel(text);
        label.setFont(UIManager.getFont("labelFont"));
        label.setHorizontalAlignment(JLabel.RIGHT);
        label.setForeground(UIManager.getColor("pmdBlue"));

        GridBagLayout layout;
        GridBagConstraints constraints;

        layout = (GridBagLayout) dataPanel.getLayout();
        constraints = layout.getConstraints(label);
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = constraints.NORTHEAST;
        constraints.fill = constraints.NONE;
        constraints.insets = new Insets(2, 2, 2, 2);

        dataPanel.add(label, constraints);
    }

    /**
     *******************************************************************************
     *
     * @param text
     * @param dataPanel
     * @param row
     * @param column
     */
    private JTextArea createTextArea(String text, JPanel dataPanel, int row, int column)
    {
        JTextArea textArea;
        JScrollPane scrollPane;
        GridBagLayout layout;
        GridBagConstraints constraints;
        Font font;
        FontMetrics fontMetrics;
        int height;
        int width;
        Dimension size;

        textArea = ComponentFactory.createTextArea(text);

        scrollPane = ComponentFactory.createScrollPane(textArea);
        font = textArea.getFont();
        fontMetrics = textArea.getFontMetrics(font);
        width = 400;
        height = (3 * fontMetrics.getHeight()) + 5;
        size = new Dimension(width, height);
        scrollPane.setSize(size);
        scrollPane.setMinimumSize(size);
        scrollPane.setPreferredSize(size);

        layout = (GridBagLayout) dataPanel.getLayout();
        constraints = layout.getConstraints(scrollPane);
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = constraints.WEST;
        constraints.fill = constraints.BOTH;
        constraints.insets = new Insets(2, 2, 2, 2);

        dataPanel.add(scrollPane, constraints);

        return textArea;
    }

    /**
     *******************************************************************************
     *
     * @param dataPanel
     * @param row
     * @param column
     */
    private void createFileButton(JPanel dataPanel, int row, int column, JTextArea textArea)
    {
        JButton button;
        GridBagLayout layout;
        GridBagConstraints constraints;
        FontMetrics fontMetrics;
        int width;
        Dimension size;

        button = ComponentFactory.createButton("Find Directory");
        fontMetrics = button.getFontMetrics(button.getFont());
        width = fontMetrics.stringWidth(button.getText()) + 50;
        size = new Dimension(width, button.getHeight());
        button.setSize(size);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);
        button.setBackground(UIManager.getColor("pmdBlue"));
        button.setForeground(Color.white);
        button.addActionListener(new FileButtonActionListener(textArea));
        layout = (GridBagLayout) dataPanel.getLayout();
        constraints = layout.getConstraints(button);
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = constraints.WEST;
        constraints.fill = constraints.NONE;
        constraints.insets = new Insets(2, 2, 2, 2);

        dataPanel.add(button, constraints);
    }

    /**
     *******************************************************************************
     *
     */
    private JComboBox createPriorityDropDownList(int priority, JPanel dataPanel, int row, int column)
    {
        JComboBox priorityLevel;
        GridBagLayout layout;
        GridBagConstraints constraints;

        priorityLevel = new JComboBox(Rule.PRIORITIES);
        priorityLevel.setSelectedIndex(priority - 1);

        layout = (GridBagLayout) dataPanel.getLayout();
        constraints = layout.getConstraints(priorityLevel);
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = constraints.WEST;
        constraints.fill = constraints.NONE;
        constraints.insets = new Insets(2, 2, 2, 2);

        dataPanel.add(priorityLevel, constraints);

        return priorityLevel;
    }

    /**
     *********************************************************************************
     *
     */
    private void createMenuBar()
    {
       m_menuBar = new JMenuBar();
       m_menuBar.add(new FileMenu());
       m_menuBar.add(new HelpMenu());
    }

    /**
     *********************************************************************************
     *
     */
    protected void setMenuBar()
    {
        PMDViewer.getViewer().setJMenuBar(m_menuBar);
    }

    /**
     *********************************************************************************
     *
     */
    public void adjustSplitPaneDividerLocation()
    {
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class SaveActionListener implements ActionListener
    {

        /**
         ********************************************************************
         *
         * @param event
         */
        public void actionPerformed(ActionEvent event)
        {
            try
            {
                Preferences preferences = Preferences.getPreferences();
                preferences.setCurrentPathToPMD(m_currentPathToPMD.getText());
                preferences.setUserPathToPMD(m_userPathToPMD.getText());
                preferences.setSharedPathToPMD(m_sharedPathToPMD.getText());
                preferences.setLowestPriorityForAnalysis(m_lowestPriorityForAnalysis.getSelectedIndex() + 1);
                preferences.save();
            }
            catch (PMDException pmdException)
            {
                String message = pmdException.getMessage();
                Exception exception = pmdException.getReason();
                MessageDialog.show(PMDViewer.getViewer(), message, exception);
            }

            PreferencesEditor.this.setVisible(false);
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class CancelButtonActionListener implements ActionListener
    {

        /**
         ********************************************************************
         *
         * @param event
         */
        public void actionPerformed(ActionEvent event)
        {
            PreferencesEditor.this.setVisible(false);
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class FileButtonActionListener implements ActionListener
    {

        private JTextArea m_textArea;

        /**
         **************************************************************************
         *
         * @param directory
         */
        private FileButtonActionListener(JTextArea textArea)
        {
            m_textArea = textArea;
        }

        /**
         ********************************************************************
         *
         * @param event
         */
        public void actionPerformed(ActionEvent event)
        {
            File file = new File(m_textArea.getText());

            if (file.exists() == false)
            {
                file = new File(System.getProperty("user.home"));
            }
            else if (file.isDirectory() == false)
            {
                file = file.getParentFile();
            }

            JFileChooser fileChooser = new JFileChooser(file);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setApproveButtonText("Select");
            fileChooser.setMinimumSize(new Dimension(500, 500));

            if (fileChooser.showOpenDialog(PMDViewer.getViewer()) == JFileChooser.APPROVE_OPTION)
            {
                file = fileChooser.getSelectedFile();

                m_textArea.setText(file.getPath());
            }
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class FileMenu extends JMenu
    {

        /**
         ********************************************************************
         *
         * @param menuBar
         */
        private FileMenu()
        {
            super("File");

            setMnemonic('F');

            Icon icon;
            JMenuItem menuItem;

            //
            // Save menu item
            //
            icon = UIManager.getIcon("save");
            menuItem = new JMenuItem("Save Changes", icon);
            menuItem.addActionListener((ActionListener) new SaveActionListener());
            menuItem.setMnemonic('S');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
            add(menuItem);

            //
            // Separator
            //
            add(new JSeparator());

            //
            // Exit menu item
            //
            menuItem = new JMenuItem("Exit...");
            menuItem.addActionListener((ActionListener) new ExitActionListener());
            menuItem.setMnemonic('X');
            add(menuItem);
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class ExitActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
            System.exit(0);
        }
    }
}