package net.sourceforge.pmd.swingui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Point;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.EventObject;

import javax.swing.border.EtchedBorder;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.UIManager;

import net.sourceforge.pmd.RuleSet;


/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
class RulesTree extends JTree
{

    private RulesEditor m_rulesEditor;

    /**
     ***************************************************************************
     *
     * @param rulesEditor
     */
    protected RulesTree(RulesEditor rulesEditor)
    {
        super(new DefaultTreeModel(RulesTreeNode.createRootNode()));

        m_rulesEditor = rulesEditor;

        setRootVisible(true);
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        setCellRenderer(new TreeNodeRenderer());
        setCellEditor(new TreeCellEditor());
        setBackground(UIManager.getColor("pmdTreeBackground"));
        addMouseListener(new RulesTreeMouseListener());
    }

    /**
     ***************************************************************************
     *
     * @param node
     */
    protected void expandNode(RulesTreeNode node)
    {
        TreePath treePath = new TreePath(node.getPath());

        expandPath(treePath);
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    protected RulesTreeNode getSelectedNode()
    {
        TreePath treePath = getSelectionPath();

        return (treePath == null) ? null : (RulesTreeNode) treePath.getLastPathComponent();
    }

    /**
     ***************************************************************************
     *
     * @param event
     */
    protected void sortChildren(RulesTreeNode parentNode)
    {
        if (parentNode != null)
        {
            int childCount = parentNode.getChildCount();
            RulesTreeNode[] treeNodes = new RulesTreeNode[childCount];
            boolean needToSort = false;

            for (int n = 0; n < childCount; n++)
            {
                treeNodes[n] = (RulesTreeNode) parentNode.getChildAt(n);

                if ((n > 0) && (needToSort == false))
                {
                    String previousNodeName = treeNodes[n - 1].getName();
                    String currentNodeName = treeNodes[n].getName();

                    if (currentNodeName.compareToIgnoreCase(previousNodeName) < 0)
                    {
                        needToSort = true;
                    }
                }
            }

            if (needToSort)
            {
                Arrays.sort(treeNodes, new SortComparator());
                parentNode.removeAllChildren();

                for (int n = 0; n < treeNodes.length; n++)
                {
                    parentNode.add(treeNodes[n]);
                }

                ((DefaultTreeModel) getModel()).reload(parentNode);
            }

            for (int n = 0; n < treeNodes.length; n++)
            {
                treeNodes[n] = null;
            }
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class SortComparator implements Comparator
    {

        /**
         ***************************************************************************
         *
         * @param object1
         * @param object2
         *
         * @return
         */
        public int compare(Object object1, Object object2)
        {
            String name1 = ((RulesTreeNode) object1).getName();
            String name2 = ((RulesTreeNode) object2).getName();

            return name1.compareToIgnoreCase(name2);
        }

        /**
         ***************************************************************************
         *
         * @param object
         *
         * @return
         */
        public boolean equals(Object object)
        {
            return object == this;
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class RulesTreeMouseListener extends MouseAdapter
    {

        private JMenuItem m_addRuleSetMenuItem;
        private JMenuItem m_deleteRuleSetMenuItem;
        private JMenuItem m_addRuleMenuItem;
        private JMenuItem m_deleteRuleMenuItem;
        private JMenuItem m_addPropertyMenuItem;
        private JMenuItem m_deletePropertyMenuItem;

        /**
         ***********************************************************************
         *
         * @param event
         */
        public void mouseReleased(MouseEvent event)
        {
            if (event.isPopupTrigger())
            {
                Point location;
                TreePath treePath;
                RulesTreeNode treeNode;
                JPopupMenu popupMenu;

                location = event.getPoint();
                treePath = RulesTree.this.getPathForLocation(location.x, location.y);
                treeNode = (RulesTreeNode) treePath.getLastPathComponent();
                popupMenu = null;

                if (treeNode.isRoot())
                {
                    popupMenu = createRootPopupMenu();
                }
                else if (treeNode.isRuleSet())
                {
                    popupMenu = createRuleSetPopupMenu();
                }
                else if (treeNode.isRule())
                {
                    popupMenu = createRulePopupMenu();
                }
                else if (treeNode.isProperty())
                {
                    popupMenu = createPropertyPopupMenu();
                }

                if (popupMenu != null)
                {
                    popupMenu.show(RulesTree.this, location.x, location.y);
                }
            }
        }

        /**
         ***********************************************************************
         *
         * @return
         */
        private JPopupMenu createRootPopupMenu()
        {
            JPopupMenu popupMenu = createPopupMenu();

            m_addRuleSetMenuItem.setEnabled(true);
            m_deleteRuleSetMenuItem.setEnabled(false);
            m_addRuleMenuItem.setEnabled(false);
            m_deleteRuleMenuItem.setEnabled(false);
            m_addPropertyMenuItem.setEnabled(false);
            m_deletePropertyMenuItem.setEnabled(false);

            return popupMenu;
        }

        /**
         ***********************************************************************
         *
         * @return
         */
        private JPopupMenu createRuleSetPopupMenu()
        {
            JPopupMenu popupMenu = createPopupMenu();

            m_addRuleSetMenuItem.setEnabled(false);
            m_deleteRuleSetMenuItem.setEnabled(true);
            m_addRuleMenuItem.setEnabled(true);
            m_deleteRuleMenuItem.setEnabled(false);
            m_addPropertyMenuItem.setEnabled(false);
            m_deletePropertyMenuItem.setEnabled(false);

            return popupMenu;
        }

        /**
         ***********************************************************************
         *
         * @return
         */
        private JPopupMenu createRulePopupMenu()
        {
            JPopupMenu popupMenu = createPopupMenu();

            m_addRuleSetMenuItem.setEnabled(false);
            m_deleteRuleSetMenuItem.setEnabled(false);
            m_addRuleMenuItem.setEnabled(false);
            m_deleteRuleMenuItem.setEnabled(true);
            m_addPropertyMenuItem.setEnabled(true);
            m_deletePropertyMenuItem.setEnabled(false);

            return popupMenu;
        }

        /**
         ***********************************************************************
         *
         * @return
         */
        private JPopupMenu createPropertyPopupMenu()
        {
            JPopupMenu popupMenu = createPopupMenu();

            m_addRuleSetMenuItem.setEnabled(false);
            m_deleteRuleSetMenuItem.setEnabled(false);
            m_addRuleMenuItem.setEnabled(false);
            m_deleteRuleMenuItem.setEnabled(false);
            m_addPropertyMenuItem.setEnabled(false);
            m_deletePropertyMenuItem.setEnabled(true);

            return popupMenu;
        }

        /**
         ***********************************************************************
         *
         * @return
         */
        private JPopupMenu createPopupMenu()
        {
            JPopupMenu popupMenu = new JPopupMenu();

            m_addRuleSetMenuItem = new JMenuItem("Add Rule Set");
            m_addRuleSetMenuItem.addActionListener(new AddRuleSetActionListener());
            popupMenu.add(m_addRuleSetMenuItem);

            m_deleteRuleSetMenuItem = new JMenuItem("Delete Rule Set");
            m_deleteRuleSetMenuItem.addActionListener(new DeleteRuleSetActionListener());
            popupMenu.add(m_deleteRuleSetMenuItem);

            popupMenu.add(new JSeparator());

            m_addRuleMenuItem = new JMenuItem("Add Rule");
            m_addRuleMenuItem.addActionListener(new AddRuleActionListener());
            popupMenu.add(m_addRuleMenuItem);

            m_deleteRuleMenuItem = new JMenuItem("Delete Rule");
            m_deleteRuleMenuItem.addActionListener(new DeleteRuleActionListener());
            popupMenu.add(m_deleteRuleMenuItem);

            popupMenu.add(new JSeparator());

            m_addPropertyMenuItem = new JMenuItem("Add Rule Property");
            m_addPropertyMenuItem.addActionListener(new AddRulePropertyActionListener());
            popupMenu.add(m_addPropertyMenuItem);

            m_deletePropertyMenuItem = new JMenuItem("Delete Rule Property");
            m_deletePropertyMenuItem.addActionListener(new DeleteRulePropertyActionListener());
            popupMenu.add(m_deletePropertyMenuItem);

            return popupMenu;
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class AddRuleSetActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class DeleteRuleSetActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class AddRuleActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class DeleteRuleActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class AddRulePropertyActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class DeleteRulePropertyActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class TreeCellEditor extends DefaultTreeCellEditor
    {

        /**
         ***************************************************************************
         *
         */
        private TreeCellEditor()
        {
            super(RulesTree.this, (DefaultTreeCellRenderer) RulesTree.this.getCellRenderer());
        }

        /**
         ***************************************************************************
         *
         * @return
         */
        public boolean isCellEditable(EventObject event)
        {
            return false;
        }
    }

    /**
     ********************************************************************************
     ********************************************************************************
     ********************************************************************************
     */
    private class TreeNodeRenderer extends DefaultTreeCellRenderer
    {

        private Icon m_defaultClosedIcon;
        private Icon m_defaultLeafIcon;
        private Icon m_defaultOpenIcon;
        private Icon m_documentIcon;

        /**
         ***************************************************************************
         *
         */
        protected TreeNodeRenderer()
        {
            super();

            m_defaultClosedIcon = getDefaultClosedIcon();
            m_defaultLeafIcon = getDefaultLeafIcon();
            m_defaultOpenIcon = getDefaultOpenIcon();
            m_documentIcon = UIManager.getIcon("document");
            setBackgroundNonSelectionColor(UIManager.getColor("pmdTreeBackground"));
        }

        /**
         **************************************************************************
         *
         * @param tree
         * @param object
         * @param isSelected
         * @param isExpanded
         * @param isLeaf
         * @param row
         * @param hasFocus
         *
         * @return
         */
        public Component getTreeCellRendererComponent(JTree tree,
                                                      Object object,
                                                      boolean isSelected,
                                                      boolean isExpanded,
                                                      boolean isLeaf,
                                                      int row,
                                                      boolean hasFocus)
        {
            RulesTreeNode treeNode = (RulesTreeNode) object;

            if (treeNode.isProperty())
            {
                setClosedIcon(m_defaultClosedIcon);
                setLeafIcon(m_documentIcon);
                setOpenIcon(m_defaultOpenIcon);
            }
            else
            {
                setClosedIcon(m_defaultClosedIcon);
                setLeafIcon(m_defaultClosedIcon);
                setOpenIcon(m_defaultOpenIcon);
            }

            return super.getTreeCellRendererComponent(tree,
                                                      object,
                                                      isSelected,
                                                      isExpanded,
                                                      isLeaf,
                                                      row,
                                                      hasFocus);
        }
    }
}