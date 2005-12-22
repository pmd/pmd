package net.sourceforge.pmd.util.viewer;

import net.sourceforge.pmd.util.viewer.gui.MainFrame;
import net.sourceforge.pmd.jaxen.RegexpFunction;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.XPathFunctionContext;

/**
 * viewer's starter
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class Viewer {
    public static void main(String[] args) {
        ((SimpleFunctionContext)XPathFunctionContext.getInstance()).registerFunction(null, "regexp", new RegexpFunction());
        new MainFrame();
    }
}
