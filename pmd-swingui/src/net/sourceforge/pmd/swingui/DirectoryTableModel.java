package net.sourceforge.pmd.swingui;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
class DirectoryTableModel extends DefaultTableModel {
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
    protected DirectoryTableModel(DirectoryTree directoryTree, String fileExtension) {
        super(createData(), createColumnNames());

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
        buffer.append(" lines");

        pattern = buffer.toString();
        m_decimalFormat = new DecimalFormat(pattern, decimalFormatSymbols);
        new DirectoryTreeSelectionListener(directoryTree);
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    private static Vector createData() {
        return new Vector();
    }

    /**
     ********************************************************************************
     *
     * @return
     */
    private static Vector createColumnNames() {
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
    protected File getFile(int row) {
        if ((row >= 0) && (row < getRowCount())) {
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
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class DirectoryTreeSelectionListener implements TreeSelectionListener {

        /**
         *************************************************************************
         *
         */
        private DirectoryTreeSelectionListener(DirectoryTree directoryTree) {
            directoryTree.addTreeSelectionListener(this);
        }

        /**
         ***************************************************************************
         *
         * @param directoryTree
         */
        public void valueChanged(TreeSelectionEvent event) {
            TreePath treePath = event.getPath();
            DirectoryTreeNode treeNode = (DirectoryTreeNode) treePath.getLastPathComponent();
            Object userObject = treeNode.getUserObject();
            Vector rows = getDataVector();

            for (int n = 0; n < rows.size(); n++) {
                ((Vector) rows.get(n)).clear();
            }

            rows.clear();

            if (userObject instanceof File) {
                File directory;
                File[] files;

                directory = (File) userObject;
                files = directory.listFiles(new FilesFilter());

                if (files != null) {
                    for (int n1 = 0; n1 < files.length; n1++) {
                        Vector row = new Vector(3);
                        String fileName = files[n1].getName();
                        int lineCount = countLines(files[n1]);
                        String size = m_decimalFormat.format(lineCount);
                        Date date = new Date(files[n1].lastModified());
                        String lastModified = m_dateFormat.format(date);

                        row.add(fileName);
                        row.add(size);
                        row.add(lastModified);
                        row.add(files[n1]);

                        rows.add(row);
                    }
                }
            }

            fireTableDataChanged();
        }

        /**
         **************************************************************************
         *
         * @param file
         *
         * @return
         */
        private int countLines(File file) {
            int lineCount = 0;
            InputStreamReader reader = null;

            try {
                char[] buffer;

                buffer = new char[10000];
                reader = new InputStreamReader(new FileInputStream(file));

                while (reader.ready()) {
                    int numberOfChars = reader.read(buffer);

                    for (int n = 0; n < numberOfChars; n++) {
                        if (buffer[n] == '\n') {
                            lineCount++;
                        }
                    }
                }
            } catch (FileNotFoundException exception) {
                lineCount = 0;
            } catch (IOException exception) {
                lineCount = 0;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException exception) {
                    }
                }
            }

            return lineCount;
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class FilesFilter implements FileFilter {

        public boolean accept(File file) {
            if (file.isFile() && (file.isHidden() == false)) {
                String fileName = file.getName().toLowerCase();

                return (fileName.endsWith(m_fileExtension));
            }

            return false;

        }
    }
}