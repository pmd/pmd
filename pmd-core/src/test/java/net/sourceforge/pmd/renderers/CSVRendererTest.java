/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.reporting.Report.ConfigurationError;
import net.sourceforge.pmd.reporting.Report.ProcessingError;

class CSVRendererTest extends AbstractRendererTest {

    @Override
    Renderer getRenderer() {
        return new CSVRenderer();
    }

    @Override
    String getExpected() {
        return getHeader()
                + "\"1\",\"\",\"" + getSourceCodeFilename() + "\",\"5\",\"1\",\"blah\",\"RuleSet\",\"Foo\"" + EOL;
    }

    @Override
    String getExpectedEmpty() {
        return getHeader();
    }

    @Override
    String getExpectedMultiple() {
        return getHeader()
                + "\"1\",\"\",\"" + getSourceCodeFilename() + "\",\"5\",\"1\",\"blah\",\"RuleSet\",\"Foo\"" + EOL
                + "\"2\",\"\",\"" + getSourceCodeFilename() + "\",\"1\",\"1\",\"blah\",\"RuleSet\",\"Boo\"" + EOL;
    }

    @Override
    String getExpectedError(ProcessingError error) {
        return getHeader();
    }

    @Override
    String getExpectedError(ConfigurationError error) {
        return getHeader();
    }

    private String getHeader() {
        return "\"Problem\",\"Package\",\"File\",\"Priority\",\"Line\",\"Description\",\"Rule set\",\"Rule\"" + EOL;
    }
}
