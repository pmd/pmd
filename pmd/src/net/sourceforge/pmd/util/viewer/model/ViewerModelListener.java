package net.sourceforge.pmd.util.viewer.model;

/**
 * identiefie a listener of the ViewerModel
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public interface ViewerModelListener {
    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    void viewerModelChanged(ViewerModelEvent e);
}
