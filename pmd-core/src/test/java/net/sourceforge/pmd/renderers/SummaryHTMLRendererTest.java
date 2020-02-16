/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

public class SummaryHTMLRendererTest extends AbstractRendererTest {

    @Override
    public Renderer getRenderer() {
        Renderer result = new SummaryHTMLRenderer();
        result.setProperty(HTMLRenderer.LINK_PREFIX, "link_prefix");
        result.setProperty(HTMLRenderer.LINE_PREFIX, "line_prefix");
        return result;
    }

    @Override
    protected String getSourceCodeFilename() {
        return "notAvailable";
    }

    @Override
    public String getExpected() {
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
    public String getExpectedEmpty() {
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
    public String getExpectedMultiple() {
        return "<html><head><title>PMD</title></head><body>" + PMD.EOL + "<center><h2>Summary</h2></center>" + PMD.EOL
                + "<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">" + PMD.EOL
                + "<tr><th>Rule name</th><th>Number of violations</th></tr>" + PMD.EOL
                + "<tr><td>Foo</td><td align=center>2</td></tr>" + PMD.EOL + "</table>" + PMD.EOL
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
    public String getExpectedError(ProcessingError error) {
        return "<html><head><title>PMD</title></head><body>" + PMD.EOL + "<center><h2>Summary</h2></center>" + PMD.EOL
                + "<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">" + PMD.EOL
                + "<tr><th>Rule name</th><th>Number of violations</th></tr>" + PMD.EOL + "</table>" + PMD.EOL
                + "<center><h2>Detail</h2></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL
                + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL
                + "</table><hr/><center><h3>Processing errors</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>File</th><th>Problem</th></tr>" + PMD.EOL + "<tr bgcolor=\"lightgrey\"> " + PMD.EOL
                + "<td>file</td>" + PMD.EOL + "<td><pre>" + error.getDetail() + "</pre></td>" + PMD.EOL + "</tr>" + PMD.EOL
                + "</table></tr></table></body></html>" + PMD.EOL;
    }

    @Override
    public String getExpectedError(ConfigurationError error) {
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
    public void testShowSuppressions() throws Exception {
        Report rep = createEmptyReportWithSuppression();
        Renderer renderer = getRenderer();
        renderer.setShowSuppressedViolations(true);
        String actual = ReportTest.render(renderer, rep);
        assertEquals("<html><head><title>PMD</title></head><body>" + PMD.EOL + "<center><h2>Summary</h2></center>"
                + PMD.EOL + "<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">" + PMD.EOL
                + "<tr><th>Rule name</th><th>Number of violations</th></tr>" + PMD.EOL + "</table>" + PMD.EOL
                + "<center><h2>Detail</h2></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL
                + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL
                + "</table><hr/><center><h3>Suppressed warnings</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>File</th><th>Line</th><th>Rule</th><th>NOPMD or Annotation</th><th>Reason</th></tr>"
                + PMD.EOL + "<tr bgcolor=\"lightgrey\"> " + PMD.EOL + "<td align=\"left\"></td>" + PMD.EOL
                + "<td align=\"center\">1</td>" + PMD.EOL + "<td align=\"center\">Foo</td>" + PMD.EOL
                + "<td align=\"center\">NOPMD</td>" + PMD.EOL + "<td align=\"center\">test</td>" + PMD.EOL + "</tr>"
                + PMD.EOL + "</table></tr></table></body></html>" + PMD.EOL, actual);
    }

    @Test
    public void testHideSuppressions() throws Exception {
        Report rep = createEmptyReportWithSuppression();
        Renderer renderer = getRenderer();
        renderer.setShowSuppressedViolations(false);
        String actual = ReportTest.render(renderer, rep);
        assertEquals(getExpectedEmpty(), actual);
    }

    private Report createEmptyReportWithSuppression() {
        Report rep = new Report();
        Map<Integer, String> suppressions = new HashMap<>();
        suppressions.put(1, "test");
        rep.suppress(suppressions);
        RuleContext ctx = new RuleContext();
        ParametricRuleViolation<Node> violation = new ParametricRuleViolation<>(new FooRule(), ctx, null,
                "suppress test");
        violation.setLines(1, 1);
        rep.addRuleViolation(violation);
        return rep;
    }
}
