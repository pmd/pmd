package net.sourceforge.pmd.util.viewer;

import net.sourceforge.pmd.jaxen.MatchesFunction;
import net.sourceforge.pmd.util.viewer.gui.MainFrame;

/**
 * viewer's starter
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class Viewer {
    public static void main(String[] args) {
        MatchesFunction.registerSelfInSimpleContext();
        new MainFrame();
    }
}
