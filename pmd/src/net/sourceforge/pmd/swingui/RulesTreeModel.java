package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleProperties;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.swingui.event.ListenerList;
import net.sourceforge.pmd.swingui.event.PMDDirectoryRequestEvent;
import net.sourceforge.pmd.swingui.event.PMDDirectoryReturnedEvent;
import net.sourceforge.pmd.swingui.event.PMDDirectoryReturnedEventListener;
import net.sourceforge.pmd.swingui.event.RulesTreeModelEvent;
import net.sourceforge.pmd.swingui.event.RulesTreeModelEventListener;

import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Donald A. Leckie
 * @since December 13, 2002
 * @version $Revision$, $Date$
 */
class RulesTreeModel extends DefaultTreeModel {

    private PMDDirectoryReturnedEventHandler m_pmdDirectoryReturnedEventHandler;
    private RulesTreeModelEventHandler m_rulesTreeModelEventHandler;
    private static RulesTreeModel m_rulesTreeModel;

    /**
     ****************************************************************************
     *
     * @param rootNode
     */
    private RulesTreeModel(RulesTreeNode rootNode) {
        super(rootNode);

        m_pmdDirectoryReturnedEventHandler = new PMDDirectoryReturnedEventHandler();
        m_rulesTreeModelEventHandler = new RulesTreeModelEventHandler();
        ListenerList.addListener((PMDDirectoryReturnedEventListener) m_pmdDirectoryReturnedEventHandler);
    }

    /**
     ***************************************************************************
     *
     * @param ruleSetName
     *
     * @return
     */
    protected RulesTreeNode getRuleSetNode(String ruleSetName) {
        if (ruleSetName != null) {
            RulesTreeNode rootNode = (RulesTreeNode) getRoot();
            Enumeration ruleSetNodes = rootNode.children();

            while (ruleSetNodes.hasMoreElements()) {
                RulesTreeNode ruleSetNode = (RulesTreeNode) ruleSetNodes.nextElement();

                if (ruleSetNode.getName().equalsIgnoreCase(ruleSetName)) {
                    return ruleSetNode;
                }
            }
        }

        return null;
    }

    /**
     ***************************************************************************
     *
     * @param ruleSetName
     * @param ruleName
     *
     * @return
     */
    protected RulesTreeNode getRuleNode(String ruleSetName, String ruleName) {
        if ((ruleSetName != null) && (ruleName != null)) {
            RulesTreeNode rootNode = (RulesTreeNode) getRoot();
            Enumeration ruleSetNodes = rootNode.children();

            while (ruleSetNodes.hasMoreElements()) {
                RulesTreeNode ruleSetNode = (RulesTreeNode) ruleSetNodes.nextElement();

                if (ruleSetNode.getName().equalsIgnoreCase(ruleSetName)) {
                    Enumeration ruleNodes = ruleSetNode.children();

                    while (ruleNodes.hasMoreElements()) {
                        RulesTreeNode ruleNode = (RulesTreeNode) ruleNodes.nextElement();

                        if (ruleNode.getName().equalsIgnoreCase(ruleName)) {
                            return ruleNode;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     ***************************************************************************
     * @return
     */
    protected static final RulesTreeModel get() throws PMDException {
        if (m_rulesTreeModel == null) {
            RulesTreeNode rootNode;

            rootNode = new RulesTreeNode("Rules");
            m_rulesTreeModel = new RulesTreeModel(rootNode);
            m_rulesTreeModel.buildTree();
        }

        return m_rulesTreeModel;
    }

    /**
     *******************************************************************************
     *
     */
    private void buildTree() throws PMDException {
        RuleSet[] ruleSets;
        RulesTreeNode rootNode;

        ruleSets = loadRuleSets();
        rootNode = (RulesTreeNode) getRoot();

        for (int n1 = 0; n1 < ruleSets.length; n1++) {
            RulesTreeNode ruleSetNode = new RulesTreeNode(ruleSets[n1]);

            rootNode.add(ruleSetNode);
            loadRuleTreeNodes(ruleSetNode);
        }

    }

    /**
     *******************************************************************************
     *
     * @return
     */
    private RuleSet[] loadRuleSets() throws PMDException {
        PMDDirectoryRequestEvent.notifyRequestAllRuleSets(this);

        // The event is processed.  The requested rule set is assembled by another class
        // that calls notifyReturnedAllRuleSets.  The list of rule sets is stored in the
        // inner class PMDDirectoryReturnedEventHandler.  Then processing will then continue here.
        //

        List ruleSetList = m_pmdDirectoryReturnedEventHandler.getRuleSetList();

        if (ruleSetList == null) {
            ruleSetList = new ArrayList();
        }

        //
        // Sort the rule sets by name in ascending order.
        //
        RuleSet[] ruleSets = new RuleSet[ruleSetList.size()];

        ruleSetList.toArray(ruleSets);
        Arrays.sort(ruleSets, new RuleSetNameComparator());

        return ruleSets;
    }

    /**
     *******************************************************************************
     *
     * @param ruleSetNode
     */
    private void loadRuleTreeNodes(RulesTreeNode ruleSetNode) {
        RuleSet ruleSet;
        Set setOfRules;
        Rule[] rules;

        ruleSet = ruleSetNode.getRuleSet();
        setOfRules = ruleSet.getRules();
        rules = new Rule[setOfRules.size()];

        setOfRules.toArray(rules);
        Arrays.sort(rules, new RuleNameComparator());

        for (int n = 0; n < rules.length; n++) {
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
    private void loadProperties(RulesTreeNode ruleNode) {
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

        while (keys.hasMoreElements()) {
            propertyNames[index] = (String) keys.nextElement();
            index++;
        }

        Arrays.sort(propertyNames, new PropertyNameComparator());

        for (int n = 0; n < propertyNames.length; n++) {
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
     ****************************************************************************
     ****************************************************************************
     ****************************************************************************
     */
    private class RulesTreeModelEventHandler implements RulesTreeModelEventListener {

        /**
         ***********************************************************************
         *
         */
        private RulesTreeModelEventHandler() {
            ListenerList.addListener((RulesTreeModelEventListener) this);
        }

        /**
         ************************************************************************
         *
         * @param parentNode
         */
        public void reload(RulesTreeModelEvent event) {
            RulesTreeNode parentNode = event.getParentNode();
            RulesTreeModel.this.reload(parentNode);
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void requestSelectedRule(RulesTreeModelEvent event) {
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void returnedSelectedRule(RulesTreeModelEvent event) {
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class PropertyNameComparator implements Comparator {

        /**
         ************************************************************************
         *
         * @param objectA
         * @param objectB
         *
         * @return
         */
        public int compare(Object objectA, Object objectB) {
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
    private class RuleSetNameComparator implements Comparator {

        /**
         ************************************************************************
         *
         * @param objectA
         * @param objectB
         *
         * @return
         */
        public int compare(Object objectA, Object objectB) {
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
    private class RuleNameComparator implements Comparator {

        /**
         ************************************************************************
         *
         * @param objectA
         * @param objectB
         *
         * @return
         */
        public int compare(Object objectA, Object objectB) {
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
    private class PMDDirectoryReturnedEventHandler implements PMDDirectoryReturnedEventListener {

        private List m_ruleSetList;
        private String m_ruleSetPath;

        /**
         ***************************************************************************
         */
        private String getRuleSetPath() {
            return m_ruleSetPath;
        }

        /**
         ***************************************************************************
         */
        private List getRuleSetList() {
            return m_ruleSetList;
        }

        /**
         ***************************************************************************
         *
         * @param event
         */
        public void returnedRuleSetPath(PMDDirectoryReturnedEvent event) {
            m_ruleSetPath = event.getRuleSetPath();
        }

        /**
         ***************************************************************************
         *
         * @param event
         */
        public void returnedAllRuleSets(PMDDirectoryReturnedEvent event) {
            m_ruleSetList = event.getRuleSetList();
        }

        /**
         ***************************************************************************
         *
         * @param event
         */
        public void returnedDefaultRuleSets(PMDDirectoryReturnedEvent event) {
            m_ruleSetList = event.getRuleSetList();
        }

        /**
         ***************************************************************************
         *
         * @param event
         */
        public void returnedIncludedRules(PMDDirectoryReturnedEvent event) {
            m_ruleSetList = event.getRuleSetList();
        }
    }
}