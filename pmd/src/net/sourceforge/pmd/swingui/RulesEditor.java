package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.swingui.event.ListenerList;
import net.sourceforge.pmd.swingui.event.PMDDirectoryRequestEvent;
import net.sourceforge.pmd.swingui.event.PMDDirectoryReturnedEvent;
import net.sourceforge.pmd.swingui.event.PMDDirectoryReturnedEventListener;
import net.sourceforge.pmd.swingui.event.RuleSetChangedEvent;
import net.sourceforge.pmd.swingui.event.RuleSetEvent;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
class RulesEditor extends JDialog
{

    private RulesTree m_tree;
    private JSplitPane m_splitPane;
    private RuleEditingTabbedPane m_editingTabbedPane;
    private boolean m_firstLayout = true;

    /**
     *******************************************************************************
     *
     * @param parentWindow
     */
    protected RulesEditor()
        throws PMDException
    {
        super(PMDViewer.getViewer(), "Rules Editor", true);

        PMDViewer pmdViewer = PMDViewer.getViewer();
        int windowWidth = pmdViewer.getWidth();
        int windowHeight = pmdViewer.getHeight();

        setSize(ComponentFactory.adjustWindowSize(windowWidth, windowHeight));
        setLocationRelativeTo(pmdViewer);
        setResizable(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        m_tree = new RulesTree(this);
        m_tree.buildTree();

        JScrollPane treeScrollPane;
        JSplitPane splitPane;

        treeScrollPane = createTreeScrollPane();
        m_editingTabbedPane = new RuleEditingTabbedPane(m_tree);
        splitPane = createSplitPane(treeScrollPane, m_editingTabbedPane);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(splitPane, BorderLayout.CENTER);

        getContentPane().add(contentPanel);
        createMenuBar();
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
        m_splitPane = new JSplitPane();

        m_splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        m_splitPane.setResizeWeight(0.5);
        m_splitPane.setDividerSize(5);
        m_splitPane.setLeftComponent(treeScrollPane);
        m_splitPane.setRightComponent(editingTabbedPane);

        return m_splitPane;
    }

    /**
     *********************************************************************************
     *
     */
    private void createMenuBar()
    {
       JMenuBar menuBar = new JMenuBar();

       setJMenuBar(menuBar);
       menuBar.add(new FileMenu());
       menuBar.add(new HelpMenu());
    }

    /**
     *********************************************************************************
     *
     */
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);

        if (visible && m_firstLayout)
        {
            m_splitPane.setDividerLocation(0.4);
            validate();
            repaint();
            m_firstLayout = false;
        }
    }

    /**
     *******************************************************************************
     *
     */
    public void dispose()
    {
        if (m_tree != null)
        {
            m_tree.dispose();
        }

        super.dispose();
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
            m_editingTabbedPane.saveData();

            RulesTreeNode rootNode = (RulesTreeNode) m_tree.getModel().getRoot();

            saveData(rootNode);
            writeRuleSets(rootNode);
            RulesEditor.this.setVisible(false);
            RuleSetChangedEvent.notifyRuleSetsChanged(this);
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
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class AddDefaultRulesButtonActionListener
        implements ActionListener, PMDDirectoryReturnedEventListener
    {

        private List m_defaultRuleSetList;

        /**
         ********************************************************************
         *
         */
        private AddDefaultRulesButtonActionListener()
        {
            ListenerList.addListener((PMDDirectoryReturnedEventListener) this);
        }

        /**
         ********************************************************************
         *
         * @param event
         */
        public void actionPerformed(ActionEvent event)
        {
            PMDDirectoryRequestEvent.notifyRequestDefaultRuleSets(this);
            Iterator ruleSets = m_defaultRuleSetList.iterator();
            RulesTreeNode rootNode = (RulesTreeNode) m_tree.getModel().getRoot();

            while (ruleSets.hasNext())
            {
                RuleSet ruleSet = (RuleSet) ruleSets.next();
                String ruleSetName = ruleSet.getName();
                RulesTreeNode ruleSetNode = rootNode.getChildNode(ruleSetName);

                if (ruleSetNode == null)
                {
                    addNewRuleSet(rootNode, ruleSet);
                }
                else
                {
                    addRulesToRuleSet(ruleSetNode, ruleSet);
                }
            }
        }

        /**
         ***********************************************************************
         *
         * @param rootNode
         * @param ruleSet
         */
        private void addNewRuleSet(RulesTreeNode rootNode, RuleSet ruleSet)
        {
            RulesTreeNode ruleSetNode = new RulesTreeNode(ruleSet);
            rootNode.add(ruleSetNode);
            rootNode.sortChildren();
            addRulesToRuleSet(ruleSetNode, ruleSet);
        }

        /**
         ***********************************************************************
         *
         * @param ruleSetNode
         * @param ruleSet
         */
        private void addRulesToRuleSet(RulesTreeNode ruleSetNode, RuleSet ruleSet)
        {
            Iterator rules = ruleSet.getRules().iterator();

            while (rules.hasNext())
            {
                Rule rule = (Rule) rules.next();
                String ruleName = rule.getName();
                RulesTreeNode ruleNode = ruleSetNode.getChildNode(ruleName);

                if (ruleNode == null)
                {
                    ruleNode = new RulesTreeNode(ruleSetNode, rule);
                    ruleSetNode.add(ruleNode);
                }
            }

            ruleSetNode.sortChildren();
        }

        /**
         **********************************************************************
         *
         * PMDDirectoryReturnedEventListener
         *
         * @param event
         */
        public void returnedRuleSetPath(PMDDirectoryReturnedEvent event)
        {
        }

        /**
         ***********************************************************************
         *
         * PMDDirectoryReturnedEventListener
         *
         * @param event
         */
        public void returnedAllRuleSets(PMDDirectoryReturnedEvent event)
        {
        }

        /**
         ***********************************************************************
         *
         * PMDDirectoryReturnedEventListener
         *
         * @param event
         */
        public void returnedDefaultRuleSets(PMDDirectoryReturnedEvent event)
        {
            m_defaultRuleSetList = event.getRuleSetList();
        }

        /**
         *******************************************************************************
         *
         * PMDDirectoryReturnedEventListener
         *
         * @param event
         */
        public void returnedIncludedRules(PMDDirectoryReturnedEvent event)
        {
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
            menuItem = new JMenuItem("Save", icon);
            menuItem.addActionListener((ActionListener) new SaveActionListener());
            menuItem.setMnemonic('S');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
            add(menuItem);

            //
            // Save As menu item
            //
            icon = UIManager.getIcon("cancel");
            menuItem = new JMenuItem("Cancel", icon);
            menuItem.addActionListener((ActionListener) new CancelActionListener());
            menuItem.setMnemonic('C');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
            add(menuItem);
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class HelpMenu extends JMenu
    {

        /**
         ********************************************************************
         *
         * @param menuBar
         */
        private HelpMenu()
        {
            super("Help");

            setMnemonic('H');

            Icon icon;
            JMenuItem menuItem;

            //
            // Online Help menu item
            //
            icon = UIManager.getIcon("help");
            menuItem = new JMenuItem("Online Help", icon);
            menuItem.addActionListener(new HelpActionListener());
            menuItem.setMnemonic('H');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_MASK));
            add(menuItem);

            //
            // Separator
            //
            add(new JSeparator());

            //
            // About menu item
            //
            menuItem = new JMenuItem("About...");
            menuItem.addActionListener(new AboutActionListener());
            menuItem.setMnemonic('A');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
            add(menuItem);
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class AboutActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
            PMDViewer.getViewer().setEnableViewer(false);
            (new AboutPMD(RulesEditor.this)).setVisible(true);
            PMDViewer.getViewer().setEnableViewer(true);
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class HelpActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
            MessageDialog.show(RulesEditor.this, "Online Help not available yet.");
        }
    }
}
