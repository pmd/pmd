/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.viewer.model;

/**
 * identiefie a listener of the ViewerModel
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
@Deprecated // to be removed with PMD 7.0.0
public interface ViewerModelListener {
    void viewerModelChanged(ViewerModelEvent e);
}
