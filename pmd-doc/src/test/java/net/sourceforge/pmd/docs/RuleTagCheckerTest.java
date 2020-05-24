/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.docs;

import java.nio.file.FileSystems;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class RuleTagCheckerTest {

    @Test
    public void testAllChecks() throws Exception {
        RuleTagChecker checker = new RuleTagChecker(FileSystems.getDefault().getPath("src/test/resources/ruletagchecker"));
        List<String> issues = checker.check();

        Assert.assertEquals(7, issues.size());
        Assert.assertEquals("ruletag-examples.md: 9: Rule tag for \"java/bestpractices/AvoidPrintStackTrace\" is not closed properly",
                issues.get(0));
        Assert.assertEquals("ruletag-examples.md:12: Rule \"java/notexistingcategory/AvoidPrintStackTrace\" is not found",
                issues.get(1));
        Assert.assertEquals("ruletag-examples.md:14: Rule \"java/bestpractices/NotExistingRule\" is not found",
                issues.get(2));
        Assert.assertEquals("ruletag-examples.md:16: Rule tag for \"java/bestpractices/OtherRule has a missing quote",
                issues.get(3));
        Assert.assertEquals("ruletag-examples.md:17: Rule tag for java/bestpractices/OtherRule\" has a missing quote",
                issues.get(4));
        Assert.assertEquals("ruletag-examples.md:21: Rule tag for \"OtherRule has a missing quote", issues.get(5));
        Assert.assertEquals("ruletag-examples.md:22: Rule tag for OtherRule\" has a missing quote", issues.get(6));
    }
}
