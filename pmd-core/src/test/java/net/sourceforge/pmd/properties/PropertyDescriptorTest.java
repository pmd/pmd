/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.properties.constraints.NumericConstraints;
import net.sourceforge.pmd.properties.constraints.PropertyConstraint;


/**
 * Mostly TODO, I'd rather implement tests on the final version of the framework.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public class PropertyDescriptorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void testConstraintViolationCausesDysfunctionalRule() {
        PropertyDescriptor<Integer> intProperty = PropertyFactory.intProperty("fooProp")
                                                                 .desc("hello")
                                                                 .defaultValue(4)
                                                                 .require(NumericConstraints.inRange(1, 10))
                                                                 .build();

        FooRule rule = new FooRule();
        rule.definePropertyDescriptor(intProperty);
        rule.setProperty(intProperty, 1000);
        RuleSet ruleSet = new RuleSetFactory().createSingleRuleRuleSet(rule);

        List<net.sourceforge.pmd.Rule> dysfunctional = new ArrayList<>();
        ruleSet.removeDysfunctionalRules(dysfunctional);

        assertEquals(1, dysfunctional.size());
        assertThat(dysfunctional, hasItem(rule));
    }


    @Test
    public void testDefaultValueConstraintViolationCausesFailure() {
        PropertyConstraint<Integer> constraint = NumericConstraints.inRange(1, 10);

        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(allOf(containsString("Constraint violat"/*-ed or -ion*/),
                                   containsString(constraint.getConstraintDescription())));

        PropertyDescriptor<Integer> intProperty = PropertyFactory.intProperty("fooProp")
                                                                 .desc("hello")
                                                                 .defaultValue(1000)
                                                                 .require(constraint)
                                                                 .build();
    }


}
