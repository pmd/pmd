package net.sourceforge.pmd.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.EventObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.border.EtchedBorder;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;

import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSetReader;
import net.sourceforge.pmd.RuleSetWriter;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
class RulesEditor extends JDialog
{

    private PMDViewer m_pmdViewer;
    private RulesTree m_tree;

    /**
     *******************************************************************************
     *
     * @param parentWindow
     */
    protected RulesEditor(PMDViewer pmdViewer)
        throws PMDException
    {
        super(pmdViewer, "Rules Editor", true);

        m_pmdViewer = pmdViewer;
        int windowWidth = 1200;
        int windowHeight = 900;
        int windowMargin = 10;
        Dimension screenSize = getToolkit().getScreenSize();

        if (windowWidth >= screenSize.width)
        {
            windowWidth = screenSize.width - 10;
        }

        if (windowHeight >= screenSize.height)
        {
            windowHeight = screenSize.height - 20;
        }

        int windowLocationX = (screenSize.width - windowWidth) / 2;
        int windowLocationY = (screenSize.height - windowHeight) / 2;

        setLocation(windowLocationX, windowLocationY);
        setSize(windowWidth, windowHeight);
        setResizable(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        buildTree();
        JScrollPane treeScrollPane = createTreeScrollPane();
        JTabbedPane editingTabbedPane = new RuleEditingTabbedPane(m_tree);
        JSplitPane splitPane = createSplitPane(treeScrollPane, editingTabbedPane);
        JPanel buttonPanel = createButtonPanel();

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(splitPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(contentPanel);
    }

    /**
     *******************************************************************************
     *
     */
    private void buildTree()
        throws PMDException
    {
        RuleSet[] ruleSets;
        RulesTreeNode rootNode;

        ruleSets = loadRuleSets();
        m_tree = new RulesTree();
        rootNode = (RulesTreeNode) m_tree.getModel().getRoot();

        for (int n = 0; n < ruleSets.length; n++)
        {
            RulesTreeNode ruleSetNode = new RulesTreeNode(ruleSets[n]);

            rootNode.add(ruleSetNode);
            loadRuleTreeNodes(ruleSetNode);

            ruleSets[n] = null;
        }

        m_tree.expandNode(rootNode);
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    private RuleSet[] loadRuleSets()
        throws PMDException
    {
        Preferences preferences;
        String ruleSetDirectoryName;
        File ruleSetDirectory;
        List ruleSetList;
        boolean getRegisteredRuleSets;

        preferences = m_pmdViewer.getPreferences();
        ruleSetDirectoryName = preferences.getCurrentRuleSetDirectory();
        ruleSetDirectory = new File(ruleSetDirectoryName);
        ruleSetList = new ArrayList();
        getRegisteredRuleSets = false;

        if (ruleSetDirectory.exists() == false)
        {
            ruleSetDirectory.mkdirs();
            getRegisteredRuleSets = true;
        }
        else if (ruleSetDirectory.isDirectory() == false)
        {
            String template = "The rule set directory name \"{0}\" is not a directory.  "
                            + "Verify that the directory name is correct in the Preferences Editor.";
            Object[] args = {ruleSetDirectoryName};
            String message = MessageFormat.format(template, args);
            PMDException exception = new PMDException(message);

            exception.fillInStackTrace();

            throw exception;
        }
        else
        {
            File[] ruleSetFiles = ruleSetDirectory.listFiles(new XMLFileFilter());

            if (ruleSetFiles.length == 0)
            {
                getRegisteredRuleSets = true;
            }
            else
            {
                for (int n = 0; n < ruleSetFiles.length; n++)
                {
                    FileInputStream inputStream = null;

                    try
                    {
                        RuleSetReader ruleSetReader;

                        inputStream = new FileInputStream(ruleSetFiles[n]);
                        ruleSetReader = new RuleSetReader(inputStream);

                        ruleSetList.add(ruleSetReader.read());
                    }
                    catch (FileNotFoundException exception)
                    {
                        String template = "Could not open file \"{0}\".  The file does not exist or the path may be incorrect.";
                        Object[] args = {ruleSetFiles[n].getName()};
                        String message = MessageFormat.format(template, args);
                        PMDException pmdException = new PMDException(message, exception);

                        pmdException.fillInStackTrace();

                        throw pmdException;
                    }
                    finally
                    {
                        if (inputStream != null)
                        {
                            try
                            {
                                inputStream.close();
                            }
                            catch (IOException exception)
                            {
                                exception.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        //
        // If no rule sets were found then load the registered rule sets.
        //
        if (getRegisteredRuleSets)
        {
            try
            {
                RuleSetFactory ruleSetFactory = new RuleSetFactory();
                Iterator ruleSets = ruleSetFactory.getRegisteredRuleSets();

                while (ruleSets.hasNext())
                {
                    ruleSetList.add(ruleSets.next());
                }
            }
            catch (RuleSetNotFoundException exception)
            {
                String message = "Rule Set Not Found";
                PMDException pmdException = new PMDException(message, exception);

                pmdException.fillInStackTrace();

                throw pmdException;
            }
        }

        //
        // Sort the rule sets by name in ascending order.
        //
        RuleSet[] ruleSets = new RuleSet[ruleSetList.size()];

        ruleSetList.toArray(ruleSets);
        ruleSetList.clear();
        Arrays.sort(ruleSets, new RuleSetNameComparator());

        return ruleSets;
    }

    /**
     *******************************************************************************
     *
     * @param ruleSetNode
     */
    private void loadRuleTreeNodes(RulesTreeNode ruleSetNode)
    {
        RuleSet ruleSet;
        Set setOfRules;
        Rule[] rules;

        ruleSet = ruleSetNode.getRuleSet();
        setOfRules = ruleSet.getRules();
        rules = new Rule[setOfRules.size()];

        setOfRules.toArray(rules);
        Arrays.sort(rules, new RuleNameComparator());

        for (int n = 0; n < rules.length; n++)
        {
            RulesTreeNode ruleNode = new RulesTreeNode(rules[n]);

            ruleSetNode.add(ruleNode);
            loadProperties(ruleNode);

            rules[n] = null;
        }
    }

    /**
     *******************************************************************************
     *
     * @param ruleNode
     */
    private void loadProperties(RulesTreeNode ruleNode)
    {
        Rule rule;
        Properties properties;
        String[] propertyNames;
        Enumeration keys;
        int index;

        rule = ruleNode.getRule();
        properties = rule.getProperties();
        propertyNames = new String[properties.size()];
        keys = properties.keys();
        index = 0;

        while (keys.hasMoreElements())
        {
            propertyNames[index] = (String) keys.nextElement();
            index++;
        }

        Arrays.sort(propertyNames, new PropertyNameComparator());

        for (int n = 0; n < propertyNames.length; n++)
        {
            String propertyName = propertyNames[n];
            String propertyValue = (String) properties.getProperty(propertyName);
            RulesTreeNode propertyNode = new RulesTreeNode(propertyName, propertyValue);

            ruleNode.add(propertyNode);

            propertyNames[n] = null;
        }
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    private JScrollPane createTreeScrollPane()
    {
        JScrollPane treeScrollPane = ComponentFactory.createScrollPane(m_tree);
        Color background = UIManager.getColor("pmdTreeBackground");

        treeScrollPane.getViewport().setBackground(background);

        return treeScrollPane;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    private JSplitPane createSplitPane(JScrollPane treeScrollPane, JTabbedPane editingTabbedPane)
    {
        JSplitPane splitPane = new JSplitPane();

        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(5);
        splitPane.setLeftComponent(treeScrollPane);
        splitPane.setRightComponent(editingTabbedPane);

        return splitPane;
    }

    /**
     *******************************************************************************
     *
     */
    private JPanel createButtonPanel()
    {
        ActionListener saveActionListener = new SaveButtonActionListener();
        ActionListener cancelActionListener = new CancelButtonActionListener();

        return ComponentFactory.createSaveCancelButtonPanel(saveActionListener,
                                                            cancelActionListener);
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class RuleSetNameComparator implements Comparator
    {

        /**
         ************************************************************************
         *
         * @param objectA
         * @param objectB
         *
         * @return
         */
        public int compare(Object objectA, Object objectB)
        {
            String ruleSetNameA = ((RuleSet) objectA).getName();
            String ruleSetNameB = ((RuleSet) objectB).getName();

            return ruleSetNameA.compareToIgnoreCase(ruleSetNameB);
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class RuleNameComparator implements Comparator
    {

        /**
         ************************************************************************
         *
         * @param objectA
         * @param objectB
         *
         * @return
         */
        public int compare(Object objectA, Object objectB)
        {
            String ruleNameA = ((Rule) objectA).getName();
            String ruleNameB = ((Rule) objectB).getName();

            return ruleNameA.compareToIgnoreCase(ruleNameB);
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class PropertyNameComparator implements Comparator
    {

        /**
         ************************************************************************
         *
         * @param objectA
         * @param objectB
         *
         * @return
         */
        public int compare(Object objectA, Object objectB)
        {
            String propertyNameA = (String) objectA;
            String propertyNameB = (String) objectB;

            return propertyNameA.compareToIgnoreCase(propertyNameB);
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class SaveButtonActionListener implements ActionListener
    {

        /**
         ********************************************************************
         *
         * @param event
         */
        public void actionPerformed(ActionEvent event)
        {
            RulesTreeNode rootNode = (RulesTreeNode) m_tree.getModel().getRoot();

            saveData(rootNode);

            if (saveRuleSets(rootNode))
            {
                RulesEditor.this.setVisible(false);
            }
        }

        /**
         ********************************************************************
         *
         * @param treeNode
         */
        private void saveData(RulesTreeNode treeNode)
        {
            if (treeNode.isRuleSet())
            {
                treeNode.saveRuleSetData();
            }
            else if (treeNode.isRule())
            {
                treeNode.saveRuleData();
            }
            else if (treeNode.isProperty())
            {
                treeNode.saveRulePropertyData();
            }

            Enumeration children = treeNode.children();

            while (children.hasMoreElements())
            {
                saveData((RulesTreeNode) children.nextElement());
            }
        }

        /**
         ********************************************************************
         *
         * @param treeNode
         */
        private boolean saveRuleSets(RulesTreeNode treeNode)
        {
            Preferences preferences = m_pmdViewer.getPreferences();
            String ruleSetDirectory = preferences.getCurrentRuleSetDirectory();
            Enumeration ruleSetNodes = treeNode.children();

            while (ruleSetNodes.hasMoreElements())
            {
                FileOutputStream fileOutputStream = null;
                RulesTreeNode ruleSetNode = (RulesTreeNode) ruleSetNodes.nextElement();
                RuleSet ruleSet = ruleSetNode.getRuleSet();
                String fileName = ruleSetDirectory + File.separator + ruleSet.getName();

                try
                {
                    Enumeration ruleNodes = ruleSetNode.children();

                    ruleSet.getRules().clear();

                    while (ruleNodes.hasMoreElements())
                    {
                        RulesTreeNode ruleNode = (RulesTreeNode) ruleNodes.nextElement();

                        ruleSet.addRule(ruleNode.getRule());
                    }

                    RuleSetWriter writer;

                    fileOutputStream = new FileOutputStream(fileName);
                    writer = new RuleSetWriter(fileOutputStream);

                    writer.write(ruleSet);
                }
                catch (FileNotFoundException exception)
                {
                    String template = "Could not create rule set file \"{0}\".  The file path may be incorrect.";
                    Object[] args = {fileName};
                    String message = MessageFormat.format(template, args);

                    MessageDialog.show(RulesEditor.this, message);

                    return false;
                }
                finally
                {
                    if (fileOutputStream != null)
                    {
                        try
                        {
                            fileOutputStream.close();
                        }
                        catch (IOException exception)
                        {
                            exception.printStackTrace();
                        }
                    }
                }
            }

            return true;
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
            RulesEditor.this.setVisible(false);
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class XMLFileFilter implements FileFilter
    {

        /**
         *************************************************************************
         *
         * @param file
         *
         * @return
         */
        public boolean accept(File file)
        {
            return file.getName().endsWith(".xml");
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class TreeNodeMenuPopup extends JPopupMenu
    {
        // Add rule set
        // Remove rule set
        // Activate Rule Set
        // Deactivate Rule Set
        // Add rule
        // Activate Rule
        // Deactivate Rule
        // Remove Rule
        // Add property
        // Remove property
    }
}