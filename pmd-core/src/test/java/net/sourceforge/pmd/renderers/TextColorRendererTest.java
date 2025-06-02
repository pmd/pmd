/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.StringReader;

import net.sourceforge.pmd.reporting.Report.ConfigurationError;
import net.sourceforge.pmd.reporting.Report.ProcessingError;

class TextColorRendererTest extends AbstractRendererTest {

    @Override
    Renderer getRenderer() {
        TextColorRenderer result = new TextColorRenderer() {
            @Override
            protected Reader getReader(String sourceFile) throws FileNotFoundException {
                return new StringReader("public class Foo {}");
            }
        };
        return result;
    }

    @Override
    String getExpected() {
        return "\u001B[1;33m*\u001B[0m file: \u001B[1;37m" + getSourceCodeFilename() + "\u001B[0m" + EOL
            + "\u001B[0;32m    src:  \u001B[0;36m" + getSourceCodeFilename() + "\u001B[0m:\u001B[0;36m1:1\u001B[0m" + EOL
            + "\u001B[0;32m    rule: \u001B[0mFoo" + EOL
            + "\u001B[0;32m    msg:  \u001B[0mblah" + EOL
            + "\u001B[0;32m    code: \u001B[0mpublic class Foo {}" + EOL
            + EOL
            + EOL
            + EOL
            + "Summary:" + EOL
            + EOL
            + "\u001B[1;33m*\u001B[0m warnings: \u001B[1;37m1\u001B[0m" + EOL;
    }

    @Override
    String getExpectedEmpty() {
        return EOL + EOL + "Summary:" + EOL + EOL + "\u001B[1;33m*\u001B[0m warnings: \u001B[1;37m0\u001B[0m" + EOL;
    }

    @Override
    String getExpectedMultiple() {
        return "\u001B[1;33m*\u001B[0m file: \u001B[1;37m" + getSourceCodeFilename() + "\u001B[0m" + EOL
            + "\u001B[0;32m    src:  \u001B[0;36m" + getSourceCodeFilename() + "\u001B[0m:\u001B[0;36m1:1\u001B[0m" + EOL
            + "\u001B[0;32m    rule: \u001B[0mFoo" + EOL
            + "\u001B[0;32m    msg:  \u001B[0mblah" + EOL
            + "\u001B[0;32m    code: \u001B[0mpublic class Foo {}" + EOL
            + "" + EOL
            + "\u001B[0;32m    src:  \u001B[0;36m" + getSourceCodeFilename() + "\u001B[0m:\u001B[0;36m1:1\u001B[0m" + EOL
            + "\u001B[0;32m    rule: \u001B[0mBoo" + EOL
            + "\u001B[0;32m    msg:  \u001B[0mblah" + EOL
            + "\u001B[0;32m    code: \u001B[0mpublic class Foo {}" + EOL
            + EOL
            + EOL
            + EOL
            + "Summary:" + EOL
            + EOL
            + "\u001B[1;33m*\u001B[0m warnings: \u001B[1;37m2\u001B[0m" + EOL;
    }

    @Override
    String getExpectedError(ProcessingError error) {
        return EOL + EOL + "Summary:" + EOL + EOL + "\u001B[1;31m*\u001B[0m file: \u001B[1;37mfile\u001B[0m" + EOL
                + "\u001B[0;32m    err:  \u001B[0;36mRuntimeException: Error\u001B[0m" + EOL
                + "\u001B[0;31m" + error.getDetail() + "\u001B[0m" + EOL + EOL
                + "\u001B[1;31m*\u001B[0m errors:   \u001B[1;37m1\u001B[0m" + EOL
                + "\u001B[1;33m*\u001B[0m warnings: \u001B[1;37m0\u001B[0m" + EOL;
    }

    @Override
    String getExpectedErrorWithoutMessage(ProcessingError error) {
        return EOL + EOL + "Summary:" + EOL + EOL + "\u001B[1;31m*\u001B[0m file: \u001B[1;37mfile\u001B[0m" + EOL
                + "\u001B[0;32m    err:  \u001B[0;36mNullPointerException: null\u001B[0m" + EOL
                + "\u001B[0;31m" + error.getDetail() + "\u001B[0m" + EOL + EOL
                + "\u001B[1;31m*\u001B[0m errors:   \u001B[1;37m1\u001B[0m" + EOL
                + "\u001B[1;33m*\u001B[0m warnings: \u001B[1;37m0\u001B[0m" + EOL;
    }

    @Override
    String getExpectedError(ConfigurationError error) {
        return EOL + EOL
            + "Summary:" + EOL
            + EOL
            + "\u001B[1;31m*\u001B[0m rule: \u001B[1;37mFoo\u001B[0m" + EOL
            + "\u001B[0;32m    err:  \u001B[0;36ma configuration error\u001B[0m" + EOL
            + EOL
            + "\u001B[1;31m*\u001B[0m errors:   \u001B[1;37m1\u001B[0m" + EOL
            + "\u001B[1;33m*\u001B[0m warnings: \u001B[1;37m0\u001B[0m" + EOL;
    }
}
