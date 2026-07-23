/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.reporting.Report;

class AvoidVarForShortTypeTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "0",
        "\"\"",
        "new DemoGeneric<? extends Demo>()",
        "new DemoGeneric<DemoGeneric<Demo1>>()"
    })
    void shouldViolate(String config) {
        final Report report = getReportForConfig(config);
        assertEquals(1, report.getViolations().size());
        assertInstanceOf(AvoidVarForShortTypeRule.class, report.getViolations().get(0).getRule());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "new DemoGeneric<DemoGeneric<Demo12>>()",
        "new DemoGeneric<? extends DemoGeneric<?>>()"
    })
    void shouldNotViolate(String config) {
        final Report report = getReportForConfig(config);
        assertTrue(report.getViolations().isEmpty());
    }

    private static Report getReportForConfig(String code) {
        final AvoidVarForShortTypeRule rule = new AvoidVarForShortTypeRule();
        rule.setMessage(""); // Ignore just don't throw an NPE

        return JavaParsingHelper.DEFAULT.executeRule(
            rule,
            "public class Bar {\n"
                + " void test() {\n"
                + "  var x = " + code + ";\n "
                + " }\n"
                + " static class DemoGeneric<T> {\n"
                + " }\n"
                + " static class Demo {\n"
                + " }\n"
                + " static class Demo1 {\n"
                + " }\n"
                + " static class Demo12 {\n"
                + " }\n"
                + "}"
        );
    }
}
