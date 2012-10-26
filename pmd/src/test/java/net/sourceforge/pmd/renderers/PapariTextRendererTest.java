package net.sourceforge.pmd.renderers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.StringReader;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report.ProcessingError;

public class PapariTextRendererTest extends AbstractRendererTst {

    private static String naString = "n/a";
    static {
        naString = naString.substring(naString.lastIndexOf(File.separator) + 1);
    }
    
    public Renderer getRenderer() {
        TextColorRenderer result = new TextColorRenderer(){
            protected Reader getReader(String sourceFile) throws FileNotFoundException {
                return new StringReader("public class Foo {}");
            }
        };
        result.setProperty(TextColorRenderer.COLOR, "false");
        return result;
    }

    public String getExpected() {
        return "* file: n/a" + PMD.EOL + "    src:  " + naString + ":1:1" + PMD.EOL + "    rule: Foo" + PMD.EOL + "    msg:  msg" + PMD.EOL + "    code: public class Foo {}" + PMD.EOL + PMD.EOL + PMD.EOL + PMD.EOL + "Summary:" + PMD.EOL + PMD.EOL + " : 1" + PMD.EOL + "* warnings: 1" + PMD.EOL;
    }
    
    public String getExpectedEmpty() {
        return PMD.EOL + PMD.EOL + "Summary:" + PMD.EOL + PMD.EOL + "* warnings: 0" + PMD.EOL;
    }
    
    public String getExpectedMultiple() {
        return "* file: n/a" + PMD.EOL + "    src:  " + naString + ":1:1" + PMD.EOL + "    rule: Foo" + PMD.EOL + "    msg:  msg" + PMD.EOL + "    code: public class Foo {}" + PMD.EOL + PMD.EOL + "    src:  " + naString + ":1:1" + PMD.EOL + "    rule: Foo" + PMD.EOL + "    msg:  msg" + PMD.EOL + "    code: public class Foo {}" + PMD.EOL + PMD.EOL + PMD.EOL + PMD.EOL + "Summary:" + PMD.EOL + PMD.EOL + " : 2" + PMD.EOL + "* warnings: 2" + PMD.EOL;
    }
    
    public String getExpectedError(ProcessingError error) {
        return PMD.EOL + PMD.EOL + "Summary:" + PMD.EOL + PMD.EOL + "    err:  Error" + PMD.EOL + PMD.EOL + "* errors:   0" + PMD.EOL + "* warnings: 0" + PMD.EOL;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(PapariTextRendererTest.class);
    }
}
