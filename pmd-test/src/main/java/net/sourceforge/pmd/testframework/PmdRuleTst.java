/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.testframework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.pmd.Rule;

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

    @TestFactory
    Collection<DynamicTest> ruleTests() {
        final List<Rule> rules = new ArrayList<>(getRules());
        rules.sort(Comparator.comparing(Rule::getName));

        final List<TestDescriptor> tests = new LinkedList<>();
        for (final Rule r : rules) {
            final TestDescriptor[] ruleTests = extractTestsFromXml(r);
            Collections.addAll(tests, ruleTests);
        }

        return tests.stream().map(this::toDynamicTest).collect(Collectors.toList());
    }

    private DynamicTest toDynamicTest(TestDescriptor testDescriptor) {
        if (isIgnored(testDescriptor)) {
            return DynamicTest.dynamicTest("[IGNORED] " + testDescriptor.getTestMethodName(),
                    testDescriptor.getTestSourceUri(),
                    () -> {});
        }
        return DynamicTest.dynamicTest(testDescriptor.getTestMethodName(),
                testDescriptor.getTestSourceUri(),
                () -> runTest(testDescriptor));
    }

    private static boolean isIgnored(TestDescriptor testDescriptor) {
        return TestDescriptor.inRegressionTestMode() && !testDescriptor.isRegressionTest();
    }
}
