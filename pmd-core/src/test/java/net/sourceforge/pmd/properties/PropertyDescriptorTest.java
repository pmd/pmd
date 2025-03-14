/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static java.util.Collections.emptyList;
import static net.sourceforge.pmd.properties.NumericConstraints.inRange;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.SubstringMatcher;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.properties.internal.PropertyParsingUtil;


/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class PropertyDescriptorTest {



    @Test
    void testDefaultValueConstraintViolationCausesFailure() {
        PropertyConstraint<Integer> constraint = inRange(1, 10);

        IllegalArgumentException thrown = assertThrows(ConstraintViolatedException.class, () ->
            PropertyFactory.intProperty("fooProp")
                           .desc("hello")
                           .defaultValue(1000)
                           .require(constraint)
                           .build());
        assertThat(thrown.getMessage(),
                containsIgnoreCase(constraint.getConstraintDescription()));
    }


    @Test
    void testDefaultValueConstraintViolationCausesFailureMulti() {
        PropertyConstraint<Double> constraint = inRange(1d, 10d);

        IllegalArgumentException thrown = assertThrows(ConstraintViolatedException.class, () ->
            PropertyFactory.doubleListProperty("fooProp")
                           .desc("hello")
                           .defaultValues(2., 11.) // 11. is out of range
                           .requireEach(constraint)
                           .build());
        assertThat(thrown.getMessage(),
                containsIgnoreCase(constraint.getConstraintDescription()));
    }


    @Test
    void testNoConstraintViolationCausesIsOkMulti() {

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
    void testNoConstraintViolationCausesIsOk() {

        PropertyDescriptor<String> descriptor = PropertyFactory.stringProperty("fooProp")
                                                                     .desc("hello")
                                                                     .defaultValue("bazooli")
                                                                     .build();

        assertEquals("fooProp", descriptor.name());
        assertEquals("hello", descriptor.description());
        assertEquals("bazooli", descriptor.defaultValue());
    }

    @Test
    void testIntProperty() {
        PropertyDescriptor<Integer> descriptor = PropertyFactory.intProperty("intProp")
                .desc("hello")
                .defaultValue(1)
                .build();
        assertEquals("intProp", descriptor.name());
        assertEquals("hello", descriptor.description());
        assertEquals(Integer.valueOf(1), descriptor.defaultValue());
        assertEquals(Integer.valueOf(5), descriptor.serializer().fromString("5"));
        assertEquals(Integer.valueOf(5), descriptor.serializer().fromString(" 5 "));

        PropertyDescriptor<List<Integer>> listDescriptor = PropertyFactory.intListProperty("intListProp")
                .desc("hello")
                .defaultValues(1, 2)
                .build();
        assertEquals("intListProp", listDescriptor.name());
        assertEquals("hello", listDescriptor.description());
        assertEquals(Arrays.asList(1, 2), listDescriptor.defaultValue());
        assertEquals(Arrays.asList(5, 7), listDescriptor.serializer().fromString("5,7"));
        assertEquals(Arrays.asList(5, 7), listDescriptor.serializer().fromString(" 5 , 7 "));
    }

    @Test
    void testIntPropertyInvalidValue() {
        PropertyDescriptor<Integer> descriptor = PropertyFactory.intProperty("intProp")
                .desc("hello")
                .defaultValue(1)
                .build();

        NumberFormatException thrown = assertThrows(NumberFormatException.class, () ->
            descriptor.serializer().fromString("not a number"));
        assertThat(thrown.getMessage(), containsString("not a number"));
    }

    @Test
    void testDoubleProperty() {
        PropertyDescriptor<Double> descriptor = PropertyFactory.doubleProperty("doubleProp")
                .desc("hello")
                .defaultValue(1.0)
                .build();
        assertEquals("doubleProp", descriptor.name());
        assertEquals("hello", descriptor.description());
        assertEquals(Double.valueOf(1.0), descriptor.defaultValue());
        assertEquals(Double.valueOf(2.0), descriptor.serializer().fromString("2.0"));
        assertEquals(Double.valueOf(2.0), descriptor.serializer().fromString("  2.0  "));

        PropertyDescriptor<List<Double>> listDescriptor = PropertyFactory.doubleListProperty("doubleListProp")
                .desc("hello")
                .defaultValues(1.0, 2.0)
                .build();
        assertEquals("doubleListProp", listDescriptor.name());
        assertEquals("hello", listDescriptor.description());
        assertEquals(Arrays.asList(1.0, 2.0), listDescriptor.defaultValue());
        assertEquals(Arrays.asList(2.0, 3.0), listDescriptor.serializer().fromString("2.0,3.0"));
        assertEquals(Arrays.asList(2.0, 3.0), listDescriptor.serializer().fromString(" 2.0 , 3.0 "));
    }

    @Test
    void testDoublePropertyInvalidValue() {
        PropertyDescriptor<Double> descriptor = PropertyFactory.doubleProperty("doubleProp")
                .desc("hello")
                .defaultValue(1.0)
                .build();
        NumberFormatException thrown = assertThrows(NumberFormatException.class, () ->
            descriptor.serializer().fromString("this is not a number"));
        assertThat(thrown.getMessage(), containsString("this is not a number"));
    }

    @Test
    void testStringProperty() {
        PropertyDescriptor<String> descriptor = PropertyFactory.stringProperty("stringProp")
                .desc("hello")
                .defaultValue("default value")
                .build();
        assertEquals("stringProp", descriptor.name());
        assertEquals("hello", descriptor.description());
        assertEquals("default value", descriptor.defaultValue());
        assertEquals("foo", descriptor.serializer().fromString("foo"));
        assertEquals("foo", descriptor.serializer().fromString("  foo   "));

    }

    @Test
    void testStringListProperty() {
        PropertyDescriptor<List<String>> listDescriptor = PropertyFactory.stringListProperty("stringListProp")
                .desc("hello")
                .defaultValues("v1", "v2")
                .build();
        assertEquals("stringListProp", listDescriptor.name());
        assertEquals("hello", listDescriptor.description());
        assertEquals(Arrays.asList("v1", "v2"), listDescriptor.defaultValue());
        assertEquals(Arrays.asList("foo", "bar"), listDescriptor.serializer().fromString("foo,bar"));
        assertEquals(Arrays.asList("foo", "bar"), listDescriptor.serializer().fromString("  foo ,  bar  "));
    }

    private enum SampleEnum { A, B, C }

    private static final Map<String, SampleEnum> NAME_MAP = new LinkedHashMap<>();

    static {
        NAME_MAP.put("TEST_A", SampleEnum.A);
        NAME_MAP.put("TEST_B", SampleEnum.B);
        NAME_MAP.put("TEST_C", SampleEnum.C);
    }

    @Test
    void testEnumProperty() {
        PropertyDescriptor<SampleEnum> descriptor = PropertyFactory.enumProperty("enumProp", NAME_MAP)
                .desc("hello")
                .defaultValue(SampleEnum.B)
                .build();
        assertEquals("enumProp", descriptor.name());
        assertEquals("hello", descriptor.description());
        assertEquals(SampleEnum.B, descriptor.defaultValue());
        assertEquals(SampleEnum.C, descriptor.serializer().fromString("TEST_C"));

        PropertyDescriptor<List<SampleEnum>> listDescriptor = PropertyFactory.enumListProperty("enumListProp", NAME_MAP)
                .desc("hello")
                .defaultValues(SampleEnum.A, SampleEnum.B)
                .build();
        assertEquals("enumListProp", listDescriptor.name());
        assertEquals("hello", listDescriptor.description());
        assertEquals(Arrays.asList(SampleEnum.A, SampleEnum.B), listDescriptor.defaultValue());
        assertEquals(Arrays.asList(SampleEnum.B, SampleEnum.C), listDescriptor.serializer().fromString("TEST_B,TEST_C"));
    }


    @Test
    void testEnumPropertyNullValueFailsBuild() {
        Map<String, SampleEnum> map = new HashMap<>(NAME_MAP);
        map.put("TEST_NULL", null);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
            PropertyFactory.enumProperty("enumProp", map));
        assertThat(thrown.getMessage(), containsIgnoreCase("null value"));
    }


    @Test
    void testEnumListPropertyNullValueFailsBuild() {
        Map<String, SampleEnum> map = new HashMap<>(NAME_MAP);
        map.put("TEST_NULL", null);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
            PropertyFactory.enumListProperty("enumProp", map));
        assertThat(thrown.getMessage(), containsIgnoreCase("null value"));
    }


    @Test
    void testEnumPropertyInvalidValue() {
        PropertyDescriptor<SampleEnum> descriptor = PropertyFactory.enumProperty("enumProp", NAME_MAP)
                .desc("hello")
                .defaultValue(SampleEnum.B)
                .build();

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
            descriptor.serializer().fromString("InvalidEnumValue"));
        assertThat(thrown.getMessage(), containsString("'InvalidEnumValue' should be one of 'TEST_A', 'TEST_B', 'TEST_C'"));
    }

    @Test
    void testRegexProperty() {
        PropertyDescriptor<Pattern> descriptor = PropertyFactory.regexProperty("regexProp")
                .desc("hello")
                .defaultValue("^[A-Z].*$")
                .build();
        assertEquals("regexProp", descriptor.name());
        assertEquals("hello", descriptor.description());
        assertEquals("^[A-Z].*$", descriptor.defaultValue().toString());
        assertEquals("[0-9]+", descriptor.serializer().fromString("[0-9]+").toString());
    }

    @Test
    void testRegexPropertyInvalidValue() {
        PropertyDescriptor<Pattern> descriptor = PropertyFactory.regexProperty("regexProp")
                .desc("hello")
                .defaultValue("^[A-Z].*$")
                .build();

        PatternSyntaxException thrown = assertThrows(PatternSyntaxException.class, () ->
            descriptor.serializer().fromString("[open class"));
        assertThat(thrown.getMessage(), containsString("Unclosed character class"));
    }

    @Test
    void testRegexPropertyInvalidDefaultValue() {
        PatternSyntaxException thrown = assertThrows(PatternSyntaxException.class, () ->
            PropertyFactory.regexProperty("regexProp")
                    .desc("hello")
                    .defaultValue("[open class")
                    .build());
        assertThat(thrown.getMessage(), containsString("Unclosed character class"));
    }


    private static List<String> parseEscaped(String s, char d) {
        return PropertyParsingUtil.parseListWithEscapes(s, d, Function.identity());
    }

    @Test
    void testStringParserEmptyString() {
        assertEquals(emptyList(), parseEscaped("", ','));
    }


    @Test
    void testStringParserSimple() {
        assertEquals(listOf("a", "b", "c"),
                     parseEscaped("a,b,c", ','));
    }

    @Test
    void testStringParserEscapedChar() {
        assertEquals(listOf("a", "b,c"),
                     parseEscaped("a,b\\,c", ','));
    }

    @Test
    void testStringParserEscapedEscapedChar() {
        assertEquals(listOf("a", "b\\", "c"),
                     parseEscaped("a,b\\\\,c", ','));
    }

    @Test
    void testStringParserDelimIsBackslash() {
        assertEquals(listOf("a,b", "", ",c"),
                     parseEscaped("a,b\\\\,c", '\\'));
    }

    @Test
    void testStringParserTrailingBackslash() {
        assertEquals(listOf("a", "b\\"),
                     parseEscaped("a,b\\", ','));
    }

    private static Matcher<String> containsIgnoreCase(final String substring) {
        return new SubstringMatcher("containing (ignoring case)", true, substring) {

            @Override
            protected boolean evalSubstringOf(String string) {
                return StringUtils.indexOfIgnoreCase(string, substring) != -1;
            }
        };
    }

}
