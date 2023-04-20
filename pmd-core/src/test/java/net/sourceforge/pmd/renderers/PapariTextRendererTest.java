/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.StringReader;

import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;

class PapariTextRendererTest extends AbstractRendererTest {

    @Override
    Renderer getRenderer() {
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
    String getExpected() {
        return "* file: " + getSourceCodeFilename() + EOL + "    src:  " + getSourceCodeFilename() + ":1:1" + EOL + "    rule: Foo" + EOL
                + "    msg:  blah" + EOL + "    code: public class Foo {}" + EOL + EOL + EOL + EOL
                + "Summary:" + EOL + EOL + "* warnings: 1" + EOL;
    }

    @Override
    String getExpectedEmpty() {
        return EOL + EOL + "Summary:" + EOL + EOL + "* warnings: 0" + EOL;
    }

    @Override
    String getExpectedMultiple() {
        return "* file: " + getSourceCodeFilename() + EOL + "    src:  " + getSourceCodeFilename() + ":1:1" + EOL + "    rule: Foo" + EOL
                + "    msg:  blah" + EOL + "    code: public class Foo {}" + EOL + EOL + "    src:  "
                + getSourceCodeFilename() + ":1:1" + EOL + "    rule: Boo" + EOL + "    msg:  blah" + EOL
                + "    code: public class Foo {}" + EOL + EOL + EOL + EOL + "Summary:" + EOL
                + EOL + "* warnings: 2" + EOL;
    }

    @Override
    String getExpectedError(ProcessingError error) {
        return EOL + EOL + "Summary:" + EOL + EOL + "* file: file" + EOL + "    err:  RuntimeException: Error" + EOL
                + error.getDetail() + EOL + EOL
                + "* errors:   1" + EOL + "* warnings: 0" + EOL;
    }

    @Override
    String getExpectedErrorWithoutMessage(ProcessingError error) {
        return EOL + EOL + "Summary:" + EOL + EOL + "* file: file" + EOL + "    err:  NullPointerException: null" + EOL
                + error.getDetail() + EOL + EOL
                + "* errors:   1" + EOL + "* warnings: 0" + EOL;
    }

    @Override
    String getExpectedError(ConfigurationError error) {
        return EOL + EOL + "Summary:" + EOL + EOL + "* rule: Foo" + EOL
                + "    err:  a configuration error" + EOL + EOL
                + "* errors:   1" + EOL + "* warnings: 0" + EOL;
    }
}
