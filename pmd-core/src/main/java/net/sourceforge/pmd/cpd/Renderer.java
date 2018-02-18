/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Iterator;

import net.sourceforge.pmd.cpd.renderer.CPDRenderer;

/**
 * @author Philippe T'Seyen
 * @deprecated Use {@link CPDRenderer} instead
 */
@Deprecated // to be removed with 7.0.0
public interface Renderer {
    String render(Iterator<Match> matches);
}
