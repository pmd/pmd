/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report.ProcessingError;

public class CSVRendererTest extends AbstractRendererTst {

    public Renderer getRenderer() {
        return new CSVRenderer();
    }

    public String getExpected() {
        return "\"Problem\",\"Package\",\"File\",\"Priority\",\"Line\",\"Description\",\"Rule set\",\"Rule\"" + PMD.EOL
                + "\"1\",\"\",\"n/a\",\"5\",\"1\",\"blah\",\"RuleSet\",\"Foo\"" + PMD.EOL;
    }

    public String getExpectedEmpty() {
        return "\"Problem\",\"Package\",\"File\",\"Priority\",\"Line\",\"Description\",\"Rule set\",\"Rule\"" + PMD.EOL;
    }

    public String getExpectedMultiple() {
        return "\"Problem\",\"Package\",\"File\",\"Priority\",\"Line\",\"Description\",\"Rule set\",\"Rule\"" + PMD.EOL
        + "\"1\",\"\",\"n/a\",\"5\",\"1\",\"blah\",\"RuleSet\",\"Foo\"" + PMD.EOL 
        + "\"2\",\"\",\"n/a\",\"5\",\"1\",\"blah\",\"RuleSet\",\"Foo\"" + PMD.EOL;
    }

    public String getExpectedError(ProcessingError error) {
        return "\"Problem\",\"Package\",\"File\",\"Priority\",\"Line\",\"Description\",\"Rule set\",\"Rule\"" + PMD.EOL;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CSVRendererTest.class);
    }
}
