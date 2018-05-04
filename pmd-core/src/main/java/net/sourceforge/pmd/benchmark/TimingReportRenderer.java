/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.benchmark;

import java.io.IOException;
import java.io.Writer;

/**
 * Defines a renderer for {@link TimingReport}.
 * @author Juan Martín Sotuyo Dodero
 */
public interface TimingReportRenderer {

    /**
     * Renders the given report into the given writer.
     * @param report The report data to render
     * @param writer The writer on which to render
     * @throws IOException if the write operation fails
     */
    void render(TimingReport report, Writer writer) throws IOException;
}
