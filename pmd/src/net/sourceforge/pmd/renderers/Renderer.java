/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.Report;

public interface Renderer {
    String render(Report report);
}
