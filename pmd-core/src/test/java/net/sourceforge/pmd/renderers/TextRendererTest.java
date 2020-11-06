/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;

public class TextRendererTest extends AbstractRendererTest {

    @Override
    public Renderer getRenderer() {
        return new TextRenderer();
    }

    @Override
    public String getExpected() {
        return getSourceCodeFilename() + ":1:\tblah" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return "";
    }

    @Override
    public String getExpectedMultiple() {
        return getSourceCodeFilename() + ":1:\tblah" + PMD.EOL
                + getSourceCodeFilename() + ":1:\tblah" + PMD.EOL;
    }

    @Override
    public String getExpectedError(ProcessingError error) {
        return "file\t-\tRuntimeException: Error" + PMD.EOL;
    }

    @Override
    public String getExpectedErrorWithoutMessage(ProcessingError error) {
        return "file\t-\tNullPointerException: null" + PMD.EOL;
    }

    @Override
    public String getExpectedError(ConfigurationError error) {
        return "Foo\t-\ta configuration error" + PMD.EOL;
    }
}
