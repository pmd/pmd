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


/*
 * $Log$
 * Revision 1.1  2005/08/15 19:51:42  tomcopeland
 * Initial revision
 *
 * Revision 1.3  2004/12/22 20:52:12  tomcopeland
 * Fixing some stuff PMD found
 *
 * Revision 1.2  2004/09/27 19:42:52  tomcopeland
 * A ridiculously large checkin, but it's all just code reformatting.  Nothing to see here...
 *
 * Revision 1.1  2003/09/23 20:32:42  tomcopeland
 * Added Boris Gruschko's new AST/XPath viewer
 *
 * Revision 1.1  2003/09/24 01:33:03  bgr
 * moved to a new package
 *
 * Revision 1.1  2003/09/22 05:21:54  bgr
 * initial commit
 *
 */
