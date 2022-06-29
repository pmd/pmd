/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.renderer;

import java.io.IOException;
import java.io.Writer;

import net.sourceforge.pmd.cpd.CPDReport;

public class CPDRendererAdapter implements CPDReportRenderer {
    private final CPDRenderer renderer;

    public CPDRendererAdapter(final CPDRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void render(final CPDReport report, final Writer writer) throws IOException {
        renderer.render(report.getMatches(), writer);
    }
}
