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
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + PMD.EOL
                + "<pmd version=\""
                + PMD.VERSION
                + "\" timestamp=\"2014-10-06T19:30:51.262\">"
                + PMD.EOL
                + "<file name=\"n/a\">"
                + PMD.EOL
                + "<violation beginline=\"1\" endline=\"1\" begincolumn=\"1\" endcolumn=\"1\" rule=\"Foo\" ruleset=\"RuleSet\" priority=\"5\">"
                + PMD.EOL + "blah" + PMD.EOL + "</violation>" + PMD.EOL + "</file>" + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + PMD.EOL + "<pmd version=\"" + PMD.VERSION
                + "\" timestamp=\"2014-10-06T19:30:51.262\">" + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    public String getExpectedMultiple() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + PMD.EOL
                + "<pmd version=\""
                + PMD.VERSION
                + "\" timestamp=\"2014-10-06T19:30:51.239\">"
                + PMD.EOL
                + "<file name=\"n/a\">"
                + PMD.EOL
                + "<violation beginline=\"1\" endline=\"1\" begincolumn=\"1\" endcolumn=\"1\" rule=\"Foo\" ruleset=\"RuleSet\" priority=\"5\">"
                + PMD.EOL
                + "blah"
                + PMD.EOL
                + "</violation>"
                + PMD.EOL
                + "<violation beginline=\"1\" endline=\"1\" begincolumn=\"1\" endcolumn=\"2\" rule=\"Foo\" ruleset=\"RuleSet\" priority=\"5\">"
                + PMD.EOL + "blah" + PMD.EOL + "</violation>" + PMD.EOL + "</file>" + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    public String getExpectedError(ProcessingError error) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + PMD.EOL + "<pmd version=\"" + PMD.VERSION
                + "\" timestamp=\"2014-10-06T19:30:51.222\">" + PMD.EOL + "<error filename=\"file\" msg=\"Error\"/>"
                + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    public String filter(String expected) {
        String result = expected.replaceAll(" timestamp=\"[^\"]+\">", " timestamp=\"\">");
        return result;
    }
}
