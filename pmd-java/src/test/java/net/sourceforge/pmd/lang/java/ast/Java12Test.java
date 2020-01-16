/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

@Ignore("those tests depend on type resolution")
public class Java12Test {


    private final JavaParsingHelper java11 =
        JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("11")
                                         .withResourceContext(Java12Test.class, "jdkversiontests/java12/");


    @Test(expected = ParseException.class)
    public void testMultipleCaseLabelsJava11() {
        java11.parseResource("MultipleCaseLabels.java");
    }

    @Test(expected = ParseException.class)
    public void testSwitchRulesJava11() {
        java11.parseResource("SwitchRules.java");
    }


    @Test(expected = ParseException.class)
    public void testSwitchExpressionsJava11() {
        java11.parseResource("SwitchExpressions.java");
    }

}
