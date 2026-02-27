/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.Report.ConfigurationError;
import net.sourceforge.pmd.reporting.Report.ProcessingError;
import net.sourceforge.pmd.reporting.RuleViolation;
import net.sourceforge.pmd.reporting.ViolationSuppressor;

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

    private String getEscapedRuleMessage() {
        return "This should be escaped: &quot;&lt;script&gt;alert('test')&lt;/script&gt;&quot;.";
    }

    @Override
    protected RuleViolation newRuleViolation(int beginLine, int beginColumn, int endLine, int endColumn, Rule rule) {
        FileLocation loc = createLocation(beginLine, beginColumn, endLine, endColumn);
        return newRuleViolation(rule, loc, "This should be escaped: \"<script>alert('test')</script>\".");
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
                + "<td width=\"*\">" + getEscapedRuleMessage() + "</td>" + EOL + "</tr>" + EOL + "</table></body></html>" + EOL;
    }

    @Override
    String getExpectedEmpty() {
        return getHeader()
                + "</table></body></html>" + EOL;
    }

    @Override
    String getExpectedMultiple() {
        String ruleDescription = getEscapedRuleMessage();
        return getHeader()
                + "<tr bgcolor=\"lightgrey\"> " + EOL + "<td align=\"center\">1</td>" + EOL
                + "<td width=\"*%\">" + getEscapedFilename() + "</td>" + EOL + "<td align=\"center\" width=\"5%\">1</td>" + EOL
                + "<td width=\"*\">" + ruleDescription + "</td>" + EOL + "</tr>" + EOL + "<tr> " + EOL
                + "<td align=\"center\">2</td>" + EOL + "<td width=\"*%\">" + getEscapedFilename() + "</td>" + EOL
                + "<td align=\"center\" width=\"5%\">1</td>" + EOL + "<td width=\"*\">" + ruleDescription + "</td>" + EOL + "</tr>"
                + EOL + "</table></body></html>" + EOL;
    }

    private String getExpectedSuppressed() {
        return getHeader()
                + "</table><hr/><center><h3>Suppressed warnings</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + EOL
                + "<th>File</th><th>Line</th><th>Rule</th><th>NOPMD or Annotation</th><th>Reason</th></tr>" + EOL
                + "<tr bgcolor=\"lightgrey\"> " + EOL
                + "<td align=\"left\">someFilename&nbsp;thatNeedsEscaping.ext</td>" + EOL
                + "<td align=\"center\">1</td>" + EOL
                + "<td align=\"center\">Foo</td>" + EOL
                + "<td align=\"center\">//NOPMD</td>" + EOL
                + "<td align=\"center\">userMessage should be &lt;script&gt;alert('escaped')&lt;/script&gt;</td>" + EOL
                + "</tr>" + EOL
                + "</table></body></html>" + EOL;
    }

    @Test
    void testRendererSuppressed() throws Exception {
        String actual = renderReport(getRenderer(), it -> {
            RuleViolation ruleViolation = newRuleViolation(1, 1, 1, 1, createFooRule());
            Report.SuppressedViolation suppressedViolation = new Report.SuppressedViolation(ruleViolation,
                    ViolationSuppressor.NOPMD_COMMENT_SUPPRESSOR, "userMessage should be <script>alert('escaped')</script>");
            it.onSuppressedRuleViolation(suppressedViolation);
        });
        assertEquals(filter(getExpectedSuppressed()), filter(actual));
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
        final String linkPrefix = "https://github.com/pmd/pmd/blob/main/";
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
        final String linkPrefix = "https://github.com/pmd/pmd/blob/main/";
        renderer.setProperty(HTMLRenderer.LINK_PREFIX, linkPrefix);
        // dont set line prefix renderer.setProperty(HTMLRenderer.LINE_PREFIX, linePrefix);
        renderer.setProperty(HTMLRenderer.HTML_EXTENSION, false);

        String actual = renderReport(renderer, reportOneViolation());
        assertEquals(filter(getExpected(linkPrefix, "")), filter(actual));
    }

    @Test
    void testEmptyLinePrefix() throws IOException {
        final HTMLRenderer renderer = new HTMLRenderer();
        final String linkPrefix = "https://github.com/pmd/pmd/blob/main/";
        renderer.setProperty(HTMLRenderer.LINK_PREFIX, linkPrefix);
        renderer.setProperty(HTMLRenderer.LINE_PREFIX, Optional.of(""));
        renderer.setProperty(HTMLRenderer.HTML_EXTENSION, false);

        String actual = renderReport(renderer, reportOneViolation());
        assertEquals(filter(getExpected(linkPrefix, "1")), filter(actual));
    }
}
