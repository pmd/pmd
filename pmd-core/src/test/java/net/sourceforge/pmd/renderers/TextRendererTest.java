/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.reporting.Report.ConfigurationError;
import net.sourceforge.pmd.reporting.Report.ProcessingError;

class TextRendererTest extends AbstractRendererTest {

    @Override
    Renderer getRenderer() {
        return new TextRenderer();
    }

    @Override
    String getExpected() {
        return getSourceCodeFilename() + ":1:\tFoo:\tblah" + EOL;
    }

    @Override
    String getExpectedEmpty() {
        return "";
    }

    @Override
    String getExpectedMultiple() {
        return getSourceCodeFilename() + ":1:\tFoo:\tblah" + EOL
                + getSourceCodeFilename() + ":1:\tBoo:\tblah" + EOL;
    }

    @Override
    String getExpectedError(ProcessingError error) {
        return "file\t-\tRuntimeException: Error" + EOL;
    }

    @Override
    String getExpectedErrorWithoutMessage(ProcessingError error) {
        return "file\t-\tNullPointerException: null" + EOL;
    }

    @Override
    String getExpectedError(ConfigurationError error) {
        return "Foo\t-\ta configuration error" + EOL;
    }
}
