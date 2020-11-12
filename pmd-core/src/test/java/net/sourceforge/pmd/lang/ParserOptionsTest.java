/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

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

    @Test
    public void testEqualsAndHashCode() {
        ParserOptions parserOptions = new ParserOptions();
        TestParserOptions testParserOptions1 = new TestParserOptions();
        TestParserOptions testParserOptions2 = new TestParserOptions();

        // Differences based on Language
        assertNotNull(parserOptions.hashCode());
        assertFalse(parserOptions.equals(testParserOptions1));
        assertNotEquals(parserOptions.hashCode(), testParserOptions1.hashCode());

        // Differences based on Properties
        assertNotNull(testParserOptions1.hashCode());
        assertTrue(testParserOptions1.equals(testParserOptions2));
        assertEquals(testParserOptions1.hashCode(), testParserOptions2.hashCode());

        testParserOptions1.setProperty(TestParserOptions.LIST_DESCRIPTOR, OVERRIDDEN_LIST);
        assertFalse(testParserOptions1.equals(testParserOptions2));
        assertNotEquals(testParserOptions1.hashCode(), testParserOptions2.hashCode());

        testParserOptions1.setProperty(TestParserOptions.STRING_DESCRIPTOR, OVERRIDDEN_STRING);
        assertFalse(testParserOptions1.equals(testParserOptions2));
        assertNotEquals(testParserOptions1.hashCode(), testParserOptions2.hashCode());

        testParserOptions2.setProperty(TestParserOptions.LIST_DESCRIPTOR, OVERRIDDEN_LIST);
        assertFalse(testParserOptions1.equals(testParserOptions2));
        assertNotEquals(testParserOptions1.hashCode(), testParserOptions2.hashCode());

        testParserOptions2.setProperty(TestParserOptions.STRING_DESCRIPTOR, OVERRIDDEN_STRING);
        assertTrue(testParserOptions1.equals(testParserOptions2));
        assertEquals(testParserOptions1.hashCode(), testParserOptions2.hashCode());
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
