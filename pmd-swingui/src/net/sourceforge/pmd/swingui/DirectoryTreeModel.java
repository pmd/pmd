package net.sourceforge.pmd.swingui;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.FileFilter;
import java.util.Enumeration;

/**
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
class DirectoryTreeModel extends DefaultTreeModel implements TreeWillExpandListener {

    private DirectoryTree m_directoryTree;

    /**
     *****************************************************************************
     */
    protected DirectoryTreeModel(String rootName) {
        super(DirectoryTreeNode.createRootNode(rootName));
    }

    /**
     ********************************************************************************
     *
     */
    protected void setupFiles(File[] rootFiles) {
        DirectoryTreeNode rootNode;
        FilesFilter filesFilter;

        rootNode = (DirectoryTreeNode) getRoot();
        filesFilter = new FilesFilter();

        if (rootFiles != null) {
            for (int n1 = 0; n1 < rootFiles.length; n1++) {
                File rootFile;
                DirectoryTreeNode fileNode;

                rootFile = rootFiles[n1];
                fileNode = new DirectoryTreeNode(rootFile);

                rootNode.add(fileNode);

                File[] files = rootFile.listFiles(filesFilter);

                if (files != null) {
                    for (int n2 = 0; n2 < files.length; n2++) {
                        fileNode.add(new DirectoryTreeNode(files[n2]));
                    }
                }
            }
        }
    }

    /**
     ********************************************************************************
     *
     * @param directory
     */
    protected void setDirectoryTree(DirectoryTree directoryTree) {
        m_directoryTree = directoryTree;

        m_directoryTree.addTreeWillExpandListener(this);
    }

    /**
     ******************************************************************************
     *
     * Called before a directory tree node in the tree will be expanded.  The tree node
     * to be expanded will contain a directory.  The tree node will contain children
     * consisting of subdirectories and/or files.  The subdirectory tree nodes will have
     * child tree nodes added so that they may be expanded.
     *
     * @param event
     *
     * @throws ExpandVetoException
     */
    public void treeWillExpand(TreeExpansionEvent event) {
        TreePath treePath;
        DirectoryTreeNode treeNode;
        Enumeration children;

        treePath = event.getPath();
        treeNode = (DirectoryTreeNode) treePath.getLastPathComponent();
        children = treeNode.children();

        while (children.hasMoreElements()) {
            DirectoryTreeNode childTreeNode = (DirectoryTreeNode) children.nextElement();
            File directory = (File) childTreeNode.getUserObject();
            File[] files = directory.listFiles(new FilesFilter());

            childTreeNode.removeAllChildren();

            if (files != null) {
                for (int n = 0; n < files.length; n++) {
                    childTreeNode.add(new DirectoryTreeNode(files[n]));
                }
            }
        }
    }

    /**
     ******************************************************************************
     *
     * Called before a directory tree node in the tree will be collapsed.  The tree node
     * to be collapsed will contain a directory.  The tree node will contain children
     * consisting of subdirectories and/or files.  The subdirectory tree nodes will have
     * their child tree nodes removed since they will no longer be visible.
     *
     * @param event
     *
     * @throws ExpandVetoException
     */
    public void treeWillCollapse(TreeExpansionEvent event) {
        TreePath treePath;
        DirectoryTreeNode treeNode;
        Enumeration children;

        treePath = event.getPath();
        treeNode = (DirectoryTreeNode) treePath.getLastPathComponent();
        children = treeNode.children();

        while (children.hasMoreElements()) {
            DirectoryTreeNode childTreeNode = (DirectoryTreeNode) children.nextElement();

            childTreeNode.removeAllChildren();
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class FilesFilter implements FileFilter {

        /**
         ****************************************************************************
         *
         * @param file
         *
         * @return
         */
        public boolean accept(File file) {
            return file.isDirectory() && (file.isHidden() == false);
        }
    }
}