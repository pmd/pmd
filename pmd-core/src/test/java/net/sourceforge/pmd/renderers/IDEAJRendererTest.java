
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;

public class IDEAJRendererTest extends AbstractRendererTst {

    @Override
    public Renderer getRenderer() {
        Renderer result = new IDEAJRenderer();
        result.setProperty(IDEAJRenderer.SOURCE_PATH, "");
        result.setProperty(IDEAJRenderer.CLASS_AND_METHOD_NAME, "Foo <init>");
        result.setProperty(IDEAJRenderer.FILE_NAME, "Foo.java");
        return result;
    }

    @Override
    public String getExpected() {
        return "blah" + PMD.EOL + " at Foo <init>(Foo.java:1)" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return "";
    }

    @Override
    public String getExpectedMultiple() {
        return "blah" + PMD.EOL + " at Foo <init>(Foo.java:1)" + PMD.EOL + "blah" + PMD.EOL
                + " at Foo <init>(Foo.java:1)" + PMD.EOL;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(IDEAJRendererTest.class);
    }
}
