package net.sourceforge.pmd.swingui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Point;
import java.io.File;
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

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
class RulesTree extends JTree
{

    /**
     ***************************************************************************
     *
     */
    protected RulesTree()
    {
        super(new DefaultTreeModel(RulesTreeNode.createRootNode()));

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
                RulesTreeNode treeNode = RulesTree.this.getSelectedNode();
                JPopupMenu popupMenu = null;

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

                if (popupMenu != null)
                {
                    Point location = event.getPoint();
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

            m_addRuleSetMenuItem.setEnabled(true);
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
            m_addRuleMenuItem.setEnabled(true);
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
            m_addPropertyMenuItem.setEnabled(true);
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
/*
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
            m_deletepropertyMenuItem.addActionListener(new DeletePropertyRuleActionListener());
            popupMenu.add(m_deletePropertyMenuItem);
*/
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

        /**
         ***********************************************************************
         *
         * @param event
         */
        public void actionPerformed(ActionEvent event)
        {
            String untitled = "Untitled";
            String name = untitled;
            int suffix = 0;
            RulesTreeNode parentNode = (RulesTreeNode) ((DefaultTreeModel) getModel()).getRoot();

            while (parentNode.getChildNode(name) != null)
            {
                suffix++;
                name = untitled + suffix;
            }

            parentNode.addNode(new RulesTreeNode(name, RulesTreeNode.RULE_SET_TYPE));
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
            Object userObject = treeNode.getUserObject();

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