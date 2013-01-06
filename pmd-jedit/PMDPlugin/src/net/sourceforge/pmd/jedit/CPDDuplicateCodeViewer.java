package net.sourceforge.pmd.jedit;

// Imports
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.GUIUtilities;

import java.awt.event.*;
import java.io.*;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
// End of Imports

/**
 *    A GUI Component to display Duplicate code.
 *
 *    @created 05 Apr 2003
 *    @author Jiger Patel
 *
 */

public class CPDDuplicateCodeViewer extends JPanel {
    JTree tree;
    DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("CPD Results", true));
    View view;

    public CPDDuplicateCodeViewer(View view) {
        this.view = view;
        setLayout(new BorderLayout());
        tree = new JTree(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (node != null && node.isLeaf() && node instanceof Duplicate) {
                    Duplicate duplicate = (Duplicate) node;
                    gotoDuplicate(duplicate);
                }
            }
        }
       );
        add(new JScrollPane(tree));

        JButton saveBtn = new JButton("Save");
        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        add(btnPanel, BorderLayout.SOUTH);
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String[] dirs = GUIUtilities.showVFSFileDialog(CPDDuplicateCodeViewer.this.view, System.getProperty("user.home"), VFSBrowser.SAVE_DIALOG, false);
                if (dirs != null && dirs.length > 0) {
                    String filename = dirs[0];
                    File f = new File(filename);
                    writeTree(f);
                }
            }
        }
       );

    }

    public void refreshTree() {
        treeModel.reload();
    }

    private void writeTree(File file) {
        try {
            String output = getTreeAsText();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(output);
            writer.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private String getTreeAsText() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("CPD Results\n");
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
            sb.append(child.toString()).append('\n');
            for (int j = 0; j < child.getChildCount(); j++) {
                sb.append('\t').append(child.getChildAt(j)).append('\n');
            }
        }
        return sb.toString();
    }

    public void gotoDuplicate(final Duplicate duplicate) {
        if (duplicate != null) {
            final Buffer buffer = jEdit.openFile(view, duplicate.getFilename());

            VFSManager.runInAWTThread(new Runnable() {
                public void run() {
                    view.setBuffer(buffer);

                    int start = buffer.getLineStartOffset(duplicate.getBeginLine() - 1);
                    int end = buffer.getLineEndOffset(Math.min(duplicate.getEndLine(), buffer.getLineCount() - 1));
                    // Log.log(Log.DEBUG, this.getClass(), "Start Line "+ duplicate.getBeginLine() + " End Line "+ duplicate.getEndLine() + " Start " + start + " End "+ end);
                    // Since an AIOOB Exception is thrown if the end is the end of file. we do a -1 from end to fix it.
                    view.getTextArea().setSelection(new Selection.Range(start, end - 1));
                    view.getTextArea().moveCaretPosition(start);
                }
            }
           );
        }
    }

    public DefaultMutableTreeNode getRoot() {
        return (DefaultMutableTreeNode) treeModel.getRoot();
    }

    public void addDuplicates(Duplicates duplicates) {
        // System.out.println("Inside addDuplicates " + duplicates +" Root child count "+ treeModel.getChildCount(treeModel.getRoot()));
        getRoot().add(duplicates);
        // vecDuplicates.addElement(duplicates);
    }

    public class Duplicates extends DefaultMutableTreeNode {
        // List vecduplicate = new ArrayList();
        String message, sourcecode;

        public Duplicates(String message, String sourcecode) {
            this.message = message;
            this.sourcecode = sourcecode;
        }

        public String getSourceCode() {
            return sourcecode;
        }

        public void addDuplicate(Duplicate duplicate) {
            add(duplicate);
            // vecduplicate.addElement(duplicate);
        }

        public String toString() {
            return message;
        }
    }

    public class Duplicate extends DefaultMutableTreeNode {
        private final String filename;
        private final int beginLine, endLine;

        public Duplicate(String filename, int beginLine, int endLine) {
            this.filename = filename;
            this.beginLine = beginLine;
            this.endLine = endLine;
        }

        public String getFilename() {
            return filename;
        }

        public int getBeginLine() {
            return beginLine;
        }

        public int getEndLine() {
            return endLine;
        }

        public String toString() {
            return filename + ":" + (getBeginLine()) + "-" + (getEndLine());
        }
    }

    public void expandAll() {
        int row = 0;
        while (row < tree.getRowCount()) {
            tree.expandRow(row);
            row++;
        }
    }

    public void collapseAll() {
        int row = tree.getRowCount() - 1;
        while (row >= 0) {
            tree.collapseRow(row);
            row--;
        }
    }

    public void clearDuplicates() {
        getRoot().removeAllChildren();
    }
}