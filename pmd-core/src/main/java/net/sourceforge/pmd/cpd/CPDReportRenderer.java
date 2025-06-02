/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Render a {@link CPDReport} to a file.
 *
 * @see CSVRenderer
 * @see XMLRenderer
 * @see SimpleRenderer
 * @see VSRenderer
 */
public interface CPDReportRenderer {

    /**
     * Write out the contents of the report to the given writer.
     *
     * @param report The report to write
     * @param writer A writer for the report file
     *
     * @throws IOException If the writer throws
     */
    void render(CPDReport report, Writer writer) throws IOException;


    /**
     * Call the other render method on a StringWriter. IO exceptions
     * are rethrown as runtime exceptions.
     */
    default String renderToString(CPDReport report) {
        StringWriter sw = new StringWriter();
        try {
            this.render(report, sw);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }
}
