/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.viewer.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.viewer.gui.menu.ASTNodePopupMenu;
import net.sourceforge.pmd.util.viewer.model.ASTModel;
import net.sourceforge.pmd.util.viewer.model.ViewerModel;
import net.sourceforge.pmd.util.viewer.model.ViewerModelEvent;
import net.sourceforge.pmd.util.viewer.model.ViewerModelListener;
import net.sourceforge.pmd.util.viewer.util.NLS;

/**
 * tree panel GUI
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */

public class ASTPanel extends JPanel implements ViewerModelListener, TreeSelectionListener {
    private ViewerModel model;
    private JTree tree;

    /**
     * constructs the panel
     *
     * @param model model to attach the panel to
     */
    public ASTPanel(ViewerModel model) {
        this.model = model;
        init();
    }

    private void init() {
        model.addViewerModelListener(this);
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), NLS.nls("AST.PANEL.TITLE")));
        setLayout(new BorderLayout());
        tree = new JTree((TreeNode) null);
        tree.addTreeSelectionListener(this);
        tree.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
                    tree.setSelectionPath(path);
                    JPopupMenu menu = new ASTNodePopupMenu(model, (Node) path.getLastPathComponent());
                    menu.show(tree, e.getX(), e.getY());
                }
            }
        });

        add(new JScrollPane(tree), BorderLayout.CENTER);
    }

    /**
     * @see ViewerModelListener#viewerModelChanged(ViewerModelEvent)
     */
    public void viewerModelChanged(ViewerModelEvent e) {
        switch (e.getReason()) {
            case ViewerModelEvent.CODE_RECOMPILED:
                tree.setModel(new ASTModel(model.getRootNode()));
                break;
            case ViewerModelEvent.NODE_SELECTED:
                if (e.getSource() != this) {
                    List<Node> list = new ArrayList<>();
                    for (Node n = (Node) e.getParameter(); n != null; n = n.jjtGetParent()) {
                        list.add(n);
                    }
                    Collections.reverse(list);
                    TreePath path = new TreePath(list.toArray());
                    tree.setSelectionPath(path);
                    tree.scrollPathToVisible(path);
                }
                break;
            default:
        	// Do nothing
        	break;
        }
    }

    /**
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    public void valueChanged(TreeSelectionEvent e) {
        model.selectNode((Node) e.getNewLeadSelectionPath().getLastPathComponent(), this);
    }
}
