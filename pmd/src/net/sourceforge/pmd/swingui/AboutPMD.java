package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.ProjectFile;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Donald A. Leckie
 * @since September 6, 2002
 * @version $Revision$, $Date$
 */
class AboutPMD extends JDialog {

    /**
     ********************************************************************************
     *
     * @pmdViewer
     */
    protected AboutPMD(PMDViewer pmdViewer) {
        super(pmdViewer, "About PMD", true);

        initialize();
    }

    /**
     ********************************************************************************
     *
     * @pmdViewer
     */
    protected AboutPMD(JDialog dialog) {
        super(dialog, "About PMD", true);

        initialize();
    }

    /**
     ********************************************************************************
     */
    private void initialize() {
        Dimension screenSize = getToolkit().getScreenSize();
        int windowWidth = 750;
        int windowHeight = 500;
        int windowLocationX = (screenSize.width - windowWidth) / 2;
        int windowLocationY = (screenSize.height - windowHeight) / 2;

        setLocation(windowLocationX, windowLocationY);
        setSize(windowWidth, windowHeight);
        setResizable(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contentPanel = new JPanel(new BorderLayout());
        EmptyBorder emptyBorder = new EmptyBorder(10, 10, 10, 10);
        contentPanel.setBorder(emptyBorder);
        contentPanel.add(createTabbedPane(), BorderLayout.CENTER);
        contentPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        JScrollPane scrollPane = ComponentFactory.createScrollPane(contentPanel);
        getContentPane().add(scrollPane);
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    private JPanel createButtonPanel() {
        JButton closeButton = new JButton("Close");
        closeButton.setForeground(Color.white);
        closeButton.setBackground(UIManager.getColor("pmdBlue"));
        closeButton.addActionListener(new CloseButtonActionListener());

        JPanel buttonPanel = ComponentFactory.createButtonPanel();
        buttonPanel.add(closeButton);

        return buttonPanel;
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);

        tabbedPane.addTab("About", createAboutPanel());
        tabbedPane.addTab("Info", createInfoPanel());
        tabbedPane.addTab("Credits", createCreditsPanel());
        tabbedPane.setFont(UIManager.getFont("tabFont"));

        return tabbedPane;
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    private JPanel createAboutPanel() {
        JPanel aboutPanel = new JPanel(new BorderLayout());

        // PMD Image
        ImageIcon imageIcon = (ImageIcon) UIManager.get("pmdLogoImage");
        JLabel imageLabel = new JLabel(imageIcon);
        aboutPanel.add(imageLabel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        bottomPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        aboutPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Version Label
        String versionText = Resources.getString("RESOURCEVersion") + " " + ProjectFile.getProperty("currentVersion");
        JLabel versionLabel = new JLabel(versionText);
        versionLabel.setFont(UIManager.getFont("labelFont"));
        versionLabel.setHorizontalAlignment(JLabel.CENTER);
        bottomPanel.add(versionLabel);

        // SourceForge PMD Project
        String sourceForgeText = Resources.getString("RESOURCEDevelopedBySourceForgePMDTeam");
        JLabel sourceForgeLabel = new JLabel(sourceForgeText);
        sourceForgeLabel.setFont(UIManager.getFont("labelFont"));
        sourceForgeLabel.setHorizontalAlignment(JLabel.CENTER);
        bottomPanel.add(sourceForgeLabel);

        return aboutPanel;
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    private JPanel createInfoPanel() {
        GridBagLayout layout = new GridBagLayout();
        JPanel infoPanel = new JPanel(layout);
        int row = 0;

        addName("Java Runtime Environment Version", row, infoPanel);
        addValue(System.getProperty("java.version"), row, infoPanel);

        row++;
        addName("Java Runtime Environment Vendor", row, infoPanel);
        addValue(System.getProperty("java.vendor"), row, infoPanel);

        row++;
        addName("Java Installation Directory", row, infoPanel);
        addValue(System.getProperty("java.home"), row, infoPanel);

        row++;
        addName("Java ClassPath", row, infoPanel);
        addMultiLineValue(System.getProperty("java.class.path"), row, 5, infoPanel);

        row += 5;
        addName("Operating System Name", row, infoPanel);
        addValue(System.getProperty("os.name"), row, infoPanel);

        row++;
        addName("Operating System Architecture", row, infoPanel);
        addValue(System.getProperty("os.arch"), row, infoPanel);

        row++;
        addName("Operating System Version", row, infoPanel);
        addValue(System.getProperty("os.version"), row, infoPanel);

        row++;
        addName("User's Home Directory", row, infoPanel);
        addValue(System.getProperty("user.home"), row, infoPanel);

        row++;
        addName("User's Current Working Director", row, infoPanel);
        addValue(System.getProperty("user.dir"), row, infoPanel);

        row++;
        addName("VM Total Memory", row, infoPanel);
        long totalMemory = Runtime.getRuntime().totalMemory() / 1024;
        String totalMemoryText = DecimalFormat.getNumberInstance().format(totalMemory) + "KB";
        addValue(totalMemoryText, row, infoPanel);

        row++;
        addName("VM Free Memory", row, infoPanel);
        long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
        String freeMemoryText = DecimalFormat.getNumberInstance().format(freeMemory) + "KB";
        addValue(freeMemoryText, row, infoPanel);

        row++;
        addName("VM Used Memory", row, infoPanel);
        long usedMemory = totalMemory - freeMemory;
        String usedMemoryText = DecimalFormat.getNumberInstance().format(usedMemory) + "KB";
        addValue(usedMemoryText, row, infoPanel);

        return infoPanel;
    }

    /**
     ********************************************************************************
     *
     * @param name
     */
    private void addName(String name, int row, JPanel infoPanel) {
        JLabel label;
        GridBagLayout layout;
        GridBagConstraints constraints;

        label = new JLabel(name, JLabel.RIGHT);
        label.setFont(UIManager.getFont("labelFont"));
        label.setHorizontalAlignment(JLabel.RIGHT);
        label.setForeground(UIManager.getColor("pmdBlue"));
        layout = (GridBagLayout) infoPanel.getLayout();
        constraints = layout.getConstraints(label);
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = constraints.NORTHEAST;
        constraints.fill = constraints.NONE;
        constraints.insets = new Insets(2, 2, 2, 2);

        infoPanel.add(label, constraints);
    }

    /**
     ********************************************************************************
     *
     * @param value
     */
    private void addValue(String value, int row, JPanel infoPanel) {
        JLabel label;
        GridBagLayout layout;
        GridBagConstraints constraints;

        label = new JLabel(value, JLabel.LEFT);
        label.setFont(UIManager.getFont("dataFont"));
        layout = (GridBagLayout) infoPanel.getLayout();
        constraints = layout.getConstraints(label);
        constraints.gridx = 1;
        constraints.gridy = row;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = constraints.WEST;
        constraints.fill = constraints.NONE;
        constraints.insets = new Insets(2, 2, 2, 2);

        infoPanel.add(label, constraints);
    }

    /**
     ********************************************************************************
     *
     * @param value
     */
    private void addMultiLineValue(String value, int row, int lines, JPanel infoPanel) {
        JTextArea textArea;
        JScrollPane scrollPane;
        GridBagLayout layout;
        GridBagConstraints constraints;
        Font font;
        FontMetrics fontMetrics;
        int height;
        int width;
        Dimension size;

        textArea = ComponentFactory.createTextArea(value);
        textArea.setBackground(Color.lightGray);

        scrollPane = ComponentFactory.createScrollPane(textArea);
        font = textArea.getFont();
        fontMetrics = textArea.getFontMetrics(font);
        width = 500;
        height = (lines * fontMetrics.getHeight()) + 5;
        size = new Dimension(width, height);
        scrollPane.setSize(size);
        scrollPane.setMinimumSize(size);
        scrollPane.setPreferredSize(size);

        layout = (GridBagLayout) infoPanel.getLayout();
        constraints = layout.getConstraints(scrollPane);
        constraints.gridx = 1;
        constraints.gridy = row;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = constraints.WEST;
        constraints.fill = constraints.BOTH;
        constraints.insets = new Insets(2, 2, 2, 2);

        infoPanel.add(scrollPane, constraints);
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    private JPanel createCreditsPanel() {
        JPanel parentPanel = new JPanel(new BorderLayout());

        // Panel Title
        JLabel title;
        EtchedBorder etchedBorder;
        CompoundBorder compoundBorder;
        EmptyBorder emptyBorder;

        title = new JLabel("The SourceForge PMD Project Team");
        etchedBorder = new EtchedBorder(EtchedBorder.RAISED);
        compoundBorder = new CompoundBorder(etchedBorder, etchedBorder);
        emptyBorder = new EmptyBorder(10, 10, 10, 10);
        compoundBorder = new CompoundBorder(emptyBorder, compoundBorder);
        compoundBorder = new CompoundBorder(compoundBorder, emptyBorder);
        title.setBorder(compoundBorder);
        title.setFont(UIManager.getFont("label16Font"));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setForeground(UIManager.getColor("pmdRed"));
        parentPanel.add(title, BorderLayout.NORTH);

        // Credits Panel
        GridBagLayout layout = new GridBagLayout();
        JPanel creditsPanel = new JPanel(layout);
        parentPanel.add(creditsPanel, BorderLayout.CENTER);
        int row = 0;

        addTitle("Project Administrators", row, creditsPanel);
        addPerson("Tom Copeland", row, creditsPanel);

        row++;
        addPerson("David Craine", row, creditsPanel);

        row++;
        addPerson("David Dixon-Peugh", row, creditsPanel);

        row++;
        addTitle(" ", row, creditsPanel);

        String developerNameKey = "developers/developer/name";
        String developerSelectKey = "developers/developer/roles/role";
        String selectValue = "developer";
        String[] developers = getPeople(developerNameKey, developerSelectKey, selectValue);

        row++;
        addTitle("Developers", row, creditsPanel);
        row--;

        for (int n = 0; n < developers.length; n++) {
            row++;
            addPerson(developers[n], row, creditsPanel);
        }

        row++;
        addTitle(" ", row, creditsPanel);

        String[] contributors = getPeople("contributors/contributor/name", null, null);

        row++;
        addTitle("Contributors", row, creditsPanel);
        row--;

        for (int n = 0; n < contributors.length; n++) {
            row++;
            addPerson(contributors[n], row, creditsPanel);
        }

        return parentPanel;
    }

    /**
     ********************************************************************************
     *
     * @param name
     */
    private void addTitle(String name, int row, JPanel creditsPanel) {
        JLabel label;
        GridBagLayout layout;
        GridBagConstraints constraints;

        label = new JLabel(name, JLabel.RIGHT);
        label.setFont(UIManager.getFont("label14Font"));
        label.setHorizontalAlignment(JLabel.RIGHT);
        label.setForeground(UIManager.getColor("pmdBlue"));
        layout = (GridBagLayout) creditsPanel.getLayout();
        constraints = layout.getConstraints(label);
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = constraints.NORTHEAST;
        constraints.fill = constraints.NONE;
        constraints.insets = new Insets(0, 2, 0, 2);

        creditsPanel.add(label, constraints);
    }

    /**
     ********************************************************************************
     *
     * @param value
     */
    private void addPerson(String value, int row, JPanel creditsPanel) {
        JLabel label;
        GridBagLayout layout;
        GridBagConstraints constraints;

        label = new JLabel(value, JLabel.LEFT);
        label.setFont(UIManager.getFont("serif14Font"));
        layout = (GridBagLayout) creditsPanel.getLayout();
        constraints = layout.getConstraints(label);
        constraints.gridx = 1;
        constraints.gridy = row;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = constraints.WEST;
        constraints.fill = constraints.NONE;
        constraints.insets = new Insets(0, 2, 0, 2);

        creditsPanel.add(label, constraints);
    }

    /**
     ********************************************************************************
     *
     * @param nameKey
     * @param selectKey
     * @param selectValue
     *
     * @return
     */
    private String[] getPeople(String nameKey, String selectKey, String selectValue) {
        String nameList = ProjectFile.getProperty(nameKey);
        String[] names = ProjectFile.toArray(nameList);

        if ((selectKey != null) && (selectValue != null)) {
            String selectList = ProjectFile.getProperty(selectKey);
            String[] selections = ProjectFile.toArray(selectList);
            List tempNameList = new ArrayList();

            for (int n = 0; n < names.length; n++) {
                if ((n < selections.length) && selections[n].equalsIgnoreCase(selectValue)) {
                    tempNameList.add(names[n]);
                }

                selections[n] = null;
                names[n] = null;
            }

            names = new String[tempNameList.size()];
            tempNameList.toArray(names);
            tempNameList.clear();
        }

        Arrays.sort(names, new PeopleNameComparator());

        return names;
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class PeopleNameComparator implements Comparator {

        /**
         ********************************************************************************
         *
         * @param object1
         * @param object2
         *
         * @return
         */
        public int compare(Object object1, Object object2) {
            String name1 = (String) object1;
            String name2 = (String) object2;
            int index = name1.lastIndexOf(' ') + 1;

            if (index >= 0) {
                name1 = name1.substring(index).concat(name1);
            }

            index = name2.lastIndexOf(' ') + 1;

            if (index >= 0) {
                name2 = name2.substring(index).concat(name2);
            }

            return name1.compareToIgnoreCase(name2);
        }

        /**
         ********************************************************************************
         *
         * @param object
         *
         * @return
         */
        public boolean compare(Object object) {
            return object == this;
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class CloseButtonActionListener implements ActionListener {

        /**
         ********************************************************************
         *
         * @param event
         */
        public void actionPerformed(ActionEvent event) {
            AboutPMD.this.setVisible(false);
        }
    }
}