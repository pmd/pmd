package net.sourceforge.pmd.swingui;

import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
class DirectoryTreeNode extends DefaultMutableTreeNode
{

    /**
     ******************************************************************************
     */
    private DirectoryTreeNode(String name)
    {
        super(name);
    }

    /**
     ******************************************************************************
     *
     * @param directory
     */
    protected DirectoryTreeNode(File directory)
    {
        super(directory);
    }

    /**
     ******************************************************************************
     *
     * @return The directory or file name.
     */
    public String toString()
    {
        Object userObject = getUserObject();

        if (userObject instanceof String)
        {
            return (String) userObject;
        }

        File file = ((File) userObject);
        String name = file.getName();

        if ((name != null) && (name.length() > 0))
        {
            return name;
        }

        return file.getPath();
    }

    /**
     ******************************************************************************
     *
     * @return A new root node.
     */
    protected static DirectoryTreeNode createRootNode(String rootName)
    {
        return new DirectoryTreeNode(rootName);
    }
}