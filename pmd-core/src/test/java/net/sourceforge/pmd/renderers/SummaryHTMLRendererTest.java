/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.reporting.FileAnalysisListener;

class SummaryHTMLRendererTest extends AbstractRendererTest {

    @Override
    Renderer getRenderer() {
        Renderer result = new SummaryHTMLRenderer();
        result.setProperty(HTMLRenderer.LINK_PREFIX, "link_prefix");
        result.setProperty(HTMLRenderer.LINE_PREFIX, "line_prefix");
        result.setProperty(HTMLRenderer.HTML_EXTENSION, true);
        return result;
    }

    @Override
    protected String getSourceCodeFilename() {
        return "notAvailable";
    }

    @Override
    String getExpected() {
        return "<html><head><title>PMD</title></head><body>" + PMD.EOL + "<center><h2>Summary</h2></center>" + PMD.EOL
                + "<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">" + PMD.EOL
                + "<tr><th>Rule name</th><th>Number of violations</th></tr>" + PMD.EOL
                + "<tr><td>Foo</td><td align=center>1</td></tr>" + PMD.EOL + "</table>" + PMD.EOL
                + "<center><h2>Detail</h2></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL
                + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL
                + "<tr bgcolor=\"lightgrey\"> " + PMD.EOL + "<td align=\"center\">1</td>" + PMD.EOL
                + "<td width=\"*%\"><a href=\"link_prefix" + getSourceCodeFilename() + ".html#line_prefix1\">" + getSourceCodeFilename() + "</a></td>" + PMD.EOL
                + "<td align=\"center\" width=\"5%\">1</td>" + PMD.EOL + "<td width=\"*\">blah</td>" + PMD.EOL + "</tr>"
                + PMD.EOL + "</table></tr></table></body></html>" + PMD.EOL;

    }

    @Override
    String getExpectedEmpty() {
        return "<html><head><title>PMD</title></head><body>" + PMD.EOL + "<center><h2>Summary</h2></center>" + PMD.EOL
                + "<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">" + PMD.EOL
                + "<tr><th>Rule name</th><th>Number of violations</th></tr>" + PMD.EOL + "</table>" + PMD.EOL
                + "<center><h2>Detail</h2></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL
                + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL
                + "</table></tr></table></body></html>" + PMD.EOL;
    }

    @Override
    String getExpectedMultiple() {
        return "<html><head><title>PMD</title></head><body>" + PMD.EOL + "<center><h2>Summary</h2></center>" + PMD.EOL
                + "<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">" + PMD.EOL
                + "<tr><th>Rule name</th><th>Number of violations</th></tr>" + PMD.EOL
                + "<tr><td>Boo</td><td align=center>1</td></tr>" + PMD.EOL
                + "<tr><td>Foo</td><td align=center>1</td></tr>" + PMD.EOL + "</table>" + PMD.EOL
                + "<center><h2>Detail</h2></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL
                + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL
                + "<tr bgcolor=\"lightgrey\"> " + PMD.EOL + "<td align=\"center\">1</td>" + PMD.EOL
                + "<td width=\"*%\"><a href=\"link_prefix" + getSourceCodeFilename() + ".html#line_prefix1\">" + getSourceCodeFilename() + "</a></td>" + PMD.EOL
                + "<td align=\"center\" width=\"5%\">1</td>" + PMD.EOL + "<td width=\"*\">blah</td>" + PMD.EOL + "</tr>"
                + PMD.EOL + "<tr> " + PMD.EOL + "<td align=\"center\">2</td>" + PMD.EOL
                + "<td width=\"*%\"><a href=\"link_prefix" + getSourceCodeFilename() + ".html#line_prefix1\">" + getSourceCodeFilename() + "</a></td>" + PMD.EOL
                + "<td align=\"center\" width=\"5%\">1</td>" + PMD.EOL + "<td width=\"*\">blah</td>" + PMD.EOL + "</tr>"
                + PMD.EOL + "</table></tr></table></body></html>" + PMD.EOL;
    }

    @Override
    String getExpectedError(ProcessingError error) {
        return "<html><head><title>PMD</title></head><body>" + PMD.EOL + "<center><h2>Summary</h2></center>" + PMD.EOL
                + "<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">" + PMD.EOL
                + "<tr><th>Rule name</th><th>Number of violations</th></tr>" + PMD.EOL + "</table>" + PMD.EOL
                + "<center><h2>Detail</h2></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL
                + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL
                + "</table><hr/><center><h3>Processing errors</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>File</th><th>Problem</th></tr>" + PMD.EOL + "<tr bgcolor=\"lightgrey\"> " + PMD.EOL
                + "<td><a href=\"link_prefixfile.html#\">file</a></td>" + PMD.EOL + "<td><pre>" + error.getDetail() + "</pre></td>" + PMD.EOL + "</tr>" + PMD.EOL
                + "</table></tr></table></body></html>" + PMD.EOL;
    }

    @Override
    String getExpectedError(ConfigurationError error) {
        return "<html><head><title>PMD</title></head><body>" + PMD.EOL + "<center><h2>Summary</h2></center>" + PMD.EOL
                + "<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">" + PMD.EOL
                + "<tr><th>Rule name</th><th>Number of violations</th></tr>" + PMD.EOL + "</table>" + PMD.EOL
                + "<center><h2>Detail</h2></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL
                + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL
                + "</table><hr/><center><h3>Configuration errors</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>Rule</th><th>Problem</th></tr>" + PMD.EOL + "<tr bgcolor=\"lightgrey\"> " + PMD.EOL
                + "<td>Foo</td>" + PMD.EOL + "<td>a configuration error</td>" + PMD.EOL + "</tr>" + PMD.EOL
                + "</table></tr></table></body></html>" + PMD.EOL;
    }

    @Test
    void testShowSuppressions() throws Exception {
        Renderer renderer = getRenderer();
        renderer.setShowSuppressedViolations(true);
        String actual = renderReport(renderer, createEmptyReportWithSuppression());
        assertEquals("<html><head><title>PMD</title></head><body>" + PMD.EOL + "<center><h2>Summary</h2></center>"
                + PMD.EOL + "<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">" + PMD.EOL
                + "<tr><th>Rule name</th><th>Number of violations</th></tr>" + PMD.EOL + "</table>" + PMD.EOL
                + "<center><h2>Detail</h2></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL
                + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL
                + "</table><hr/><center><h3>Suppressed warnings</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>File</th><th>Line</th><th>Rule</th><th>NOPMD or Annotation</th><th>Reason</th></tr>"
                         + PMD.EOL + "<tr bgcolor=\"lightgrey\"> " + PMD.EOL + "<td align=\"left\"><a href=\"link_prefix" + getSourceCodeFilename() + ".html#line_prefix1\">" + getSourceCodeFilename() + "</a></td>" + PMD.EOL
                + "<td align=\"center\">1</td>" + PMD.EOL + "<td align=\"center\">Foo</td>" + PMD.EOL
                         + "<td align=\"center\">//NOPMD</td>" + PMD.EOL + "<td align=\"center\">test</td>" + PMD.EOL
                         + "</tr>"
                + PMD.EOL + "</table></tr></table></body></html>" + PMD.EOL, actual);
    }

    @Test
    void testHideSuppressions() throws Exception {
        Renderer renderer = getRenderer();
        renderer.setShowSuppressedViolations(false);
        String actual = renderReport(renderer, createEmptyReportWithSuppression());
        assertEquals(getExpectedEmpty(), actual);
    }

    private Consumer<FileAnalysisListener> createEmptyReportWithSuppression() {
        return listener -> {
            DummyRootNode root = helper.parse("dummy code", getSourceCodeFilename())
                                       .withNoPmdComments(Collections.singletonMap(1, "test"));

            RuleContext ruleContext = RuleContext.create(listener, new FooRule());
            ruleContext.addViolationWithPosition(root, 1, 1, "suppress test");
        };
    }
}
