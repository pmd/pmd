/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.InternalApiBridge;
import net.sourceforge.pmd.reporting.Report.ConfigurationError;
import net.sourceforge.pmd.reporting.Report.ProcessingError;
import net.sourceforge.pmd.reporting.RuleContext;

class SummaryHTMLRendererTest extends AbstractRendererTest {

    @Override
    Renderer getRenderer() {
        Renderer result = new SummaryHTMLRenderer();
        result.setProperty(HTMLRenderer.LINK_PREFIX, "link_prefix");
        result.setProperty(HTMLRenderer.LINE_PREFIX, Optional.of("line_prefix"));
        result.setProperty(HTMLRenderer.HTML_EXTENSION, true);
        return result;
    }

    @Override
    protected String getSourceCodeFilename() {
        return "notAvailable";
    }

    @Override
    String getExpected() {
        return "<html><head><title>PMD</title></head><body>" + EOL + "<center><h2>Summary</h2></center>" + EOL
                + "<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">" + EOL
                + "<tr><th>Rule name</th><th>Number of violations</th></tr>" + EOL
                + "<tr><td>Foo</td><td align=center>1</td></tr>" + EOL + "</table>" + EOL
                + "<center><h2>Detail</h2></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + EOL
                + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + EOL
                + "<tr bgcolor=\"lightgrey\"> " + EOL + "<td align=\"center\">1</td>" + EOL
                + "<td width=\"*%\"><a href=\"link_prefix" + getSourceCodeFilename() + ".html#line_prefix1\">" + getSourceCodeFilename() + "</a></td>" + EOL
                + "<td align=\"center\" width=\"5%\">1</td>" + EOL + "<td width=\"*\">blah</td>" + EOL + "</tr>"
                + EOL + "</table></tr></table></body></html>" + EOL;

    }

    @Override
    String getExpectedEmpty() {
        return "<html><head><title>PMD</title></head><body>" + EOL + "<center><h2>Summary</h2></center>" + EOL
                + "<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">" + EOL
                + "<tr><th>Rule name</th><th>Number of violations</th></tr>" + EOL + "</table>" + EOL
                + "<center><h2>Detail</h2></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + EOL
                + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + EOL
                + "</table></tr></table></body></html>" + EOL;
    }

    @Override
    String getExpectedMultiple() {
        return "<html><head><title>PMD</title></head><body>" + EOL + "<center><h2>Summary</h2></center>" + EOL
                + "<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">" + EOL
                + "<tr><th>Rule name</th><th>Number of violations</th></tr>" + EOL
                + "<tr><td>Foo</td><td align=center>1</td></tr>" + EOL
                + "<tr><td>Boo</td><td align=center>1</td></tr>" + EOL + "</table>" + EOL
                + "<center><h2>Detail</h2></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + EOL
                + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + EOL
                + "<tr bgcolor=\"lightgrey\"> " + EOL + "<td align=\"center\">1</td>" + EOL
                + "<td width=\"*%\"><a href=\"link_prefix" + getSourceCodeFilename() + ".html#line_prefix1\">" + getSourceCodeFilename() + "</a></td>" + EOL
                + "<td align=\"center\" width=\"5%\">1</td>" + EOL + "<td width=\"*\">blah</td>" + EOL + "</tr>"
                + EOL + "<tr> " + EOL + "<td align=\"center\">2</td>" + EOL
                + "<td width=\"*%\"><a href=\"link_prefix" + getSourceCodeFilename() + ".html#line_prefix1\">" + getSourceCodeFilename() + "</a></td>" + EOL
                + "<td align=\"center\" width=\"5%\">1</td>" + EOL + "<td width=\"*\">blah</td>" + EOL + "</tr>"
                + EOL + "</table></tr></table></body></html>" + EOL;
    }

    @Override
    String getExpectedError(ProcessingError error) {
        return "<html><head><title>PMD</title></head><body>" + EOL + "<center><h2>Summary</h2></center>" + EOL
                + "<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">" + EOL
                + "<tr><th>Rule name</th><th>Number of violations</th></tr>" + EOL + "</table>" + EOL
                + "<center><h2>Detail</h2></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + EOL
                + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + EOL
                + "</table><hr/><center><h3>Processing errors</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + EOL + "<th>File</th><th>Problem</th></tr>" + EOL + "<tr bgcolor=\"lightgrey\"> " + EOL
                + "<td><a href=\"link_prefixfile.html#\">file</a></td>" + EOL + "<td><pre>" + error.getDetail() + "</pre></td>" + EOL + "</tr>" + EOL
                + "</table></tr></table></body></html>" + EOL;
    }

    @Override
    String getExpectedError(ConfigurationError error) {
        return "<html><head><title>PMD</title></head><body>" + EOL + "<center><h2>Summary</h2></center>" + EOL
                + "<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">" + EOL
                + "<tr><th>Rule name</th><th>Number of violations</th></tr>" + EOL + "</table>" + EOL
                + "<center><h2>Detail</h2></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + EOL
                + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + EOL
                + "</table><hr/><center><h3>Configuration errors</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + EOL + "<th>Rule</th><th>Problem</th></tr>" + EOL + "<tr bgcolor=\"lightgrey\"> " + EOL
                + "<td>Foo</td>" + EOL + "<td>a configuration error</td>" + EOL + "</tr>" + EOL
                + "</table></tr></table></body></html>" + EOL;
    }

    @Test
    void testShowSuppressions() throws Exception {
        Renderer renderer = getRenderer();
        renderer.setShowSuppressedViolations(true);
        String actual = renderReport(renderer, createEmptyReportWithSuppression());
        assertEquals("<html><head><title>PMD</title></head><body>" + EOL + "<center><h2>Summary</h2></center>"
                + EOL + "<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">" + EOL
                + "<tr><th>Rule name</th><th>Number of violations</th></tr>" + EOL + "</table>" + EOL
                + "<center><h2>Detail</h2></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + EOL
                + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + EOL
                + "</table><hr/><center><h3>Suppressed warnings</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + EOL + "<th>File</th><th>Line</th><th>Rule</th><th>NOPMD or Annotation</th><th>Reason</th></tr>"
                         + EOL + "<tr bgcolor=\"lightgrey\"> " + EOL + "<td align=\"left\"><a href=\"link_prefix" + getSourceCodeFilename() + ".html#line_prefix1\">" + getSourceCodeFilename() + "</a></td>" + EOL
                + "<td align=\"center\">1</td>" + EOL + "<td align=\"center\">Foo</td>" + EOL
                         + "<td align=\"center\">//NOPMD</td>" + EOL + "<td align=\"center\">test</td>" + EOL
                         + "</tr>"
                + EOL + "</table></tr></table></body></html>" + EOL, actual);
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

            RuleContext ruleContext = InternalApiBridge.createRuleContext(listener, new FooRule());
            ruleContext.addViolationWithPosition(root, 1, 1, "suppress test");
        };
    }
}
