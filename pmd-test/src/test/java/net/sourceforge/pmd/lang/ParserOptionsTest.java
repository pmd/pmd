/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.test.lang.DummyLanguageModule;

/**
 * Unit tests for {@link ParserOptions}.
 * This class is located in the pmd-test project instead of pmd-core so that it can invoke
 * {@link ParserOptionsTestUtils#verifyOptionsEqualsHashcode}
 */
public class ParserOptionsTest {
    private static final List<String> DEFAULT_LIST = Arrays.asList("value1", "value2");
    private static final String DEFAULT_STRING = "value3";
    private static final List<String> OVERRIDDEN_LIST = Arrays.asList("override1", "override2");
    private static final String OVERRIDDEN_STRING = "override3";

    private static class TestParserOptions extends ParserOptions {
        private static final PropertyDescriptor<List<String>> LIST_DESCRIPTOR =
                PropertyFactory.stringListProperty("listOfStringValues")
                        .desc("A list of values for testing.")
                        .defaultValue(DEFAULT_LIST)
                        .delim(',')
                        .build();

        private static final PropertyDescriptor<String> STRING_DESCRIPTOR =
                PropertyFactory.stringProperty("stringValue")
                        .desc("A single value for testing.")
                        .defaultValue(DEFAULT_STRING)
                        .build();

        private TestParserOptions() {
            super(new DummyLanguageModule());
            definePropertyDescriptor(LIST_DESCRIPTOR);
            definePropertyDescriptor(STRING_DESCRIPTOR);
            overridePropertiesFromEnv();
        }
    }

    /**
     * SuppressMarker should be initially null and changeable.
     */
    @Test
    public void testSuppressMarker() {
        ParserOptions parserOptions = new ParserOptions();
        Assert.assertNull(parserOptions.getSuppressMarker());
        parserOptions.setSuppressMarker("foo");
        Assert.assertEquals("foo", parserOptions.getSuppressMarker());
    }

    @Test
    public void testDefaultPropertyDescriptors() {
        TestParserOptions parserOptions = new TestParserOptions();
        assertEquals(DEFAULT_LIST, parserOptions.getProperty(TestParserOptions.LIST_DESCRIPTOR));
        assertEquals(DEFAULT_STRING, parserOptions.getProperty(TestParserOptions.STRING_DESCRIPTOR));
        assertEquals("ParserOptions", parserOptions.getPropertySourceType());
        assertEquals("TestParserOptions", parserOptions.getName());
    }

    @Test
    public void testOverriddenPropertyDescriptors() {
        TestParserOptions parserOptions = new TestParserOptions();
        parserOptions.setProperty(TestParserOptions.LIST_DESCRIPTOR, OVERRIDDEN_LIST);
        parserOptions.setProperty(TestParserOptions.STRING_DESCRIPTOR, OVERRIDDEN_STRING);

        assertEquals(OVERRIDDEN_LIST, parserOptions.getProperty(TestParserOptions.LIST_DESCRIPTOR));
        assertEquals(OVERRIDDEN_STRING, parserOptions.getProperty(TestParserOptions.STRING_DESCRIPTOR));
    }

    @Test
    public void testEnvOverriddenPropertyDescriptors() {
        TestParserOptions parserOptions = new TestParserOptions() {
            @Override
            protected String getEnvValue(PropertyDescriptor propertyDescriptor) {
                if (propertyDescriptor.equals(TestParserOptions.LIST_DESCRIPTOR)) {
                    return StringUtils.join(OVERRIDDEN_LIST, ",");
                } else if (propertyDescriptor.equals(TestParserOptions.STRING_DESCRIPTOR)) {
                    return OVERRIDDEN_STRING;
                } else {
                    throw new RuntimeException("Should not happen");
                }
            }
        };

        assertEquals(OVERRIDDEN_LIST, parserOptions.getProperty(TestParserOptions.LIST_DESCRIPTOR));
        assertEquals(OVERRIDDEN_STRING, parserOptions.getProperty(TestParserOptions.STRING_DESCRIPTOR));
    }

    @Test
    public void testEmptyPropertyDescriptors() {
        TestParserOptions vfParserOptions = new TestParserOptions() {
            @Override
            protected String getEnvValue(PropertyDescriptor propertyDescriptor) {
                if (propertyDescriptor.equals(TestParserOptions.LIST_DESCRIPTOR)
                        || propertyDescriptor.equals(TestParserOptions.STRING_DESCRIPTOR)) {
                    return "";
                } else {
                    throw new RuntimeException("Should not happen");
                }
            }
        };

        assertEquals(Collections.emptyList(), vfParserOptions.getProperty(TestParserOptions.LIST_DESCRIPTOR));
        assertEquals("", vfParserOptions.getProperty(TestParserOptions.STRING_DESCRIPTOR));
    }

    /**
     * Verify that the equals and hashCode methods work as expected.
     * TODO: Consider using Guava's EqualsTester
     */
    @Test
    public void testSuppressMarkerEqualsHashCode() {
        ParserOptions options1;
        ParserOptions options2;
        ParserOptions options3;
        ParserOptions options4;

        // SuppressMarker
        options1 = new ParserOptions();
        options2 = new ParserOptions();
        options3 = new ParserOptions();
        options4 = new ParserOptions();
        options1.setSuppressMarker("foo");
        options2.setSuppressMarker("bar");
        options3.setSuppressMarker("foo");
        options4.setSuppressMarker("bar");
        ParserOptionsTestUtils.verifyOptionsEqualsHashcode(options1, options2, options3, options4);

        // PropertyDescriptor
        options1 = new ParserOptions();
        options2 = new ParserOptions();
        options3 = new ParserOptions();
        options4 = new ParserOptions();
        options1.definePropertyDescriptor(TestParserOptions.LIST_DESCRIPTOR);
        options2.definePropertyDescriptor(TestParserOptions.STRING_DESCRIPTOR);
        options3.definePropertyDescriptor(TestParserOptions.LIST_DESCRIPTOR);
        options4.definePropertyDescriptor(TestParserOptions.STRING_DESCRIPTOR);
        ParserOptionsTestUtils.verifyOptionsEqualsHashcode(options1, options2, options3, options4);

        // PropertyValue
        options1 = new ParserOptions();
        options2 = new ParserOptions();
        options3 = new ParserOptions();
        options4 = new ParserOptions();
        options1.definePropertyDescriptor(TestParserOptions.STRING_DESCRIPTOR);
        options1.setProperty(TestParserOptions.STRING_DESCRIPTOR, DEFAULT_STRING);
        options2.definePropertyDescriptor(TestParserOptions.STRING_DESCRIPTOR);
        options2.setProperty(TestParserOptions.STRING_DESCRIPTOR, OVERRIDDEN_STRING);
        options3.definePropertyDescriptor(TestParserOptions.STRING_DESCRIPTOR);
        options3.setProperty(TestParserOptions.STRING_DESCRIPTOR, DEFAULT_STRING);
        options4.definePropertyDescriptor(TestParserOptions.STRING_DESCRIPTOR);
        options4.setProperty(TestParserOptions.STRING_DESCRIPTOR, OVERRIDDEN_STRING);
        ParserOptionsTestUtils.verifyOptionsEqualsHashcode(options1, options2, options3, options4);

        // Language
        options1 = new ParserOptions(new DummyLanguageModule());
        options2 = new ParserOptions();
        options3 = new ParserOptions(new DummyLanguageModule());
        options4 = new ParserOptions();
        ParserOptionsTestUtils.verifyOptionsEqualsHashcode(options1, options2, options3, options4);

        // SuppressMarker, PropertyDescriptor, PropertyValue, Language
        options1 = new ParserOptions(new DummyLanguageModule());
        options2 = new ParserOptions();
        options3 = new ParserOptions(new DummyLanguageModule());
        options4 = new ParserOptions();
        options1.setSuppressMarker("foo");
        options2.setSuppressMarker("bar");
        options3.setSuppressMarker("foo");
        options4.setSuppressMarker("bar");
        options1.definePropertyDescriptor(TestParserOptions.LIST_DESCRIPTOR);
        options1.setProperty(TestParserOptions.LIST_DESCRIPTOR, DEFAULT_LIST);
        options2.definePropertyDescriptor(TestParserOptions.STRING_DESCRIPTOR);
        options2.setProperty(TestParserOptions.STRING_DESCRIPTOR, OVERRIDDEN_STRING);
        options3.definePropertyDescriptor(TestParserOptions.LIST_DESCRIPTOR);
        options3.setProperty(TestParserOptions.LIST_DESCRIPTOR, DEFAULT_LIST);
        options4.definePropertyDescriptor(TestParserOptions.STRING_DESCRIPTOR);
        options4.setProperty(TestParserOptions.STRING_DESCRIPTOR, OVERRIDDEN_STRING);
        ParserOptionsTestUtils.verifyOptionsEqualsHashcode(options1, options2, options3, options4);

        assertFalse(options1.equals(null));
    }

    @Test
    public void testGetEnvironmentVariableName() {
        ParserOptions parserOptions = new TestParserOptions();
        assertEquals("PMD_DUMMY_LISTOFSTRINGVALUES",
                parserOptions.getEnvironmentVariableName(TestParserOptions.LIST_DESCRIPTOR));
        assertEquals("PMD_DUMMY_STRINGVALUE",
                parserOptions.getEnvironmentVariableName(TestParserOptions.STRING_DESCRIPTOR));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetEnvironmentVariableNameThrowsExceptionIfLanguageIsNull() {
        ParserOptions parserOptions = new ParserOptions();
        parserOptions.getEnvironmentVariableName(TestParserOptions.LIST_DESCRIPTOR);
    }
}
