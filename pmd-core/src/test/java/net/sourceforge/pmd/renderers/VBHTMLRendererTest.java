/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;

public class VBHTMLRendererTest extends AbstractRendererTest {

    @Override
    public Renderer getRenderer() {
        return new VBHTMLRenderer();
    }

    @Override
    public String getExpected() {
        return "<html><head><title>PMD</title></head><style type=\"text/css\"><!--" + PMD.EOL
                + "body { background-color: white; font-family:verdana, arial, helvetica, geneva; font-size: 16px; font-style: italic; color: black; }"
                + PMD.EOL
                + ".title { font-family: verdana, arial, helvetica,geneva; font-size: 12px; font-weight:bold; color: white; }"
                + PMD.EOL
                + ".body { font-family: verdana, arial, helvetica, geneva; font-size: 12px; font-weight:plain; color: black; }"
                + PMD.EOL + "#TableHeader { background-color: #003366; }" + PMD.EOL
                + "#RowColor1 { background-color: #eeeeee; }" + PMD.EOL + "#RowColor2 { background-color: white; }"
                + PMD.EOL
                + "--></style><body><center><table border=\"0\" width=\"80%\"><tr id=TableHeader><td colspan=\"2\"><font class=title>&nbsp;" + getSourceCodeFilename() + "</font></tr>"
                + PMD.EOL
                + "<tr id=RowColor2><td width=\"50\" align=\"right\"><font class=body>1&nbsp;&nbsp;&nbsp;</font></td><td><font class=body>blah</font></td></tr>"
                + PMD.EOL + "</table><br></center></body></html>" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return "<html><head><title>PMD</title></head><style type=\"text/css\"><!--" + PMD.EOL
                + "body { background-color: white; font-family:verdana, arial, helvetica, geneva; font-size: 16px; font-style: italic; color: black; }"
                + PMD.EOL
                + ".title { font-family: verdana, arial, helvetica,geneva; font-size: 12px; font-weight:bold; color: white; }"
                + PMD.EOL
                + ".body { font-family: verdana, arial, helvetica, geneva; font-size: 12px; font-weight:plain; color: black; }"
                + PMD.EOL + "#TableHeader { background-color: #003366; }" + PMD.EOL
                + "#RowColor1 { background-color: #eeeeee; }" + PMD.EOL + "#RowColor2 { background-color: white; }"
                + PMD.EOL + "--></style><body><center><br></center></body></html>" + PMD.EOL;
    }

    @Override
    public String getExpectedMultiple() {
        return "<html><head><title>PMD</title></head><style type=\"text/css\"><!--" + PMD.EOL
                + "body { background-color: white; font-family:verdana, arial, helvetica, geneva; font-size: 16px; font-style: italic; color: black; }"
                + PMD.EOL
                + ".title { font-family: verdana, arial, helvetica,geneva; font-size: 12px; font-weight:bold; color: white; }"
                + PMD.EOL
                + ".body { font-family: verdana, arial, helvetica, geneva; font-size: 12px; font-weight:plain; color: black; }"
                + PMD.EOL + "#TableHeader { background-color: #003366; }" + PMD.EOL
                + "#RowColor1 { background-color: #eeeeee; }" + PMD.EOL + "#RowColor2 { background-color: white; }"
                + PMD.EOL
                + "--></style><body><center><table border=\"0\" width=\"80%\"><tr id=TableHeader><td colspan=\"2\"><font class=title>&nbsp;" + getSourceCodeFilename() + "</font></tr>"
                + PMD.EOL
                + "<tr id=RowColor2><td width=\"50\" align=\"right\"><font class=body>1&nbsp;&nbsp;&nbsp;</font></td><td><font class=body>blah</font></td></tr>"
                + PMD.EOL
                + "<tr id=RowColor1><td width=\"50\" align=\"right\"><font class=body>1&nbsp;&nbsp;&nbsp;</font></td><td><font class=body>blah</font></td></tr>"
                + PMD.EOL + "</table><br></center></body></html>" + PMD.EOL;
    }

    @Override
    public String getExpectedError(ProcessingError error) {
        return "<html><head><title>PMD</title></head><style type=\"text/css\"><!--" + PMD.EOL
                + "body { background-color: white; font-family:verdana, arial, helvetica, geneva; font-size: 16px; font-style: italic; color: black; }"
                + PMD.EOL
                + ".title { font-family: verdana, arial, helvetica,geneva; font-size: 12px; font-weight:bold; color: white; }"
                + PMD.EOL
                + ".body { font-family: verdana, arial, helvetica, geneva; font-size: 12px; font-weight:plain; color: black; }"
                + PMD.EOL + "#TableHeader { background-color: #003366; }" + PMD.EOL
                + "#RowColor1 { background-color: #eeeeee; }" + PMD.EOL + "#RowColor2 { background-color: white; }"
                + PMD.EOL
                + "--></style><body><center><br><table border=\"0\" width=\"80%\"><tr id=TableHeader><td colspan=\"2\"><font class=title>&nbsp;Problems found</font></td></tr><tr id=RowColor2><td><font class=body>"
                + error.getFile() + "</font></td><td><font class=body><pre>" + error.getDetail() + "</pre></font></td></tr></table></center></body></html>" + PMD.EOL;
    }

    @Override
    public String getExpectedError(ConfigurationError error) {
        return "<html><head><title>PMD</title></head><style type=\"text/css\"><!--" + PMD.EOL
                + "body { background-color: white; font-family:verdana, arial, helvetica, geneva; font-size: 16px; font-style: italic; color: black; }"
                + PMD.EOL
                + ".title { font-family: verdana, arial, helvetica,geneva; font-size: 12px; font-weight:bold; color: white; }"
                + PMD.EOL
                + ".body { font-family: verdana, arial, helvetica, geneva; font-size: 12px; font-weight:plain; color: black; }"
                + PMD.EOL + "#TableHeader { background-color: #003366; }" + PMD.EOL
                + "#RowColor1 { background-color: #eeeeee; }" + PMD.EOL + "#RowColor2 { background-color: white; }"
                + PMD.EOL
                + "--></style><body><center><br><table border=\"0\" width=\"80%\"><tr id=TableHeader><td colspan=\"2\"><font class=title>&nbsp;Configuration problems found</font></td></tr><tr id=RowColor2><td><font class=body>"
                + error.rule().getName() + "</font></td><td><font class=body>" + error.issue() + "</font></td></tr></table></center></body></html>" + PMD.EOL;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(VBHTMLRendererTest.class);
    }
}
