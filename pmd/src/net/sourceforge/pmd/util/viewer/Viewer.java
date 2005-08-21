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
 * Revision 1.4  2005/08/21 19:29:45  tomcopeland
 * Adding Boris' viewer back into the repository; as long as someone is using it, removing it is not good
 *
 * Revision 1.1.1.1  2005/08/15 19:51:41  tomcopeland
 * Import of Boris Grushko's viewer code
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
