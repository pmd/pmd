/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * An empty renderer, for when you really don't want a report.
 */
public class EmptyRenderer extends AbstractRenderer {
    public static final String NAME = "empty";

    public EmptyRenderer() {
        super(NAME, "Empty, nothing.");
    }

    @Override
    public String defaultFileExtension() {
        return "";
    }

    @Override
    public void start() throws IOException {
        // deliberately does nothing
    }

    @Override
    public void startFileAnalysis(DataSource dataSource) {
        // deliberately does nothing
    }

    @Override
    public void renderFileReport(Report report) throws IOException {
        // deliberately does nothing
    }

    @Override
    public void end() throws IOException {
        // deliberately does nothing
    }
}
