package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ParserTst;

import org.junit.Test;


public class ASTAnnotationTest extends ParserTst {

    @Test
    public void testAnnotationSucceedsWithDefaultMode() throws Throwable {
        getNodes(ASTAnnotation.class, TEST1);
    }

    @Test(expected = ParseException.class)
    public void testAnnotationFailsWithJDK14() throws Throwable {
        getNodes(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.4"), ASTAnnotation.class, TEST1);
    }

    @Test
    public void testAnnotationSucceedsWithJDK15() throws Throwable {
        getNodes(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"), ASTAnnotation.class, TEST1);
    }

    private static final String TEST1 =
            "public class Foo extends Buz {" + PMD.EOL +
            " @Override" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  // overrides a superclass method" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ASTAnnotationTest.class);
    }
}
