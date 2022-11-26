/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;

class TextRendererTest extends AbstractRendererTest {

    @Override
    Renderer getRenderer() {
        return new TextRenderer();
    }

    @Override
    String getExpected() {
        return getSourceCodeFilename() + ":1:\tFoo:\tblah" + PMD.EOL;
    }

    @Override
    String getExpectedEmpty() {
        return "";
    }

    @Override
    String getExpectedMultiple() {
        return getSourceCodeFilename() + ":1:\tFoo:\tblah" + PMD.EOL
                + getSourceCodeFilename() + ":1:\tBoo:\tblah" + PMD.EOL;
    }

    @Override
    String getExpectedError(ProcessingError error) {
        return "file\t-\tRuntimeException: Error" + PMD.EOL;
    }

    @Override
    String getExpectedErrorWithoutMessage(ProcessingError error) {
        return "file\t-\tNullPointerException: null" + PMD.EOL;
    }

    @Override
    String getExpectedError(ConfigurationError error) {
        return "Foo\t-\ta configuration error" + PMD.EOL;
    }
}
