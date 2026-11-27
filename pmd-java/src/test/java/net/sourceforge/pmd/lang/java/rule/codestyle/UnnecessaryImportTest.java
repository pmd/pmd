/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.test.PmdRuleTst;

class UnnecessaryImportTest extends PmdRuleTst {

    /**
     * Regression test for GHSA-p8c4-pmh9-36vf: a very long dotted name in a Javadoc reference
     * must not cause a StackOverflowError in the regex matching. The repetition count is far
     * above the overflow threshold of the old (recursive) patterns, so that the test fails reliably
     * without the fix, independent of the stack size.
     */
    @ParameterizedTest
    @CsvSource(delimiter = '|', quoteCharacter = '`', value = {
        "`@see `|",
        "`{@link `|}",
        "`// @link substring=\"a\" target=\"`|\"",
        "[|]"
    })
    void veryLongJavadocReferenceShouldNotStackOverflow(String prefix, String suffix) {
        String code = "import java.util.List;\n"
                + "import x.ParamType1;\n"
                + "public class Foo {\n"
                + "    /**\n"
                + "     * " + prefix + StringUtils.repeat("a.", 50_000)
                + "Type#method(ParamType1, int)" + StringUtils.defaultString(suffix) + "\n"
                + "     */\n"
                + "    public void a() {}\n"
                + "    /** @see List */\n"
                + "    public void b() {}\n"
                + "}\n";

        Report report = JavaParsingHelper.DEFAULT.executeRule(findRule("category/java/codestyle.xml", "UnnecessaryImport"), code);

        // both imports are used: ParamType1 within the long reference, List in a later reference
        assertEquals(0, report.getViolations().size(), "Number of violations");
    }
}
