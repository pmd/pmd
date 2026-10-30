/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.lang.rule.RuleSetLoader;
import net.sourceforge.pmd.test.PmdRuleTst;

class ReplaceJavaUtilVectorTest extends PmdRuleTst {

    @ParameterizedTest
    @ValueSource(strings = {
        "category/java/bestpractices.xml/ReplaceVectorWithList",
        "category/java/performance.xml/UseArrayListInsteadOfVector"
    })
    void deprecatedNamesReferToReplacement(String ruleRef) {
        Rule rule = new RuleSetLoader().includeDeprecatedRuleReferences(true).warnDeprecated(false)
                .loadFromResource(ruleRef).getRules().iterator().next();

        assertTrue(rule.isDeprecated());
        RuleReference reference = assertInstanceOf(RuleReference.class, rule);
        assertEquals("ReplaceJavaUtilVector", reference.getOriginalName());
        assertEquals("category/java/bestpractices.xml", reference.getRuleSetReference().getRuleSetFileName());
    }
}
