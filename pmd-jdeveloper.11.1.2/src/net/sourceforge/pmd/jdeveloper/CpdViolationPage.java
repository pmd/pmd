package net.sourceforge.pmd.jdeveloper;

import java.awt.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.TokenEntry;

import oracle.ide.ceditor.CodeEditor;
import oracle.ide.editor.EditorManager;
import oracle.ide.layout.ViewId;
import oracle.ide.log.AbstractLogPage;
import oracle.ide.model.Node;


public class CpdViolationPage extends AbstractLogPage implements TreeSelectionListener {

    private final transient JScrollPane scrollPane;
    private final transient JTree tree;
    private final transient DefaultMutableTreeNode top;
    public final transient Map cpdFileToNodeMap = 
        new HashMap(); // whew, this is kludgey

    public CpdViolationPage() {
        super(new ViewId("CpdPage", CpdAddin.CPD_TITLE), null, false);
        top = new DefaultMutableTreeNode("CPD");
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this);
        scrollPane = new JScrollPane(tree);
    }

    public void valueChanged(final TreeSelectionEvent event) {
        final DefaultMutableTreeNode node = 
            (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
        if (node != null && node.isLeaf()) {
            final CpdViolationWrapper nodeInfo = 
                (CpdViolationWrapper)node.getUserObject();
            EditorManager.getEditorManager().openDefaultEditorInFrame(nodeInfo.file.getURL());
            ((CodeEditor)EditorManager.getEditorManager().getCurrentEditor()).gotoLine(nodeInfo.mark.getBeginLine(), 
                                                                                       0, 
                                                                                       false);
        }
    }

    public void add(final Match match) {
        final Node file1 = 
            (Node)cpdFileToNodeMap.get(match.getFirstMark().getTokenSrcID());
        final DefaultMutableTreeNode matchNode = 
            new DefaultMutableTreeNode(file1.getShortLabel() + " contains a " + 
                                       match.getLineCount() + 
                                       " line block of duplicated code", true);
        top.add(matchNode);
        for (final Iterator i = match.iterator(); i.hasNext(); ) {
            final TokenEntry mark = (TokenEntry)i.next();
            final Node file = (Node)cpdFileToNodeMap.get(mark.getTokenSrcID());
            final DefaultMutableTreeNode markTreeNode = 
                new DefaultMutableTreeNode(new CpdViolationWrapper(mark, file, 
                                                                   file.getShortLabel() + 
                                                                   " has some at line " + 
                                                                   mark.getBeginLine()), 
                                           false);
            matchNode.add(markTreeNode);
        }
    }

    public Component getGUI() {
        return scrollPane;
    }

    public void clearAll() {
        top.removeAllChildren();
        tree.repaint();
        scrollPane.repaint();
        //tree.removeSelectionPath(new TreePath(new Object[] {top}));
    }
}
