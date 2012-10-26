package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;

public class IDEAJRendererTest extends AbstractRendererTst {

    public Renderer getRenderer() {
	Renderer result = new IDEAJRenderer();
	result.setProperty(IDEAJRenderer.SOURCE_PATH, "");
	result.setProperty(IDEAJRenderer.CLASS_AND_METHOD_NAME, "Foo <init>");
	result.setProperty(IDEAJRenderer.FILE_NAME, "Foo.java");
	return result;
    }

    public String getExpected() {
        return "msg" + PMD.EOL + " at Foo <init>(Foo.java:1)" + PMD.EOL;
    }
    
    public String getExpectedEmpty() {
        return "";
    }
    
    public String getExpectedMultiple() {
        return "msg" + PMD.EOL + " at Foo <init>(Foo.java:1)" + PMD.EOL + "msg" + PMD.EOL + " at Foo <init>(Foo.java:1)" + PMD.EOL;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(IDEAJRendererTest.class);
    }
}

