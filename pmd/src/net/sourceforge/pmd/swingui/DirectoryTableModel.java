package net.sourceforge.pmd.swingui;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
class DirectoryTableModel extends DefaultTableModel implements TreeSelectionListener
{
    private DateFormat m_dateFormat;
    private DecimalFormat m_decimalFormat;
    private String m_fileExtension;

    //Constants
    protected static final int FILE_NAME_COLUMN = 0;
    protected static final int FILE_SIZE_COLUMN = 1;
    protected static final int FILE_LAST_MODIFIED_COLUMN = 2;
    private final int FILE_COLUMN = 3; // not a visible column

    /**
     *********************************************************************************
     *
     */
    protected DirectoryTableModel(DirectoryTree directoryTree, String fileExtension)
    {
        super(createData(), createColumnNames());

        directoryTree.addTreeSelectionListener(this);

        DecimalFormatSymbols decimalFormatSymbols;
        StringBuffer buffer;
        String pattern;

        m_fileExtension = fileExtension.toLowerCase();
        m_dateFormat = DateFormat.getDateTimeInstance();
        decimalFormatSymbols = new DecimalFormatSymbols();
        buffer = new StringBuffer(25);

        buffer.append('#');
        buffer.append(decimalFormatSymbols.getGroupingSeparator());
        buffer.append('#');
        buffer.append('#');
        buffer.append(decimalFormatSymbols.getDigit());
        buffer.append(" bytes");

        pattern = buffer.toString();
        m_decimalFormat = new DecimalFormat(pattern, decimalFormatSymbols);
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    private static Vector createData()
    {
        return new Vector();
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    private static Vector createColumnNames()
    {
        Vector row = new Vector(2);

        row.add("File Name");
        row.add("Size");
        row.add("Last Modified");

        return row;
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    protected File getFile(int row)
    {
        if ((row >= 0) && (row < getRowCount()))
        {
            return (File) getValueAt(row, FILE_COLUMN);
        }

        return null;
    }

    /**
     *******************************************************************************
     *
     * @param row
     * @param column
     *
     * @return
     */
    public boolean isCellEditable(int row, int column)
    {
        return false;
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
        Vector rows = getDataVector();

        for (int n = 0; n < rows.size(); n++)
        {
            ((Vector) rows.get(n)).clear();
        }

        rows.clear();

        if (userObject instanceof File)
        {
            File directory;
            File[] files;
            StringBuffer buffer = new StringBuffer(25);

            directory = (File) userObject;
            files = directory.listFiles(new FilesFilter());

            if (files != null)
            {
                int largestSize = 0;

                for (int n1 = 0; n1 < files.length; n1++)
                {
                    Vector row = new Vector(3);
                    String fileName = files[n1].getName();
                    String size = m_decimalFormat.format(files[n1].length());
                    Date date = new Date(files[n1].lastModified());
                    String lastModified = m_dateFormat.format(date);

                    if (size.length() > largestSize)
                    {
                        largestSize = size.length();
                    }

                    row.add(fileName);
                    row.add(size);
                    row.add(lastModified);
                    row.add(files[n1]);

                    rows.add(row);
                }

                // Get the size with the most characters.
                for (int n1 = 0; n1 < files.length; n1++)
                {
                    Vector row = (Vector) rows.get(n1);
                    String size = (String) row.get(DirectoryTableModel.FILE_SIZE_COLUMN);

                    buffer.setLength(0);

                    int numberOfSpaces = largestSize - size.length();

                    for (int n2 = 0; n2 < numberOfSpaces; n2++)
                    {
                        buffer.append(' ');
                    }

                    buffer.append(size);

                    size = buffer.toString();

                    row.set(DirectoryTableModel.FILE_SIZE_COLUMN, size);
                }
            }
        }

        fireTableDataChanged();
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

                return (fileName.endsWith(m_fileExtension));
            }

            return false;

        }
    }
}