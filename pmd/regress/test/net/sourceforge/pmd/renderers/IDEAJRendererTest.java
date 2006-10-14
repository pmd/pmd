package test.net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.renderers.AbstractRenderer;
import net.sourceforge.pmd.renderers.IDEAJRenderer;

public class IDEAJRendererTest extends AbstractRendererTst {

    public AbstractRenderer getRenderer() {
        return new IDEAJRenderer(new String[]{"","","","","Foo <init>","Foo.java"});
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
}

