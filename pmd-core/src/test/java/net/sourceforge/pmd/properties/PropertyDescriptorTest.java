/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.inRange;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.SubstringMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
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
                                                                 .require(inRange(1, 10))
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
    public void testConstraintViolationCausesDysfunctionalRuleMulti() {
        PropertyDescriptor<List<Double>> descriptor = PropertyFactory.doubleListProperty("fooProp")
                                                                     .desc("hello")
                                                                     .defaultValues(2., 11.) // 11. is in range
                                                                     .requireEach(inRange(1d, 20d))
                                                                     .build();

        FooRule rule = new FooRule();
        rule.definePropertyDescriptor(descriptor);
        rule.setProperty(descriptor, Collections.singletonList(1000d)); // not in range
        RuleSet ruleSet = new RuleSetFactory().createSingleRuleRuleSet(rule);

        List<net.sourceforge.pmd.Rule> dysfunctional = new ArrayList<>();
        ruleSet.removeDysfunctionalRules(dysfunctional);

        assertEquals(1, dysfunctional.size());
        assertThat(dysfunctional, hasItem(rule));
    }

    @Test
    public void testDefaultValueConstraintViolationCausesFailure() {
        PropertyConstraint<Integer> constraint = inRange(1, 10);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(allOf(containsIgnoreCase("Constraint violat"/*-ed or -ion*/),
                                   containsIgnoreCase(constraint.getConstraintDescription())));

        PropertyFactory.intProperty("fooProp")
                       .desc("hello")
                       .defaultValue(1000)
                       .require(constraint)
                       .build();
    }


    @Test
    public void testDefaultValueConstraintViolationCausesFailureMulti() {
        PropertyConstraint<Double> constraint = inRange(1d, 10d);

        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(allOf(containsIgnoreCase("Constraint violat"/*-ed or -ion*/),
                                   containsIgnoreCase(constraint.getConstraintDescription())));

        PropertyFactory.doubleListProperty("fooProp")
                       .desc("hello")
                       .defaultValues(2., 11.) // 11. is out of range
                       .requireEach(constraint)
                       .build();
    }


    @Test
    public void testNoConstraintViolationCausesIsOkMulti() {

        PropertyDescriptor<List<Double>> descriptor = PropertyFactory.doubleListProperty("fooProp")
                                                                     .desc("hello")
                                                                     .defaultValues(2., 11.) // 11. is in range
                                                                     .requireEach(inRange(1d, 20d))
                                                                     .build();

        assertEquals("fooProp", descriptor.name());
        assertEquals("hello", descriptor.description());
        assertThat(descriptor.defaultValue(), Matchers.contains(2., 11.));
    }



    @Test
    public void testNoConstraintViolationCausesIsOk() {

        PropertyDescriptor<String> descriptor = PropertyFactory.stringProperty("fooProp")
                                                                     .desc("hello")
                                                                     .defaultValue("bazooli")
                                                                     .build();

        assertEquals("fooProp", descriptor.name());
        assertEquals("hello", descriptor.description());
        assertEquals("bazooli", descriptor.defaultValue());
    }

    @Test
    public void testIntProperty() {
        PropertyDescriptor<Integer> descriptor = PropertyFactory.intProperty("intProp")
                .desc("hello")
                .defaultValue(1)
                .build();
        assertEquals(Integer.valueOf(1), descriptor.defaultValue());
        assertEquals("intProp", descriptor.name());
        assertEquals("hello", descriptor.description());

        PropertyDescriptor<List<Integer>> listDescriptor = PropertyFactory.intListProperty("intListProp")
                .desc("hello")
                .defaultValues(1, 2)
                .build();
        assertEquals(Arrays.asList(1, 2), listDescriptor.defaultValue());
        assertEquals("intListProp", listDescriptor.name());
        assertEquals("hello", listDescriptor.description());
    }

    @Test
    public void testDoubleProperty() {
        PropertyDescriptor<Double> descriptor = PropertyFactory.doubleProperty("doubleProp")
                .desc("hello")
                .defaultValue(1.0)
                .build();
        assertEquals(Double.valueOf(1.0), descriptor.defaultValue());

        PropertyDescriptor<List<Double>> listDescriptor = PropertyFactory.doubleListProperty("doubleListProp")
                .desc("hello")
                .defaultValues(1.0, 2.0)
                .build();
        assertEquals(Arrays.asList(1.0, 2.0), listDescriptor.defaultValue());
    }

    @Test
    public void testStringProperty() {
        PropertyDescriptor<String> descriptor = PropertyFactory.stringProperty("stringProp")
                .desc("hello")
                .defaultValue("default value")
                .build();
        assertEquals("default value", descriptor.defaultValue());

        PropertyDescriptor<List<String>> listDescriptor = PropertyFactory.stringListProperty("stringListProp")
                .desc("hello")
                .defaultValues("v1", "v2")
                .build();
        assertEquals(Arrays.asList("v1", "v2"), listDescriptor.defaultValue());
    }

    private enum SampleEnum { A, B, C }

    @Test
    public void testEnumProperty() {
        Map<String, SampleEnum> nameMap = new HashMap<>();
        nameMap.put("TEST_A", SampleEnum.A);
        nameMap.put("TEST_B", SampleEnum.B);
        nameMap.put("TEST_C", SampleEnum.C);

        PropertyDescriptor<SampleEnum> descriptor = PropertyFactory.enumProperty("enumProp", nameMap)
                .desc("hello")
                .defaultValue(SampleEnum.B)
                .build();
        assertEquals(SampleEnum.B, descriptor.defaultValue());

        PropertyDescriptor<List<SampleEnum>> listDescriptor = PropertyFactory.enumListProperty("enumListProp", nameMap)
                .desc("hello")
                .defaultValues(SampleEnum.A, SampleEnum.B)
                .build();
        assertEquals(Arrays.asList(SampleEnum.A, SampleEnum.B), listDescriptor.defaultValue());
    }

    @Test
    public void testRegexProperty() {
        PropertyDescriptor<Pattern> descriptor = PropertyFactory.regexProperty("regexProp")
                .desc("hello")
                .defaultValue("^[A-Z].*$")
                .build();
        assertEquals("regexProp", descriptor.name());
        assertEquals("hello", descriptor.description());
        assertEquals("^[A-Z].*$", descriptor.defaultValue().toString());
    }

    private static Matcher<String> containsIgnoreCase(final String substring) {
        return new SubstringMatcher(substring) {

            @Override
            protected boolean evalSubstringOf(String string) {
                return StringUtils.indexOfIgnoreCase(string, substring) != -1;
            }


            @Override
            protected String relationship() {
                return "containing ignoring case";
            }
        };
    }

}
