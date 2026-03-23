/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.reporting.Report.ConfigurationError;
import net.sourceforge.pmd.reporting.Report.ProcessingError;
import net.sourceforge.pmd.reporting.RuleViolation;

class VBHTMLRendererTest extends AbstractRendererTest {

    @Override
    Renderer getRenderer() {
        return new VBHTMLRenderer();
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
    String getExpected() {
        return "<html><head><title>PMD</title></head><style type=\"text/css\"><!--" + EOL
                + "body { background-color: white; font-family:verdana, arial, helvetica, geneva; font-size: 16px; font-style: italic; color: black; }"
                + EOL
                + ".title { font-family: verdana, arial, helvetica,geneva; font-size: 12px; font-weight:bold; color: white; }"
                + EOL
                + ".body { font-family: verdana, arial, helvetica, geneva; font-size: 12px; font-weight:plain; color: black; }"
                + EOL + "#TableHeader { background-color: #003366; }" + EOL
                + "#RowColor1 { background-color: #eeeeee; }" + EOL + "#RowColor2 { background-color: white; }"
                + EOL
                + "--></style><body><center><table border=\"0\" width=\"80%\"><tr id=TableHeader><td colspan=\"2\"><font class=title>&nbsp;" + getSourceCodeFilename() + "</font></tr>"
                + EOL
                + "<tr id=RowColor2><td width=\"50\" align=\"right\"><font class=body>1&nbsp;&nbsp;&nbsp;</font></td><td><font class=body>" + getEscapedRuleMessage() + "</font></td></tr>"
                + EOL + "</table><br></center></body></html>" + EOL;
    }

    @Override
    String getExpectedEmpty() {
        return "<html><head><title>PMD</title></head><style type=\"text/css\"><!--" + EOL
                + "body { background-color: white; font-family:verdana, arial, helvetica, geneva; font-size: 16px; font-style: italic; color: black; }"
                + EOL
                + ".title { font-family: verdana, arial, helvetica,geneva; font-size: 12px; font-weight:bold; color: white; }"
                + EOL
                + ".body { font-family: verdana, arial, helvetica, geneva; font-size: 12px; font-weight:plain; color: black; }"
                + EOL + "#TableHeader { background-color: #003366; }" + EOL
                + "#RowColor1 { background-color: #eeeeee; }" + EOL + "#RowColor2 { background-color: white; }"
                + EOL + "--></style><body><center><br></center></body></html>" + EOL;
    }

    @Override
    String getExpectedMultiple() {
        String ruleDescription = getEscapedRuleMessage();
        return "<html><head><title>PMD</title></head><style type=\"text/css\"><!--" + EOL
                + "body { background-color: white; font-family:verdana, arial, helvetica, geneva; font-size: 16px; font-style: italic; color: black; }"
                + EOL
                + ".title { font-family: verdana, arial, helvetica,geneva; font-size: 12px; font-weight:bold; color: white; }"
                + EOL
                + ".body { font-family: verdana, arial, helvetica, geneva; font-size: 12px; font-weight:plain; color: black; }"
                + EOL + "#TableHeader { background-color: #003366; }" + EOL
                + "#RowColor1 { background-color: #eeeeee; }" + EOL + "#RowColor2 { background-color: white; }"
                + EOL
                + "--></style><body><center><table border=\"0\" width=\"80%\"><tr id=TableHeader><td colspan=\"2\"><font class=title>&nbsp;" + getSourceCodeFilename() + "</font></tr>"
                + EOL
                + "<tr id=RowColor2><td width=\"50\" align=\"right\"><font class=body>1&nbsp;&nbsp;&nbsp;</font></td><td><font class=body>" + ruleDescription + "</font></td></tr>"
                + EOL
                + "<tr id=RowColor1><td width=\"50\" align=\"right\"><font class=body>1&nbsp;&nbsp;&nbsp;</font></td><td><font class=body>" + ruleDescription + "</font></td></tr>"
                + EOL + "</table><br></center></body></html>" + EOL;
    }

    @Override
    String getExpectedError(ProcessingError error) {
        return "<html><head><title>PMD</title></head><style type=\"text/css\"><!--" + EOL
                + "body { background-color: white; font-family:verdana, arial, helvetica, geneva; font-size: 16px; font-style: italic; color: black; }"
                + EOL
                + ".title { font-family: verdana, arial, helvetica,geneva; font-size: 12px; font-weight:bold; color: white; }"
                + EOL
                + ".body { font-family: verdana, arial, helvetica, geneva; font-size: 12px; font-weight:plain; color: black; }"
                + EOL + "#TableHeader { background-color: #003366; }" + EOL
                + "#RowColor1 { background-color: #eeeeee; }" + EOL + "#RowColor2 { background-color: white; }"
                + EOL
                + "--></style><body><center><br><table border=\"0\" width=\"80%\"><tr id=TableHeader><td colspan=\"2\"><font class=title>&nbsp;Problems found</font></td></tr><tr id=RowColor2><td><font class=body>"
                + error.getFileId().getOriginalPath() + "</font></td><td><font class=body><pre>" + error.getDetail() + "</pre></font></td></tr></table></center></body></html>" + EOL;
    }

    @Override
    String getExpectedError(ConfigurationError error) {
        return "<html><head><title>PMD</title></head><style type=\"text/css\"><!--" + EOL
                + "body { background-color: white; font-family:verdana, arial, helvetica, geneva; font-size: 16px; font-style: italic; color: black; }"
                + EOL
                + ".title { font-family: verdana, arial, helvetica,geneva; font-size: 12px; font-weight:bold; color: white; }"
                + EOL
                + ".body { font-family: verdana, arial, helvetica, geneva; font-size: 12px; font-weight:plain; color: black; }"
                + EOL + "#TableHeader { background-color: #003366; }" + EOL
                + "#RowColor1 { background-color: #eeeeee; }" + EOL + "#RowColor2 { background-color: white; }"
                + EOL
                + "--></style><body><center><br><table border=\"0\" width=\"80%\"><tr id=TableHeader><td colspan=\"2\"><font class=title>&nbsp;Configuration problems found</font></td></tr><tr id=RowColor2><td><font class=body>"
                + error.rule().getName() + "</font></td><td><font class=body>" + error.issue() + "</font></td></tr></table></center></body></html>" + EOL;
    }
}
