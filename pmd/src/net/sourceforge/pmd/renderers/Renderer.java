/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 12:13:03 PM
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.Report;

public interface Renderer {
    String render(Report report);
}
