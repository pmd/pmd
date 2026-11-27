/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.test.PmdRuleTst;

class AvoidUsingHardCodedIPTest extends PmdRuleTst {

    /**
     * Regression test for GHSA-p8c4-pmh9-36vf: a very long string that looks like an IPv6 address
     * must not cause a StackOverflowError in the regex matching.
     */
    @Test
    void veryLongIPv6LikeStringShouldNotStackOverflow() {
        // passes the quick pre-check (first char is hex, at least two colons),
        // the trailing "!" makes the regex fail after consuming all segments
        String code = "public class Foo {\n"
                + "    String s = \"" + StringUtils.repeat("a:", 50_000) + "!\";\n"
                + "    String ip = \"2001:db8::1\";\n"
                + "}\n";

        Report report = JavaParsingHelper.DEFAULT.executeRule(findRule("category/java/bestpractices.xml", "AvoidUsingHardCodedIP"), code);

        // only the real address is reported
        assertEquals(1, report.getViolations().size(), "Number of violations");
        assertEquals(3, report.getViolations().get(0).getBeginLine());
    }
}
