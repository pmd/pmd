package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.swingui.event.ListenerList;
import net.sourceforge.pmd.swingui.event.RuleSetChangedEvent;
import net.sourceforge.pmd.swingui.event.RuleSetEvent;
import net.sourceforge.pmd.swingui.event.RulesInMemoryEvent;
import net.sourceforge.pmd.swingui.event.RulesInMemoryEventListener;
import net.sourceforge.pmd.swingui.event.RulesTreeModelEvent;
import net.sourceforge.pmd.swingui.event.RulesTreeModelEventListener;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
class RulesEditor extends JPanel
{

    private RulesTree m_tree;
    private JSplitPane m_splitPane;
    private RuleEditingTabbedPane m_editingTabbedPane;
    private JMenuBar m_menuBar;
    private JMenuItem m_printSelectedRuleMenuItem;

    /**
     *******************************************************************************
     *
     * @param parentWindow
     */
    protected RulesEditor() throws PMDException
    {
        super(new BorderLayout());

        m_tree = new RulesTree();

        JScrollPane treeScrollPane;

        treeScrollPane = createTreeScrollPane();
        m_editingTabbedPane = new RuleEditingTabbedPane(m_tree);
        m_splitPane = ComponentFactory.createHorizontalSplitPane(treeScrollPane, m_editingTabbedPane);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(m_splitPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        createMenuBar();

        ListenerList.addListener((RulesInMemoryEventListener) new RulesInMemoryEventHandler());
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    private JScrollPane createTreeScrollPane()
    {
        JScrollPane scrollPane = ComponentFactory.createScrollPane(m_tree);
        Color background = UIManager.getColor("pmdTreeBackground");
        scrollPane.getViewport().setBackground(background);

        return scrollPane;
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
    public void adjustSplitPaneDividerLocation()
    {
        m_splitPane.setDividerLocation(0.4);
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
     ********************************************************************
     *
     */
    protected void saveData()
    {
        m_editingTabbedPane.saveData();
        saveData((RulesTreeNode) m_tree.getModel().getRoot());
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
            saveData();
            writeRuleSets((RulesTreeNode) m_tree.getModel().getRoot());
            RuleSetChangedEvent.notifyRuleSetsChanged(this);
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

            RuleSetEvent.notifySaveRuleSets(this, ruleSetList);
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class CancelActionListener implements ActionListener
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
            // Save As menu item
            //
            icon = UIManager.getIcon("cancel");
            menuItem = new JMenuItem("Cancel Changes", icon);
            menuItem.addActionListener((ActionListener) new CancelActionListener());
            menuItem.setMnemonic('C');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
            add(menuItem);

            //
            // Separator
            //
            add(new JSeparator());

            //
            // Print Rules menu item
            //
            icon = UIManager.getIcon("print");
            menuItem = new JMenuItem("Print Rules...", icon);
            menuItem.addActionListener((ActionListener) new PrintRulesActionListener());
            menuItem.setMnemonic('R');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
            add(menuItem);

            //
            // Print Selected Rule menu item
            //
            icon = UIManager.getIcon("print");
            m_printSelectedRuleMenuItem = new JMenuItem("Print Selected Rule...", icon);
            m_printSelectedRuleMenuItem.addActionListener((ActionListener) new PrintRulesActionListener());
            m_printSelectedRuleMenuItem.setMnemonic('E');
            m_printSelectedRuleMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK));
            add(m_printSelectedRuleMenuItem);

            //
            // Page Setup menu item
            //
            menuItem = new JMenuItem("Page Setup...");
            menuItem.addActionListener((ActionListener) new PageSetupActionListener());
            menuItem.setMnemonic('A');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
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

            addMouseListener(new FileMenuMouseListener());
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class PrintRulesActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
            MessageDialog.show(PMDViewer.getViewer(), "Printing not available yet.");
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class PageSetupActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
            MessageDialog.show(PMDViewer.getViewer(), "Page setup not available yet.");
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

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class FileMenuMouseListener extends MouseAdapter
                                        implements RulesTreeModelEventListener
    {

        private Rule m_rule;

        /**
         *****************************************************************************
         *
         * @param event
         */
        public void mouseEntered(MouseEvent event)
        {
            try
            {
                ListenerList.addListener((RulesTreeModelEventListener) this);
                RulesTreeModelEvent.notifyRequestSelectedRule(this);
                boolean enable = (m_rule != null);
                m_printSelectedRuleMenuItem.setEnabled(enable);
            }
            finally
            {
                ListenerList.removeListener((RulesTreeModelEventListener) this);
            }
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void reload(RulesTreeModelEvent event)
        {
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void requestSelectedRule(RulesTreeModelEvent event)
        {
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void returnedSelectedRule(RulesTreeModelEvent event)
        {
            m_rule = event.getRule();
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class RulesInMemoryEventHandler implements RulesInMemoryEventListener
    {

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void requestAllRules(RulesInMemoryEvent event)
        {
            RuleSet rules = new RuleSet();
            RulesTreeNode rootNode = (RulesTreeNode) m_tree.getModel().getRoot();
            getRules(rootNode, rules, new IncludeAllRuleFilter());
            RulesInMemoryEvent.notifyReturnedRules(this, rules);
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void requestIncludedRules(RulesInMemoryEvent event)
        {
            try
            {
                RuleSet rules = new RuleSet();
                RulesTreeNode rootNode = (RulesTreeNode) m_tree.getModel().getRoot();
                int lowestPriority = Preferences.getPreferences().getLowestPriorityForAnalysis();
                getRules(rootNode, rules, new IncludeSelectedRuleFilter(lowestPriority));
                RulesInMemoryEvent.notifyReturnedRules(this, rules);
            }
            catch (PMDException pmdException)
            {
                String message = pmdException.getMessage();
                Exception exception = pmdException.getReason();
                MessageDialog.show(PMDViewer.getViewer(), message, exception);
            }
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        private void getRules(RulesTreeNode rootNode, RuleSet rules, RuleFilter ruleFilter)
        {
            Enumeration ruleSetNodes = rootNode.children();

            while (ruleSetNodes.hasMoreElements())
            {
                RulesTreeNode ruleSetNode = (RulesTreeNode) ruleSetNodes.nextElement();

                if (ruleFilter.include(ruleSetNode))
                {
                    Enumeration ruleNodes = ruleSetNode.children();

                    while (ruleNodes.hasMoreElements())
                    {
                        RulesTreeNode ruleNode = (RulesTreeNode) ruleNodes.nextElement();

                        if (ruleFilter.include(ruleNode))
                        {
                            rules.addRule(ruleNode.getRule());
                        }
                    }
                }
            }
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void returnedRules(RulesInMemoryEvent event)
        {
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private abstract class RuleFilter
    {

        /**
         ***************************************************************************
         *
         * @param rule
         *
         * @return
         */
        protected abstract boolean include(RulesTreeNode treeNode);
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class IncludeAllRuleFilter extends RuleFilter
    {

        /**
         ***************************************************************************
         *
         * @param rule
         *
         * @return
         */
        protected boolean include(RulesTreeNode treeNode)
        {
            return true;
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class IncludeSelectedRuleFilter extends RuleFilter
    {
        private int m_lowestPriority;

        /**
         ***************************************************************************
         *
         * @param rule
         *
         * @return
         */
        private IncludeSelectedRuleFilter(int lowestPriority)
        {
            m_lowestPriority = lowestPriority;
        }

        /**
         ***************************************************************************
         *
         * @param rule
         *
         * @return
         */
        protected boolean include(RulesTreeNode treeNode)
        {
            return treeNode.include() && (treeNode.getPriority() <= m_lowestPriority);
        }
    }
}
