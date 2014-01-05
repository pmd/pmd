/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.viewer.gui;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.viewer.model.ViewerModel;
import net.sourceforge.pmd.util.viewer.model.ViewerModelEvent;
import net.sourceforge.pmd.util.viewer.model.ViewerModelListener;

/**
 * A panel showing XPath expression evaluation results
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class EvaluationResultsPanel extends JPanel implements ViewerModelListener {
    private ViewerModel model;
    private JList list;

    /**
     * constructs the panel
     *
     * @param model model to refer to
     */
    public EvaluationResultsPanel(ViewerModel model) {
        super(new BorderLayout());

        this.model = model;

        init();
    }

    private void init() {
        model.addViewerModelListener(this);

        list = new JList();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (list.getSelectedValue() != null) {
                    model.selectNode((Node) list.getSelectedValue(), EvaluationResultsPanel.this);
                }
            }
        });

        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    /**
     * @see ViewerModelListener#viewerModelChanged(ViewerModelEvent)
     */
    @SuppressWarnings("PMD.UseArrayListInsteadOfVector")
    public void viewerModelChanged(ViewerModelEvent e) {
        switch (e.getReason()) {
            case ViewerModelEvent.PATH_EXPRESSION_EVALUATED:

                if (e.getSource() != this) {
                    list.setListData(new Vector(model.getLastEvaluationResults()));
                }

                break;

            case ViewerModelEvent.CODE_RECOMPILED:
                list.setListData(new Vector(0));

                break;
            default:
        	// Do nothing
                break;
        }
    }
}
