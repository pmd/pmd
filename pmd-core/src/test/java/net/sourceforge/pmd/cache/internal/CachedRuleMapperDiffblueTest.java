package net.sourceforge.pmd.cache.internal;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.lang.rule.Rule;

import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.lang.rule.internal.RuleSets;
import org.junit.jupiter.api.Test;

class CachedRuleMapperDiffblueTest {
    /**
     * Method under test:
     * {@link CachedRuleMapper#getRuleForClass(String, String, String)}
     */
    @Test
    void testGetRuleForClass() {
        // Arrange, Act and Assert
        assertNull((new CachedRuleMapper()).getRuleForClass("Class Name", "Rule Name", "en"));
    }

    /**
     * Method under test: {@link CachedRuleMapper#initialize(RuleSets)}
     */
    @Test
    void testInitialize() {
        // Arrange
        CachedRuleMapper cachedRuleMapper = new CachedRuleMapper();
        RuleSet ruleSet = mock(RuleSet.class);
        when(ruleSet.getRules()).thenReturn(new ArrayList<>());

        ArrayList<RuleSet> ruleSets = new ArrayList<>();
        ruleSets.add(ruleSet);

        // Act
        cachedRuleMapper.initialize(new RuleSets(ruleSets));

        // Assert
        verify(ruleSet).getRules();
    }

    /**
     * Method under test: {@link CachedRuleMapper#initialize(RuleSets)}
     */
    @Test
    void testInitialize2() {
        // Arrange
        CachedRuleMapper cachedRuleMapper = new CachedRuleMapper();

        ArrayList<Rule> ruleList = new ArrayList<>();
        ruleList.add(new FooRule());
        RuleSet ruleSet = mock(RuleSet.class);
        when(ruleSet.getRules()).thenReturn(ruleList);

        ArrayList<RuleSet> ruleSets = new ArrayList<>();
        ruleSets.add(ruleSet);

        // Act
        cachedRuleMapper.initialize(new RuleSets(ruleSets));

        // Assert
        verify(ruleSet).getRules();
    }
}
