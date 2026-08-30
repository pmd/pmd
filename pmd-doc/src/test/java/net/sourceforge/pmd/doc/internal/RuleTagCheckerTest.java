/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.doc.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.nio.file.FileSystems;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class RuleTagCheckerTest {

    @Test
    void testAllChecks() throws Exception {
        RuleTagChecker checker = new RuleTagChecker(FileSystems.getDefault().getPath("src/test/resources/ruletagchecker"));
        List<String> issues = checker.check();

        issues = issues.stream()
                        .map(s -> s.replace('\\', '/')) // convert windows paths
                        .sorted()
                        .collect(Collectors.toList());

        assertThat(issues, contains(
                "pmd/rules/java/codestyle.md: 8: Rule NotExistingRule is not found",
                "pmd/rules/java/codestyle.md: 9: Rule \"NotExistingRule\" is not found",
                "pmd/rules/java/codestyle.md:17: Rule java/notexistingcategory/AvoidPrintStackTrace is not found",
                "pmd/rules/java/design.md: 9: Rule tag for \"java/bestpractices/AvoidPrintStackTrace\" is not closed properly",
                "pmd/rules/java/design.md:12: Rule \"java/notexistingcategory/AvoidPrintStackTrace\" is not found",
                "pmd/rules/java/design.md:14: Rule \"java/bestpractices/NotExistingRule\" is not found",
                "pmd/rules/java/design.md:16: Rule tag for \"java/bestpractices/OtherRule has a missing quote",
                "pmd/rules/java/design.md:17: Rule tag for java/bestpractices/OtherRule\" has a missing quote",
                "pmd/rules/java/design.md:19: Rule OtherRule is not found",
                "pmd/rules/java/design.md:20: Rule \"OtherRule\" is not found",
                "pmd/rules/java/design.md:21: Rule tag for \"OtherRule has a missing quote",
                "pmd/rules/java/design.md:22: Rule tag for OtherRule\" has a missing quote"
        ));
    }
}
