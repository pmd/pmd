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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.SubstringMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RulesetsFactoryUtils;
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
        RuleSet ruleSet = RulesetsFactoryUtils.defaultFactory().createSingleRuleRuleSet(rule);

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
        RuleSet ruleSet = RulesetsFactoryUtils.defaultFactory().createSingleRuleRuleSet(rule);

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

        thrown.expect(IllegalArgumentException.class);
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
        assertEquals("intProp", descriptor.name());
        assertEquals("hello", descriptor.description());
        assertEquals(Integer.valueOf(1), descriptor.defaultValue());
        assertEquals(Integer.valueOf(5), descriptor.valueFrom("5"));

        PropertyDescriptor<List<Integer>> listDescriptor = PropertyFactory.intListProperty("intListProp")
                .desc("hello")
                .defaultValues(1, 2)
                .build();
        assertEquals("intListProp", listDescriptor.name());
        assertEquals("hello", listDescriptor.description());
        assertEquals(Arrays.asList(1, 2), listDescriptor.defaultValue());
        assertEquals(Arrays.asList(5, 7), listDescriptor.valueFrom("5,7"));
    }

    @Test
    public void testIntPropertyInvalidValue() {
        PropertyDescriptor<Integer> descriptor = PropertyFactory.intProperty("intProp")
                .desc("hello")
                .defaultValue(1)
                .build();
        thrown.expect(NumberFormatException.class);
        thrown.expectMessage("not a number");
        descriptor.valueFrom("not a number");
    }

    @Test
    public void testDoubleProperty() {
        PropertyDescriptor<Double> descriptor = PropertyFactory.doubleProperty("doubleProp")
                .desc("hello")
                .defaultValue(1.0)
                .build();
        assertEquals("doubleProp", descriptor.name());
        assertEquals("hello", descriptor.description());
        assertEquals(Double.valueOf(1.0), descriptor.defaultValue());
        assertEquals(Double.valueOf(2.0), descriptor.valueFrom("2.0"));

        PropertyDescriptor<List<Double>> listDescriptor = PropertyFactory.doubleListProperty("doubleListProp")
                .desc("hello")
                .defaultValues(1.0, 2.0)
                .build();
        assertEquals("doubleListProp", listDescriptor.name());
        assertEquals("hello", listDescriptor.description());
        assertEquals(Arrays.asList(1.0, 2.0), listDescriptor.defaultValue());
        assertEquals(Arrays.asList(2.0, 3.0), listDescriptor.valueFrom("2.0,3.0"));
    }

    @Test
    public void testDoublePropertyInvalidValue() {
        PropertyDescriptor<Double> descriptor = PropertyFactory.doubleProperty("doubleProp")
                .desc("hello")
                .defaultValue(1.0)
                .build();
        thrown.expect(NumberFormatException.class);
        thrown.expectMessage("this is not a number");
        descriptor.valueFrom("this is not a number");
    }

    @Test
    public void testStringProperty() {
        PropertyDescriptor<String> descriptor = PropertyFactory.stringProperty("stringProp")
                .desc("hello")
                .defaultValue("default value")
                .build();
        assertEquals("stringProp", descriptor.name());
        assertEquals("hello", descriptor.description());
        assertEquals("default value", descriptor.defaultValue());
        assertEquals("foo", descriptor.valueFrom("foo"));

        PropertyDescriptor<List<String>> listDescriptor = PropertyFactory.stringListProperty("stringListProp")
                .desc("hello")
                .defaultValues("v1", "v2")
                .build();
        assertEquals("stringListProp", listDescriptor.name());
        assertEquals("hello", listDescriptor.description());
        assertEquals(Arrays.asList("v1", "v2"), listDescriptor.defaultValue());
        assertEquals(Arrays.asList("foo", "bar"), listDescriptor.valueFrom("foo|bar"));
    }

    private enum SampleEnum { A, B, C }

    private static Map<String, SampleEnum> nameMap = new LinkedHashMap<>();

    static {
        nameMap.put("TEST_A", SampleEnum.A);
        nameMap.put("TEST_B", SampleEnum.B);
        nameMap.put("TEST_C", SampleEnum.C);
    }

    @Test
    public void testEnumProperty() {
        PropertyDescriptor<SampleEnum> descriptor = PropertyFactory.enumProperty("enumProp", nameMap)
                .desc("hello")
                .defaultValue(SampleEnum.B)
                .build();
        assertEquals("enumProp", descriptor.name());
        assertEquals("hello", descriptor.description());
        assertEquals(SampleEnum.B, descriptor.defaultValue());
        assertEquals(SampleEnum.C, descriptor.valueFrom("TEST_C"));

        PropertyDescriptor<List<SampleEnum>> listDescriptor = PropertyFactory.enumListProperty("enumListProp", nameMap)
                .desc("hello")
                .defaultValues(SampleEnum.A, SampleEnum.B)
                .build();
        assertEquals("enumListProp", listDescriptor.name());
        assertEquals("hello", listDescriptor.description());
        assertEquals(Arrays.asList(SampleEnum.A, SampleEnum.B), listDescriptor.defaultValue());
        assertEquals(Arrays.asList(SampleEnum.B, SampleEnum.C), listDescriptor.valueFrom("TEST_B|TEST_C"));
    }


    @Test
    public void testEnumPropertyNullValueFailsBuild() {
        Map<String, SampleEnum> map = new HashMap<>(nameMap);
        map.put("TEST_NULL", null);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsIgnoreCase("null value"));

        PropertyFactory.enumProperty("enumProp", map);
    }


    @Test
    public void testEnumListPropertyNullValueFailsBuild() {
        Map<String, SampleEnum> map = new HashMap<>(nameMap);
        map.put("TEST_NULL", null);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsIgnoreCase("null value"));

        PropertyFactory.enumListProperty("enumProp", map);
    }


    @Test
    public void testEnumPropertyInvalidValue() {
        PropertyDescriptor<SampleEnum> descriptor = PropertyFactory.enumProperty("enumProp", nameMap)
                .desc("hello")
                .defaultValue(SampleEnum.B)
                .build();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Value was not in the set [TEST_A, TEST_B, TEST_C]");
        descriptor.valueFrom("InvalidEnumValue");
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
        assertEquals("[0-9]+", descriptor.valueFrom("[0-9]+").toString());
    }

    @Test
    public void testRegexPropertyInvalidValue() {
        PropertyDescriptor<Pattern> descriptor = PropertyFactory.regexProperty("regexProp")
                .desc("hello")
                .defaultValue("^[A-Z].*$")
                .build();
        thrown.expect(PatternSyntaxException.class);
        thrown.expectMessage("Unclosed character class");
        descriptor.valueFrom("[open class");
    }

    @Test
    public void testRegexPropertyInvalidDefaultValue() {
        thrown.expect(PatternSyntaxException.class);
        thrown.expectMessage("Unclosed character class");
        PropertyDescriptor<Pattern> descriptor = PropertyFactory.regexProperty("regexProp")
                .desc("hello")
                .defaultValue("[open class")
                .build();
    }

    private static Matcher<String> containsIgnoreCase(final String substring) {
        return new SubstringMatcher(substring) {

            @Override
            protected boolean evalSubstringOf(String string) {
                return StringUtils.indexOfIgnoreCase(string, substring) != -1;
            }


            @Override
            protected String relationship() {
                return "containing (ignoring case)";
            }
        };
    }

}
