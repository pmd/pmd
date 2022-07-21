/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RulesetsFactoryUtils;
import net.sourceforge.pmd.util.IOUtil;

public class RuleSetResolverTest {

    private static List<String> excludedRulesets = new ArrayList<>();

    static {
        excludedRulesets.add(IOUtil.normalizePath("pmd-test/src/main/resources/rulesets/dummy/basic.xml"));
    }

    @Test
    public void resolveAllRulesets() {
        Path basePath = FileSystems.getDefault().getPath(".").resolve("..").toAbsolutePath().normalize();
        List<String> additionalRulesets = GenerateRuleDocsCmd.findAdditionalRulesets(basePath);

        filterRuleSets(additionalRulesets);

        assertFalse(additionalRulesets.isEmpty());

        RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.defaultFactory();
        for (String filename : additionalRulesets) {
            try {
                ruleSetFactory.createRuleSet(filename);
            } catch (RuntimeException | RuleSetNotFoundException e) {
                fail("Couldn't load ruleset " + filename + ": " + e.getMessage());
            }
        }
    }

    @Test
    public void testAdditionalRulesetPattern() {
        String filePath = IOUtil.normalizePath("/home/foo/pmd/pmd-java/src/main/resources/rulesets/java/quickstart.xml");
        assertTrue(GenerateRuleDocsCmd.ADDITIONAL_RULESET_PATTERN.matcher(filePath).matches());
    }

    private void filterRuleSets(List<String> additionalRulesets) {
        Iterator<String> it = additionalRulesets.iterator();
        while (it.hasNext()) {
            String filename = it.next();
            for (String exclusion : excludedRulesets) {
                if (filename.endsWith(exclusion)) {
                    it.remove();
                    break;
                }
            }
        }
    }
}
