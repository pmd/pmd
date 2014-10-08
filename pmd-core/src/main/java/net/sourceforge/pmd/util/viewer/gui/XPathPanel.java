/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.viewer.gui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import net.sourceforge.pmd.util.viewer.model.ViewerModel;
import net.sourceforge.pmd.util.viewer.model.ViewerModelEvent;
import net.sourceforge.pmd.util.viewer.model.ViewerModelListener;
import net.sourceforge.pmd.util.viewer.util.NLS;


/**
 * Panel for the XPath entry and editing
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */

public class XPathPanel extends JTabbedPane implements ViewerModelListener {
    private ViewerModel model;
    private JTextArea xPathArea;

    /**
     * Constructs the panel
     *
     * @param model model to refer to
     */
    public XPathPanel(ViewerModel model) {
        super(JTabbedPane.BOTTOM);
        this.model = model;
        init();
    }

    private void init() {
        model.addViewerModelListener(this);
        xPathArea = new JTextArea();
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), NLS.nls("XPATH.PANEL.TITLE")));
        add(new JScrollPane(xPathArea), NLS.nls("XPATH.PANEL.EXPRESSION"));
        add(new EvaluationResultsPanel(model), NLS.nls("XPATH.PANEL.RESULTS"));
        setPreferredSize(new Dimension(-1, 200));
    }

    public String getXPathExpression() {
        return xPathArea.getText();
    }

    /**
     * @see ViewerModelListener#viewerModelChanged(ViewerModelEvent)
     */
    public void viewerModelChanged(ViewerModelEvent e) {
        switch (e.getReason()) {
            case ViewerModelEvent.PATH_EXPRESSION_APPENDED:
                if (e.getSource() != this) {
                    xPathArea.append((String) e.getParameter());
                }
                setSelectedIndex(0);
                break;
            case ViewerModelEvent.CODE_RECOMPILED:
                setSelectedIndex(0);
                break;
            default:
        	// Do nothing
                break;
        }
    }
}