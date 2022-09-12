/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.RuleSetLoader;
import net.sourceforge.pmd.util.IOUtil;

public class RuleSetResolverTest {

    private static final List<String> EXCLUDED_RULESETS = listOf(
            IOUtil.normalizePath("pmd-test/src/main/resources/rulesets/dummy/basic.xml")
    );

    @Test
    public void resolveAllRulesets() {
        Path basePath = FileSystems.getDefault().getPath(".").resolve("..").toAbsolutePath().normalize();
        List<String> additionalRulesets = GenerateRuleDocsCmd.findAdditionalRulesets(basePath);

        filterRuleSets(additionalRulesets);

        assertFalse(additionalRulesets.isEmpty());

        for (String filename : additionalRulesets) {
            new RuleSetLoader().warnDeprecated(false).loadFromResource(filename); // will throw if invalid
        }
    }

    @Test
    public void testAdditionalRulesetPattern() {
        String filePath = IOUtil.normalizePath("/home/foo/pmd/pmd-java/src/main/resources/rulesets/java/quickstart.xml");
        assertTrue(GenerateRuleDocsCmd.ADDITIONAL_RULESET_PATTERN.matcher(filePath).matches());
    }

    private void filterRuleSets(List<String> additionalRulesets) {
        additionalRulesets.removeIf(this::isExcluded);
    }

    private boolean isExcluded(String fileName) {
        return EXCLUDED_RULESETS.stream().anyMatch(fileName::endsWith);
    }
}
