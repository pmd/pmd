/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

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
        return "blah" + EOL + " at Foo <init>(Foo.java:1)" + EOL;
    }

    @Override
    String getExpectedEmpty() {
        return "";
    }

    @Override
    String getExpectedMultiple() {
        return "blah" + EOL + " at Foo <init>(Foo.java:1)" + EOL + "blah" + EOL
                + " at Foo <init>(Foo.java:1)" + EOL;
    }
}
