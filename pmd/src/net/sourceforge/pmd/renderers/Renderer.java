/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.Report;

public interface Renderer {
    void showSuppressedViolations(boolean show);

    String render(Report report);
}
