package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.swingui.event.ListenerList;
import net.sourceforge.pmd.swingui.event.SetupFilesEvent;
import net.sourceforge.pmd.swingui.event.SetupFilesEventListener;
import net.sourceforge.pmd.swingui.event.StatusBarEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.Component;
import java.io.File;

/**
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
class DirectoryTree extends JTree
{

    /**
     *******************************************************************************
     *
     */
    protected DirectoryTree(String rootName)
    {
        super(new DirectoryTreeModel(rootName));

        setRootVisible(true);
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        setCellRenderer(new DirectoryTreeNodeRenderer());
        ((DirectoryTreeModel) getModel()).setDirectoryTree(this);
        setBackground(UIManager.getColor("pmdTreeBackground"));
        ListenerList.addListener((SetupFilesEventListener) new SetupFilesEventHandler());
    }

    /**
     *******************************************************************************
     *
     */
    protected void expandRootNode()
    {
        DirectoryTreeModel treeModel = (DirectoryTreeModel) getModel();
        DirectoryTreeNode treeNode = (DirectoryTreeNode) treeModel.getRoot();
        TreePath treePath = new TreePath(treeNode.getPath());
        expandPath(treePath);
    }

    /**
     *******************************************************************************
     *
     */
    protected DirectoryTreeNode getSelectedNode()
    {
        TreePath path = getSelectionModel().getSelectionPath();

        return (DirectoryTreeNode) path.getLastPathComponent();
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class SetupFilesEventHandler implements SetupFilesEventListener
    {

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void startSetup(SetupFilesEvent event)
        {
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void stopSetup(SetupFilesEvent event)
        {
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void setFileList(SetupFilesEvent event)
        {
            File[] directories = event.getFileList();
            String name = "Locating root directories.";
            SetupFilesThread setupFilesThread = new SetupFilesThread(name, directories);
            setupFilesThread.start();
        }
    }

    /**
     ********************************************************************************
     ********************************************************************************
     ********************************************************************************
     */
    private class SetupFilesThread extends Thread
    {
        private File[] m_rootDirectories;

        /**
         ****************************************************************************
         *
         * @param name
         */
        private SetupFilesThread(String threadName, File[] rootDirectories)
        {
            super(threadName);

            m_rootDirectories = rootDirectories;
        }

        /**
         ***************************************************************************
         *
         */
        public void run()
        {
            setup();
            process();
            cleanup();
        }

        /**
         ***************************************************************************
         *
         */
        protected void setup()
        {
            SetupFilesEvent.notifyStartSetup(this);
            StatusBarEvent.notifyStartAnimation(this);
        }

        /**
         ***************************************************************************
         *
         */
        protected void process()
        {
            StatusBarEvent.notifyShowMessage(this, "Locating root directories.  Please wait...");
            DirectoryTreeModel treeModel = (DirectoryTreeModel) getModel();
            treeModel.setupFiles(m_rootDirectories);
            expandRootNode();
        }

        /**
         ***************************************************************************
         *
         */
        protected void cleanup()
        {
            StatusBarEvent.notifyStopAnimation(this);
            SetupFilesEvent.notifyStopSetup(this);
        }
    }

    /**
     ********************************************************************************
     ********************************************************************************
     ********************************************************************************
     */
    private class DirectoryTreeNodeRenderer extends DefaultTreeCellRenderer
    {

        private Icon m_defaultClosedIcon;
        private Icon m_defaultLeafIcon;
        private Icon m_defaultOpenIcon;

        /**
         ***************************************************************************
         *
         */
        protected DirectoryTreeNodeRenderer()
        {
            super();

            m_defaultClosedIcon = getDefaultClosedIcon();
            m_defaultLeafIcon = getDefaultLeafIcon();
            m_defaultOpenIcon = getDefaultOpenIcon();
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
            DirectoryTreeNode treeNode = (DirectoryTreeNode) object;
            Object userObject = treeNode.getUserObject();

            if (userObject instanceof String)
            {
                // The root node will display either an open or closed icon.
                setClosedIcon(m_defaultClosedIcon);
                setLeafIcon(m_defaultClosedIcon);
                setOpenIcon(m_defaultOpenIcon);
            }
            else if (((File) userObject).isFile())
            {
                // A file cannot have any children; therefore, the default icon settings are used.
                setClosedIcon(m_defaultClosedIcon);
                setLeafIcon(m_defaultLeafIcon);
                setOpenIcon(m_defaultOpenIcon);
            }
            else
            {
                // A directory may or may not have children.  The following conditions are used:
                //
                //   For a file
                //      files are not viewed in the tree
                //   For a directory
                //      has no children --- use closed folder icon
                //      has children
                //         is expanded --- use open folder icon
                //         is collapsed --- use closed folder icon
                //
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
