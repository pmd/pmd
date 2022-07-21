/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.renderer;

import java.io.IOException;
import java.io.Writer;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.cpd.CPDReport;

/**
 * Adapter to convert an old {@link CPDRenderer} into a {@link CPDReportRenderer}.
 *
 * @deprecated This is internal API. If you want to write your own renderer, directly implement
 * {@link CPDReportRenderer}.
 */
@Deprecated
@InternalApi
public class CPDRendererAdapter implements CPDReportRenderer {
    private final CPDRenderer renderer;

    public CPDRendererAdapter(final CPDRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void render(final CPDReport report, final Writer writer) throws IOException {
        renderer.render(report.getMatches().iterator(), writer);
    }
}
