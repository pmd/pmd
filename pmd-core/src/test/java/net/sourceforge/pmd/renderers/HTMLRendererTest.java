/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;

public class HTMLRendererTest extends AbstractRendererTest {

    @Override
    protected String getSourceCodeFilename() {
        return "someFilename<br>thatNeedsEscaping.ext";
    }

    final String style = "<link rel='stylesheet' type='text/css' href='https://cdn.datatables.net/1.12.1/css/jquery.dataTables.css'>" +
    "<link rel='stylesheet' type='text/css' href='https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/css/bootstrap.min.css'>"
    ;

    final String scripts = "<script src='https://code.jquery.com/jquery-3.6.1.min.js'></script> " +
    "<script type='text/javascript' charset='utf8' src='https://cdn.datatables.net/1.12.1/js/jquery.dataTables.js'></script>"
    ;
    final String setupTableScript = "<script>$(document).ready( function () { $('#pmdTable').DataTable(); } );</script>";
    final String tableClass = " class='table table-bordered table-hover table-striped'";
    final String tableId = " id='pmdTable'";


    private String getEscapedFilename() {
        return "someFilename&lt;br&gt;thatNeedsEscaping.ext";
    }

    @Override
    public Renderer getRenderer() {
        return new HTMLRenderer();
    }

    @Override
    public String getExpected() {
        return getExpected(null, null);
    }

    private String getExpected(String linkPrefix, String lineAnchor) {
        String filename = getEscapedFilename();
        if (linkPrefix != null) {
            filename = "<a href=\"" + linkPrefix + filename + "#" + lineAnchor + "\">"
                    + filename + "</a>";
        }

        return getHeader()
                + "<tr bgcolor=\"lightgrey\"> " + PMD.EOL + "<td align=\"center\">1</td>" + PMD.EOL
                + "<td width=\"*%\">" + filename + "</td>" + PMD.EOL + "<td align=\"center\" width=\"5%\">1</td>" + PMD.EOL
                + PMD.EOL + "<td align=\"center\" width=\"5%\">1</td>" + PMD.EOL
                + "<td width=\"*\">blah</td>" + PMD.EOL + "</tr>" + PMD.EOL + "</table>" + setupTableScript + "</body></html>" + PMD.EOL;

    }

    @Override
    public String getExpectedEmpty() {

        return getHeader()
                + "</table>" + setupTableScript + "</body></html>" + PMD.EOL;
    }

    @Override
    public String getExpectedMultiple() {
        return getHeader()
                + "<tr bgcolor=\"lightgrey\"> " + PMD.EOL + "<td align=\"center\">1</td>" + PMD.EOL
                + "<td width=\"*%\">" + getEscapedFilename() + "</td>" + PMD.EOL + "<td align=\"center\" width=\"5%\">1</td>" + PMD.EOL
                + "<td width=\"*\">blah</td>" + PMD.EOL + "</tr>" + PMD.EOL + "<tr> " + PMD.EOL
                + "<td align=\"center\">2</td>" + PMD.EOL + "<td width=\"*%\">" + getEscapedFilename() + "</td>" + PMD.EOL
                + "<td align=\"center\" width=\"5%\">1</td>" + PMD.EOL + "<td align=\"center\" width=\"5%\">1</td>" + PMD.EOL +  "<td width=\"*\">blah</td>" + PMD.EOL + "</tr>"
                + PMD.EOL + "</table>" + setupTableScript + "</body></html>" + PMD.EOL;
    }

    @Override
    public String getExpectedError(ProcessingError error) {
        return getHeader()
                + "</table><hr/><center><h3>Processing errors</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>File</th><th>Problem</th></tr>" + PMD.EOL + "<tr bgcolor=\"lightgrey\"> " + PMD.EOL
                + "<td>file</td>" + PMD.EOL + "<td><pre>" + error.getDetail() + "</pre></td>" + PMD.EOL + "</tr>" + PMD.EOL + "</table></body></html>"
                + PMD.EOL;
    }

    @Override
    public String getExpectedError(ConfigurationError error) {
        return getHeader()
                + "</table><hr/><center><h3>Configuration errors</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>Rule</th><th>Problem</th></tr>" + PMD.EOL + "<tr bgcolor=\"lightgrey\"> " + PMD.EOL
                + "<td>Foo</td>" + PMD.EOL + "<td>a configuration error</td>" + PMD.EOL + "</tr>" + PMD.EOL + "</table></body></html>"
                + PMD.EOL;
    }

    private String getHeader() {
        return "<html><head><title>PMD</title>" + style + scripts + "</head><body>"  + PMD.EOL
                + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table " + tableId + tableClass + " align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>#</th><th>File</th><th>Line</th><th>Priority</th><th>Problem</th></tr>" + PMD.EOL;
    }

    @Test
    public void testLinkPrefix() throws IOException {
        final HTMLRenderer renderer = new HTMLRenderer();
        final String linkPrefix = "https://github.com/pmd/pmd/blob/master/";
        final String linePrefix = "L";
        renderer.setProperty(HTMLRenderer.LINK_PREFIX, linkPrefix);
        renderer.setProperty(HTMLRenderer.LINE_PREFIX, linePrefix);
        renderer.setProperty(HTMLRenderer.HTML_EXTENSION, false);

        Report rep = reportOneViolation();
        String actual = renderReport(renderer, rep);
        assertEquals(filter(getExpected(linkPrefix, "L1")), filter(actual));
    }

    @Test
    public void testLinePrefixNotSet() throws IOException {
        final HTMLRenderer renderer = new HTMLRenderer();
        final String linkPrefix = "https://github.com/pmd/pmd/blob/master/";
        renderer.setProperty(HTMLRenderer.LINK_PREFIX, linkPrefix);
        // dont set line prefix renderer.setProperty(HTMLRenderer.LINE_PREFIX, linePrefix);
        renderer.setProperty(HTMLRenderer.HTML_EXTENSION, false);

        Report rep = reportOneViolation();
        String actual = renderReport(renderer, rep);
        assertEquals(filter(getExpected(linkPrefix, "")), filter(actual));
    }

    @Test
    public void testEmptyLinePrefix() throws IOException {
        final HTMLRenderer renderer = new HTMLRenderer();
        final String linkPrefix = "https://github.com/pmd/pmd/blob/master/";
        renderer.setProperty(HTMLRenderer.LINK_PREFIX, linkPrefix);
        renderer.setProperty(HTMLRenderer.LINE_PREFIX, "");
        renderer.setProperty(HTMLRenderer.HTML_EXTENSION, false);

        Report rep = reportOneViolation();
        String actual = renderReport(renderer, rep);
        assertEquals(filter(getExpected(linkPrefix, "1")), filter(actual));
    }
}
