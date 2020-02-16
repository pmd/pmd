/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;

public class HTMLRendererTest extends AbstractRendererTest {

    @Override
    protected String getSourceCodeFilename() {
        return "someFilename<br>thatNeedsEscaping.ext";
    }

    private String getEscapedFilename() {
        return "someFilename&lt;br&gt;thatNeedsEscaping.ext";
    }

    @Override
    public Renderer getRenderer() {
        return new HTMLRenderer();
    }

    @Override
    public String getExpected() {
        return getHeader()
                + "<tr bgcolor=\"lightgrey\"> " + PMD.EOL + "<td align=\"center\">1</td>" + PMD.EOL
                + "<td width=\"*%\">" + getEscapedFilename() + "</td>" + PMD.EOL + "<td align=\"center\" width=\"5%\">1</td>" + PMD.EOL
                + "<td width=\"*\">blah</td>" + PMD.EOL + "</tr>" + PMD.EOL + "</table></body></html>" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return getHeader()
                + "</table></body></html>" + PMD.EOL;
    }

    @Override
    public String getExpectedMultiple() {
        return getHeader()
                + "<tr bgcolor=\"lightgrey\"> " + PMD.EOL + "<td align=\"center\">1</td>" + PMD.EOL
                + "<td width=\"*%\">" + getEscapedFilename() + "</td>" + PMD.EOL + "<td align=\"center\" width=\"5%\">1</td>" + PMD.EOL
                + "<td width=\"*\">blah</td>" + PMD.EOL + "</tr>" + PMD.EOL + "<tr> " + PMD.EOL
                + "<td align=\"center\">2</td>" + PMD.EOL + "<td width=\"*%\">" + getEscapedFilename() + "</td>" + PMD.EOL
                + "<td align=\"center\" width=\"5%\">1</td>" + PMD.EOL + "<td width=\"*\">blah</td>" + PMD.EOL + "</tr>"
                + PMD.EOL + "</table></body></html>" + PMD.EOL;
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
        return "<html><head><title>PMD</title></head><body>" + PMD.EOL
                + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL;
    }
}
