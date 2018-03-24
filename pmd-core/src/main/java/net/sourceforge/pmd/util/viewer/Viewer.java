/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.viewer;

import net.sourceforge.pmd.lang.xpath.Initializer;
import net.sourceforge.pmd.util.viewer.gui.MainFrame;

/**
 * viewer's starter
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
@Deprecated // to be removed with PMD 7.0.0
public final class Viewer {

    private Viewer() { }

    public static void main(String[] args) {
        Initializer.initialize();
        new MainFrame();
    }
}
