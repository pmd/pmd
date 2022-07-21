/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.renderer;

import java.io.IOException;
import java.io.Writer;

import net.sourceforge.pmd.cpd.CPDReport;

public interface CPDReportRenderer {
    void render(CPDReport report, Writer writer) throws IOException;
}
