/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;

class IDEAJRendererTest extends AbstractRendererTest {

    @Override
    Renderer getRenderer() {
        Renderer result = new IDEAJRenderer();
        result.setProperty(IDEAJRenderer.SOURCE_PATH, "");
        result.setProperty(IDEAJRenderer.CLASS_AND_METHOD_NAME, "Foo <init>");
        result.setProperty(IDEAJRenderer.FILE_NAME, "Foo.java");
        return result;
    }

    @Override
    String getExpected() {
        return "blah" + PMD.EOL + " at Foo <init>(Foo.java:1)" + PMD.EOL;
    }

    @Override
    String getExpectedEmpty() {
        return "";
    }

    @Override
    String getExpectedMultiple() {
        return "blah" + PMD.EOL + " at Foo <init>(Foo.java:1)" + PMD.EOL + "blah" + PMD.EOL
                + " at Foo <init>(Foo.java:1)" + PMD.EOL;
    }
}
