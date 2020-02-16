/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;

public class CSVRendererTest extends AbstractRendererTest {

    @Override
    public Renderer getRenderer() {
        return new CSVRenderer();
    }

    @Override
    public String getExpected() {
        return getHeader()
                + "\"1\",\"\",\"" + getSourceCodeFilename() + "\",\"5\",\"1\",\"blah\",\"RuleSet\",\"Foo\"" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return getHeader();
    }

    @Override
    public String getExpectedMultiple() {
        return getHeader()
                + "\"1\",\"\",\"" + getSourceCodeFilename() + "\",\"5\",\"1\",\"blah\",\"RuleSet\",\"Foo\"" + PMD.EOL
                + "\"2\",\"\",\"" + getSourceCodeFilename() + "\",\"5\",\"1\",\"blah\",\"RuleSet\",\"Foo\"" + PMD.EOL;
    }

    @Override
    public String getExpectedError(ProcessingError error) {
        return getHeader();
    }

    @Override
    public String getExpectedError(ConfigurationError error) {
        return getHeader();
    }

    private String getHeader() {
        return "\"Problem\",\"Package\",\"File\",\"Priority\",\"Line\",\"Description\",\"Rule set\",\"Rule\"" + PMD.EOL;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CSVRendererTest.class);
    }
}
