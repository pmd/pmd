package test.net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.renderers.AbstractRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;

public class HTMLRendererTest extends AbstractRendererTst {

    public AbstractRenderer getRenderer() {
        return new HTMLRenderer();
    }

    public String getExpected() {
        return "<html><head><title>PMD</title></head><body>" + PMD.EOL +
        "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL +
        "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL +
        "<tr bgcolor=\"lightgrey\"> " + PMD.EOL + "<td align=\"center\">1</td>" + PMD.EOL + "<td width=\"*%\">n/a</td>" + PMD.EOL +
        "<td align=\"center\" width=\"5%\">1</td>" + PMD.EOL +
        "<td width=\"*\">msg</td>" + PMD.EOL +
        "</tr>" + PMD.EOL +
        "</table></body></html>";
    }

    public String getExpectedEmpty() {
        return "<html><head><title>PMD</title></head><body>" + PMD.EOL +
        "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL +
        "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL +
        "</table></body></html>";
    }

    public String getExpectedMultiple() {
        return "<html><head><title>PMD</title></head><body>" + PMD.EOL +
        "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL +
        "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL +
        "<tr bgcolor=\"lightgrey\"> " + PMD.EOL + "<td align=\"center\">1</td>" + PMD.EOL + "<td width=\"*%\">n/a</td>" + PMD.EOL +
        "<td align=\"center\" width=\"5%\">1</td>" + PMD.EOL +
        "<td width=\"*\">msg</td>" + PMD.EOL +
        "</tr>" + PMD.EOL +
        "<tr> " + PMD.EOL + "<td align=\"center\">2</td>" + PMD.EOL + "<td width=\"*%\">n/a</td>" + PMD.EOL +
        "<td align=\"center\" width=\"5%\">1</td>" + PMD.EOL +
        "<td width=\"*\">msg</td>" + PMD.EOL +
        "</tr>" + PMD.EOL +
        "</table></body></html>";
    }
    
    public String getExpectedError(ProcessingError error) {
        return "<html><head><title>PMD</title></head><body>" + PMD.EOL +
            "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL +
            "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL +
            "</table><hr/><center><h3>Processing errors</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>" + PMD.EOL +
            "<th>File</th><th>Problem</th></tr>" + PMD.EOL +
            "<tr bgcolor=\"lightgrey\"> " + PMD.EOL +
            "<td>file</td>" + PMD.EOL +
            "<td>Error</td>" + PMD.EOL +
            "</tr>" + PMD.EOL +
            "</table></body></html>";        
    }
}

