/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;

public class TextRendererTest extends AbstractRendererTst {

    @Override
    public Renderer getRenderer() {
        return new TextRenderer();
    }

    @Override
    public String getExpected() {
        return "n/a:1:\tblah" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return "";
    }

    @Override
    public String getExpectedMultiple() {
        return "n/a:1:\tblah" + PMD.EOL + "n/a:1:\tblah" + PMD.EOL;
    }

    @Override
    public String getExpectedError(ProcessingError error) {
        return "file\t-\tError" + PMD.EOL;
    }

    @Override
    public String getExpectedError(ConfigurationError error) {
        return "Foo\t-\ta configuration error" + PMD.EOL;
    }
}
