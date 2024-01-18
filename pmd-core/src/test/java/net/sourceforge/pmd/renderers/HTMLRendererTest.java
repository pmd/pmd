/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.reporting.Report.ConfigurationError;
import net.sourceforge.pmd.reporting.Report.ProcessingError;

class HTMLRendererTest extends AbstractRendererTest {

    @Override
    protected String getSourceCodeFilename() {
        // note: the file name should still be a valid file name on both win and nix.
        // This precludes using chars like <> to test escaping (https://stackoverflow.com/questions/1976007/what-characters-are-forbidden-in-windows-and-linux-directory-names)
        return "someFilename\u00A0thatNeedsEscaping.ext";
    }

    private String getEscapedFilename() {
        return "someFilename&nbsp;thatNeedsEscaping.ext";
    }

    @Override
    Renderer getRenderer() {
        return new HTMLRenderer();
    }

    @Override
    String getExpected() {
        return getExpected(null, null);
    }

    private String getExpected(String linkPrefix, String lineAnchor) {
        String filename = getEscapedFilename();
        if (linkPrefix != null) {
            filename = "<a href=\"" + linkPrefix + filename + "#" + lineAnchor + "\">"
                    + filename + "</a>";
        }
        return getHeader()
                + "<tr bgcolor=\"lightgrey\"> " + EOL + "<td align=\"center\">1</td>" + EOL
                + "<td width=\"*%\">" + filename + "</td>" + EOL + "<td align=\"center\" width=\"5%\">1</td>" + EOL
                + "<td width=\"*\">blah</td>" + EOL + "</tr>" + EOL + "</table></body></html>" + EOL;
    }

    @Override
    String getExpectedEmpty() {
        return getHeader()
                + "</table></body></html>" + EOL;
    }

    @Override
    String getExpectedMultiple() {
        return getHeader()
                + "<tr bgcolor=\"lightgrey\"> " + EOL + "<td align=\"center\">1</td>" + EOL
                + "<td width=\"*%\">" + getEscapedFilename() + "</td>" + EOL + "<td align=\"center\" width=\"5%\">1</td>" + EOL
                + "<td width=\"*\">blah</td>" + EOL + "</tr>" + EOL + "<tr> " + EOL
                + "<td align=\"center\">2</td>" + EOL + "<td width=\"*%\">" + getEscapedFilename() + "</td>" + EOL
                + "<td align=\"center\" width=\"5%\">1</td>" + EOL + "<td width=\"*\">blah</td>" + EOL + "</tr>"
                + EOL + "</table></body></html>" + EOL;
    }

    @Override
    String getExpectedError(ProcessingError error) {
        return getHeader()
                + "</table><hr/><center><h3>Processing errors</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + EOL + "<th>File</th><th>Problem</th></tr>" + EOL + "<tr bgcolor=\"lightgrey\"> " + EOL
                + "<td>file</td>" + EOL + "<td><pre>" + error.getDetail() + "</pre></td>" + EOL + "</tr>" + EOL + "</table></body></html>"
                + EOL;
    }

    @Override
    String getExpectedError(ConfigurationError error) {
        return getHeader()
                + "</table><hr/><center><h3>Configuration errors</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + EOL + "<th>Rule</th><th>Problem</th></tr>" + EOL + "<tr bgcolor=\"lightgrey\"> " + EOL
                + "<td>Foo</td>" + EOL + "<td>a configuration error</td>" + EOL + "</tr>" + EOL + "</table></body></html>"
                + EOL;
    }

    private String getHeader() {
        return "<html><head><title>PMD</title></head><body>" + EOL
                + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + EOL;
    }

    @Test
    void testLinkPrefix() throws IOException {
        final HTMLRenderer renderer = new HTMLRenderer();
        final String linkPrefix = "https://github.com/pmd/pmd/blob/master/";
        final String linePrefix = "L";
        renderer.setProperty(HTMLRenderer.LINK_PREFIX, linkPrefix);
        renderer.setProperty(HTMLRenderer.LINE_PREFIX, Optional.of(linePrefix));
        renderer.setProperty(HTMLRenderer.HTML_EXTENSION, false);

        String actual = renderReport(renderer, reportOneViolation());
        assertEquals(filter(getExpected(linkPrefix, "L1")), filter(actual));
    }

    @Test
    void testLinePrefixNotSet() throws IOException {
        final HTMLRenderer renderer = new HTMLRenderer();
        final String linkPrefix = "https://github.com/pmd/pmd/blob/master/";
        renderer.setProperty(HTMLRenderer.LINK_PREFIX, linkPrefix);
        // dont set line prefix renderer.setProperty(HTMLRenderer.LINE_PREFIX, linePrefix);
        renderer.setProperty(HTMLRenderer.HTML_EXTENSION, false);

        String actual = renderReport(renderer, reportOneViolation());
        assertEquals(filter(getExpected(linkPrefix, "")), filter(actual));
    }

    @Test
    void testEmptyLinePrefix() throws IOException {
        final HTMLRenderer renderer = new HTMLRenderer();
        final String linkPrefix = "https://github.com/pmd/pmd/blob/master/";
        renderer.setProperty(HTMLRenderer.LINK_PREFIX, linkPrefix);
        renderer.setProperty(HTMLRenderer.LINE_PREFIX, Optional.of(""));
        renderer.setProperty(HTMLRenderer.HTML_EXTENSION, false);

        String actual = renderReport(renderer, reportOneViolation());
        assertEquals(filter(getExpected(linkPrefix, "1")), filter(actual));
    }
}
