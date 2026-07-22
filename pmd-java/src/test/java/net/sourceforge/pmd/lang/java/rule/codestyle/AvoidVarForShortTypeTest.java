/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.test.PmdRuleTst;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AvoidVarForShortTypeTest extends PmdRuleTst {
    // no additional unit tests

    @Test
    void test() {
        final Report report = JavaParsingHelper.DEFAULT.executeRule(
                new AvoidVarForShortTypeRule(),
                "public class Bar {\n"
                        + " void test() {\n"
                        + "  var x = 0;\n"
                        + " }\n"
                        + "}"
        );
        Assertions.assertFalse(report.getViolations().isEmpty());
    }
}
