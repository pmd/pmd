package net.sourceforge.pmd.swingui.viewer;

import java.io.File;
import java.io.FileFilter;
import java.util.Vector;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.JList;
import javax.swing.tree.TreePath;

/**
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
class SourceFileList extends JList implements TreeSelectionListener
{

    private DirectoryTree m_directoryTree;
    private File m_lastDirectory;

    //Constants
    private final String JAVA = ".java";

    /**
     *******************************************************************************
     */
    protected SourceFileList()
    {
    }

    /**
     *******************************************************************************
     *
     * @param index
     */
    protected File getFile(int index)
    {
        if ((index < 0) || (index >= getModel().getSize()))
        {
            return null;
        }

        SourceFileListEntry entry;

        entry = (SourceFileListEntry) getModel().getElementAt(index);

        return entry.getFile();
    }

    /**
     *******************************************************************************
     *
     * @param directoryTree
     */
    protected void setDirectoryTree(DirectoryTree directoryTree)
    {
        m_directoryTree = directoryTree;

        m_directoryTree.addTreeSelectionListener(this);
    }

    /**
     *******************************************************************************
     *
     * @param directoryTree
     */
    public void valueChanged(TreeSelectionEvent event)
    {
        TreePath treePath = event.getPath();
        DirectoryTreeNode treeNode = (DirectoryTreeNode) treePath.getLastPathComponent();
        Object userObject = treeNode.getUserObject();

        if ((userObject instanceof File) && (userObject != m_lastDirectory))
        {
            File directory;
            File[] files;
            Vector fileNames;

            directory = (File) userObject;
            m_lastDirectory = directory;
            files = directory.listFiles(new FilesFilter());
            fileNames = new Vector();

            if (files != null)
            {
                for (int n = 0; n < files.length; n++)
                {
                    fileNames.add(new SourceFileListEntry(files[n]));
                }
            }

            setListData(fileNames);
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class FilesFilter implements FileFilter
    {

        public boolean accept(File file)
        {
            if (file.isFile() && (file.isHidden() == false))
            {
                String fileName = file.getName().toLowerCase();

                return (fileName.endsWith(JAVA));
            }

            return false;

        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class SourceFileListEntry
    {

        private File m_file;

        /**
         ********************************************************************************
         */
        protected SourceFileListEntry(File file)
        {
            m_file = file;
        }

        /**
         ******************************************************************************
         *
         * @return The directory or file name.
         */
        private File getFile()
        {
            return m_file;
        }

        /**
         ******************************************************************************
         *
         * @return The directory or file name.
         */
        public String toString()
        {
            String name = m_file.getName();

            if ((name != null) && (name.length() > 0))
            {
                return name;
            }

            return m_file.getPath();
        }
    }
}