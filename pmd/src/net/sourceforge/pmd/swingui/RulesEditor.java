package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleProperties;
import net.sourceforge.pmd.RuleSet;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

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
    private RuleEditingTabbedPane m_editingTabbedPane;

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
        int windowWidth = pmdViewer.getWidth();
        int windowHeight = pmdViewer.getHeight();
        Dimension screenSize = getToolkit().getScreenSize();

        if (windowWidth >= screenSize.width)
        {
            windowWidth = screenSize.width - 10;
        }

        if (windowHeight >= screenSize.height)
        {
            windowHeight = screenSize.height - 20;
        }

        int windowLocationX = pmdViewer.getX();
        int windowLocationY = pmdViewer.getY();

        setLocation(windowLocationX, windowLocationY);
        setSize(windowWidth, windowHeight);
        setResizable(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        buildTree();
        JScrollPane treeScrollPane;
        JSplitPane splitPane;
        JPanel buttonPanel;

        treeScrollPane = createTreeScrollPane();
        m_editingTabbedPane = new RuleEditingTabbedPane(m_tree);
        splitPane = createSplitPane(treeScrollPane, m_editingTabbedPane);
        buttonPanel = createButtonPanel();

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
        m_tree = new RulesTree(this);
        rootNode = (RulesTreeNode) m_tree.getModel().getRoot();

        for (int n1 = 0; n1 < ruleSets.length; n1++)
        {
            RulesTreeNode ruleSetNode = new RulesTreeNode(ruleSets[n1]);

            rootNode.add(ruleSetNode);
            loadRuleTreeNodes(ruleSetNode);
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
        List ruleSetList = m_pmdViewer.getPMDDirectory().getRuleSets();

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
            RulesTreeNode ruleNode = new RulesTreeNode(ruleSetNode, rules[n]);

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
        RuleProperties properties;
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
            String propertyName;
            String propertyValue;
            String propertyValueType;
            RulesTreeNode propertyNode;

            propertyName = propertyNames[n];
            propertyValue = properties.getValue(propertyName);
            propertyValueType = properties.getValueType(propertyName);
            propertyNode = new RulesTreeNode(ruleNode, propertyName, propertyValue, propertyValueType);

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
     *
     * @return
     */
    protected PMDViewer getPMDViewer()
    {
        return m_pmdViewer;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected RuleEditingTabbedPane getEditingTabbedPane()
    {
        return m_editingTabbedPane;
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
            m_editingTabbedPane.saveData();

            RulesTreeNode rootNode = (RulesTreeNode) m_tree.getModel().getRoot();

            saveData(rootNode);
            writeRuleSets(rootNode);
            RulesEditor.this.setVisible(false);
        }

        /**
         ********************************************************************
         *
         * @param treeNode
         */
        private void saveData(RulesTreeNode treeNode)
        {
            treeNode.saveData();

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
        private void writeRuleSets(RulesTreeNode rootNode)
        {
            List ruleSetList = new ArrayList();
            Enumeration ruleSetNodes = rootNode.children();

            while (ruleSetNodes.hasMoreElements())
            {
                RulesTreeNode ruleSetNode = (RulesTreeNode) ruleSetNodes.nextElement();
                RuleSet ruleSet = ruleSetNode.getRuleSet();
                Enumeration ruleNodes = ruleSetNode.children();

                ruleSetList.add(ruleSet);
                ruleSet.getRules().clear();

                while (ruleNodes.hasMoreElements())
                {
                    RulesTreeNode ruleNode = (RulesTreeNode) ruleNodes.nextElement();

                    ruleSet.addRule(ruleNode.getRule());
                }
            }

            m_pmdViewer.getPMDDirectory().saveRuleSets(ruleSetList);
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
}