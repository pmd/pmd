/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.renderer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import net.sourceforge.pmd.cpd.CPDReport;

public interface CPDReportRenderer {

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
