package net.sourceforge.pmd.renderers;

import java.util.Properties;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.renderers.IDEAJRenderer;
import net.sourceforge.pmd.renderers.Renderer;

public class IDEAJRendererTest extends AbstractRendererTst {

    public Renderer getRenderer() {
	Properties properties = new Properties();
	properties.put(IDEAJRenderer.SOURCE_PATH, "");
	properties.put(IDEAJRenderer.CLASS_AND_METHOD_NAME, "Foo <init>");
	properties.put(IDEAJRenderer.FILE_NAME, "Foo.java");
        return new IDEAJRenderer(properties);
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

