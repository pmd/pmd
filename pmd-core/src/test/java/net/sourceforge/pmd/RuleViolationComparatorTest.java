/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.PmdCoreTestUtils.setDummyLanguage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextRange2d;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

class RuleViolationComparatorTest {

    @Test
    void testComparator() {
        Rule rule1 = setDummyLanguage(new MockRule("name1", "desc", "msg", "rulesetname1"));
        Rule rule2 = setDummyLanguage(new MockRule("name2", "desc", "msg", "rulesetname2"));

        // RuleViolations created in pre-sorted order
        RuleViolation[] expectedOrder = new RuleViolation[12];

        int index = 0;
        // Different begin line
        expectedOrder[index++] = createJavaRuleViolation(rule1, "file1", 10, "desc1", 1, 20, 80);
        expectedOrder[index++] = createJavaRuleViolation(rule1, "file1", 20, "desc1", 1, 20, 80);
        // Different description
        expectedOrder[index++] = createJavaRuleViolation(rule1, "file2", 10, "desc1", 1, 20, 80);
        expectedOrder[index++] = createJavaRuleViolation(rule1, "file2", 10, "desc2", 1, 20, 80);
        // Different begin column
        expectedOrder[index++] = createJavaRuleViolation(rule1, "file3", 10, "desc1", 1, 20, 80);
        expectedOrder[index++] = createJavaRuleViolation(rule1, "file3", 10, "desc1", 10, 20, 80);
        // Different end line
        expectedOrder[index++] = createJavaRuleViolation(rule1, "file4", 10, "desc1", 1, 20, 80);
        expectedOrder[index++] = createJavaRuleViolation(rule1, "file4", 10, "desc1", 1, 30, 80);
        // Different end column
        expectedOrder[index++] = createJavaRuleViolation(rule1, "file5", 10, "desc1", 1, 20, 80);
        expectedOrder[index++] = createJavaRuleViolation(rule1, "file5", 10, "desc1", 1, 20, 90);
        // Different rule name
        expectedOrder[index++] = createJavaRuleViolation(rule1, "file6", 10, "desc1", 1, 20, 80);
        expectedOrder[index++] = createJavaRuleViolation(rule2, "file6", 10, "desc1", 1, 20, 80);

        // Randomize
        List<RuleViolation> ruleViolations = new ArrayList<>(Arrays.asList(expectedOrder));
        long seed = System.nanoTime();
        Random random = new Random(seed);
        Collections.shuffle(ruleViolations, random);

        // Sort
        Collections.sort(ruleViolations, RuleViolation.DEFAULT_COMPARATOR);

        // Check
        int count = 0;
        for (int i = 0; i < expectedOrder.length; i++) {
            count++;
            assertSame(expectedOrder[i], ruleViolations.get(i), "Wrong RuleViolation " + i + ", used seed: " + seed);
        }
        assertEquals(expectedOrder.length, count, "Missing assertion for every RuleViolation");
    }

    private RuleViolation createJavaRuleViolation(Rule rule, String fileName, int beginLine, String description,
            int beginColumn, int endLine, int endColumn) {
        FileLocation loc = FileLocation.range(fileName, TextRange2d.range2d(beginLine, beginColumn, endLine, endColumn));
        return new ParametricRuleViolation(rule, loc, description, Collections.emptyMap());
    }
}
