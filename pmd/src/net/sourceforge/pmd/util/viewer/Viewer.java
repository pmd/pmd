package net.sourceforge.pmd.util.viewer;

import net.sourceforge.pmd.util.viewer.gui.MainFrame;


/**
 * viewer's starter
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class Viewer {
    /**
     * starts the viewer
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        new MainFrame();
    }
}


/*
 * $Log$
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
