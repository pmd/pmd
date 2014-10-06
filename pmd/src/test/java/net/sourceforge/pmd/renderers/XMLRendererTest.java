/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report.ProcessingError;

public class XMLRendererTest extends AbstractRendererTst {

    @Override
    public Renderer getRenderer() {
        return new XMLRenderer();
    }

    @Override
    public String getExpected() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
                "<pmd version=\"" + PMD.VERSION + "\" timestamp=\"2014-10-06T19:30:51.262\">\n" + 
                "<file name=\"n/a\">\n" + 
                "<violation beginline=\"1\" endline=\"1\" begincolumn=\"1\" endcolumn=\"1\" rule=\"Foo\" ruleset=\"RuleSet\" priority=\"5\">\n" + 
                "blah\n" + 
                "</violation>\n" + 
                "</file>\n" + 
                "</pmd>\n";
    }

    @Override
    public String getExpectedEmpty() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
                "<pmd version=\"" + PMD.VERSION + "\" timestamp=\"2014-10-06T19:30:51.262\">\n" + 
                "</pmd>\n";
    }

    @Override
    public String getExpectedMultiple() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
                "<pmd version=\"" + PMD.VERSION + "\" timestamp=\"2014-10-06T19:30:51.239\">\n" + 
                "<file name=\"n/a\">\n" + 
                "<violation beginline=\"1\" endline=\"1\" begincolumn=\"1\" endcolumn=\"1\" rule=\"Foo\" ruleset=\"RuleSet\" priority=\"5\">\n" + 
                "blah\n" + 
                "</violation>\n" + 
                "<violation beginline=\"1\" endline=\"1\" begincolumn=\"1\" endcolumn=\"2\" rule=\"Foo\" ruleset=\"RuleSet\" priority=\"5\">\n" + 
                "blah\n" + 
                "</violation>\n" + 
                "</file>\n" + 
                "</pmd>\n";
    }

    @Override
    public String getExpectedError(ProcessingError error) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
                "<pmd version=\"" + PMD.VERSION + "\" timestamp=\"2014-10-06T19:30:51.222\">\n" + 
                "<error filename=\"file\" msg=\"Error\"/>\n" + 
                "</pmd>\n";
    }

    @Override
    public String filter(String expected) {
        String result = expected.replaceAll(" timestamp=\"[^\"]+\">", " timestamp=\"\">");
        result = result.replaceAll("\r", "\n");
        return result;
    }
}
