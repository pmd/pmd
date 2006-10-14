/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.renderers.AbstractRenderer;
import net.sourceforge.pmd.renderers.CSVRenderer;

public class CSVRendererTest extends AbstractRendererTst {

    public AbstractRenderer getRenderer() {
        return new CSVRenderer();
    }

    public String getExpected() {
        return "\"Problem\",\"Package\",\"File\",\"Priority\",\"Line\",\"Description\",\"Rule set\",\"Rule\"" + PMD.EOL
                + "\"1\",\"\",\"n/a\",\"5\",\"1\",\"msg\",\"RuleSet\",\"Foo\"" + PMD.EOL;
    }

    public String getExpectedEmpty() {
        return "\"Problem\",\"Package\",\"File\",\"Priority\",\"Line\",\"Description\",\"Rule set\",\"Rule\"" + PMD.EOL;
    }

    public String getExpectedMultiple() {
        return "\"Problem\",\"Package\",\"File\",\"Priority\",\"Line\",\"Description\",\"Rule set\",\"Rule\"" + PMD.EOL
        + "\"1\",\"\",\"n/a\",\"5\",\"1\",\"msg\",\"RuleSet\",\"Foo\"" + PMD.EOL 
        + "\"2\",\"\",\"n/a\",\"5\",\"1\",\"msg\",\"RuleSet\",\"Foo\"" + PMD.EOL;
    }

    public String getExpectedError(ProcessingError error) {
        return "\"Problem\",\"Package\",\"File\",\"Priority\",\"Line\",\"Description\",\"Rule set\",\"Rule\"" + PMD.EOL;
    }

}
