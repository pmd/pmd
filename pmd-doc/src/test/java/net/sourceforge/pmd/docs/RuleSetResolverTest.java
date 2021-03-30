/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import net.sourceforge.pmd.RuleSetLoader;

public class RuleSetResolverTest {

    private static final List<String> EXCLUDED_RULESETS = listOf(
        FilenameUtils.normalize("pmd-test/src/main/resources/rulesets/dummy/basic.xml")
    );

    @Test
    public void resolveAllRulesets() {
        Path basePath = FileSystems.getDefault().getPath(".").resolve("..").toAbsolutePath().normalize();
        List<String> additionalRulesets = GenerateRuleDocsCmd.findAdditionalRulesets(basePath);

        filterRuleSets(additionalRulesets);

        for (String filename : additionalRulesets) {
            new RuleSetLoader().loadFromResource(filename); // will throw if invalid
        }
    }

    private void filterRuleSets(List<String> additionalRulesets) {
        Iterator<String> it = additionalRulesets.iterator();
        while (it.hasNext()) {
            String filename = it.next();
            for (String exclusion : EXCLUDED_RULESETS) {
                if (filename.endsWith(exclusion)) {
                    it.remove();
                    break;
                }
            }
        }
    }
}
