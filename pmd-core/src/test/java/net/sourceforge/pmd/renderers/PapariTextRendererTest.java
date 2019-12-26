/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.StringReader;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;

public class PapariTextRendererTest extends AbstractRendererTest {

    @Override
    public Renderer getRenderer() {
        TextColorRenderer result = new TextColorRenderer() {
            @Override
            protected Reader getReader(String sourceFile) throws FileNotFoundException {
                return new StringReader("public class Foo {}");
            }
        };
        result.setProperty(TextColorRenderer.COLOR, "false");
        return result;
    }

    @Override
    public String getExpected() {
        return "* file: " + getSourceCodeFilename() + PMD.EOL + "    src:  " + getSourceCodeFilename() + ":1:1" + PMD.EOL + "    rule: Foo" + PMD.EOL
                + "    msg:  blah" + PMD.EOL + "    code: public class Foo {}" + PMD.EOL + PMD.EOL + PMD.EOL + PMD.EOL
                + "Summary:" + PMD.EOL + PMD.EOL + " : 1" + PMD.EOL + "* warnings: 1" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return PMD.EOL + PMD.EOL + "Summary:" + PMD.EOL + PMD.EOL + "* warnings: 0" + PMD.EOL;
    }

    @Override
    public String getExpectedMultiple() {
        return "* file: " + getSourceCodeFilename() + PMD.EOL + "    src:  " + getSourceCodeFilename() + ":1:1" + PMD.EOL + "    rule: Foo" + PMD.EOL
                + "    msg:  blah" + PMD.EOL + "    code: public class Foo {}" + PMD.EOL + PMD.EOL + "    src:  "
                + getSourceCodeFilename() + ":1:1" + PMD.EOL + "    rule: Foo" + PMD.EOL + "    msg:  blah" + PMD.EOL
                + "    code: public class Foo {}" + PMD.EOL + PMD.EOL + PMD.EOL + PMD.EOL + "Summary:" + PMD.EOL
                + PMD.EOL + " : 2" + PMD.EOL + "* warnings: 2" + PMD.EOL;
    }

    @Override
    public String getExpectedError(ProcessingError error) {
        return PMD.EOL + PMD.EOL + "Summary:" + PMD.EOL + PMD.EOL + "* file: file" + PMD.EOL + "    err:  RuntimeException: Error" + PMD.EOL
                + error.getDetail() + PMD.EOL + PMD.EOL
                + "* errors:   1" + PMD.EOL + "* warnings: 0" + PMD.EOL;
    }

    @Override
    public String getExpectedErrorWithoutMessage(ProcessingError error) {
        return PMD.EOL + PMD.EOL + "Summary:" + PMD.EOL + PMD.EOL + "* file: file" + PMD.EOL + "    err:  NullPointerException: null" + PMD.EOL
                + error.getDetail() + PMD.EOL + PMD.EOL
                + "* errors:   1" + PMD.EOL + "* warnings: 0" + PMD.EOL;
    }

    @Override
    public String getExpectedError(ConfigurationError error) {
        return PMD.EOL + PMD.EOL + "Summary:" + PMD.EOL + PMD.EOL + "* rule: Foo" + PMD.EOL
                + "    err:  a configuration error" + PMD.EOL + PMD.EOL
                + "* errors:   1" + PMD.EOL + "* warnings: 0" + PMD.EOL;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(PapariTextRendererTest.class);
    }
}
