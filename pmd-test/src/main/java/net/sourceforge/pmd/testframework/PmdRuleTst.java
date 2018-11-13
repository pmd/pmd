/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.testframework;

import java.util.Collections;
import java.util.List;

import org.junit.runner.RunWith;

import net.sourceforge.pmd.Rule;

@RunWith(PMDTestRunner.class)
public class PmdRuleTst extends RuleTst {

    @Override
    protected void setUp() {
        // empty, nothing to do
    }

    @Override
    protected List<Rule> getRules() {
        String[] packages = getClass().getPackage().getName().split("\\.");
        String categoryName = packages[packages.length - 1];
        String language = packages[packages.length - 3];
        String rulesetXml = "category/" + language + "/" + categoryName + ".xml";

        Rule rule = findRule(rulesetXml, getClass().getSimpleName().replaceFirst("Test$", ""));
        return Collections.singletonList(rule);
    }
}
