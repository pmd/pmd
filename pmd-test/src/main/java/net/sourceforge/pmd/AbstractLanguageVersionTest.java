/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import net.sourceforge.pmd.ant.SourceLanguage;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;

/**
 * Base test class for {@link LanguageVersion} implementations. <br>
 * Each language implementation should subclass this and provide a method called {@code data}.
 *
 * <pre>
 *     static Collection&lt;TestDescriptor&gt; data() {
 *       return Arrays.asList(
 *            new TestDescriptor(MyLanguageModule.NAME, MyLanguageModule.TERSE_NAME, "1.1",
 *                               LanguageRegistry.getLanguage(MyLanguageModule.NAME).getVersion("1.1")),
 *            new TestDescriptor(MyLanguageModule.NAME, MyLanguageModule.TERSE_NAME, "1.2",
 *                               LanguageRegistry.getLanguage(MyLanguageModule.NAME).getVersion("1.2")),
 *
 *            // doesn't exist
 *            new TestDescriptor(MyLanguageModule.NAME, MyLanguageModule.TERSE_NAME, "1.3", null)
 *       };
 * </pre>
 *
 * <p>For the parameters, see the constructor
 * {@link TestDescriptor#TestDescriptor(String, String, String, LanguageVersion)}.</p>
 */
public abstract class AbstractLanguageVersionTest {

    public static class TestDescriptor {
        private final String name;
        private final String version;
        private final String simpleTerseName;
        private final LanguageVersion expected;

        /**
         * Creates a new {@link TestDescriptor}
         *
         * @param name
         *            the name under which the language module is registered
         * @param terseName
         *            the terse name under which the language module is registered
         * @param version
         *            the specific version of the language version
         * @param expected
         *            the expected {@link LanguageVersion} instance
         */
        public TestDescriptor(String name, String terseName, String version, LanguageVersion expected) {
            this.name = name;
            this.version = version;
            this.simpleTerseName = terseName;
            this.expected = expected;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public String getSimpleTerseName() {
            return simpleTerseName;
        }

        public LanguageVersion getExpected() {
            return expected;
        }
    }


    protected static Language getLanguage(String name) {
        return LanguageRegistry.PMD.getLanguageByFullName(name);
    }

    /**
     * Checks that the expected {@link LanguageVersion} can be found via
     * {@link TestDescriptor#name} and {@link TestDescriptor#version}.
     */
    @ParameterizedTest
    @MethodSource("data")
    void testFindVersionsForLanguageNameAndVersion(TestDescriptor testDescriptor) {
        SourceLanguage sourceLanguage = new SourceLanguage();
        sourceLanguage.setName(testDescriptor.getName());
        sourceLanguage.setVersion(testDescriptor.getVersion());

        Language language = getLanguage(sourceLanguage.getName());
        LanguageVersion languageVersion = null;
        if (language != null) {
            languageVersion = language.getVersion(sourceLanguage.getVersion());
        }

        assertEquals(testDescriptor.getExpected(), languageVersion);
    }

    /**
     * Makes sure, that for each language a "categories.properties" file exists.
     *
     * @throws Exception
     *             any error
     */
    @ParameterizedTest
    @MethodSource("data")
    void testRegisteredRulesets(TestDescriptor testDescriptor) throws Exception {
        if (testDescriptor.getExpected() == null) {
            return;
        }

        Properties props = new Properties();
        String rulesetsProperties = "/category/" + testDescriptor.getSimpleTerseName() + "/categories.properties";
        try (InputStream inputStream = getClass().getResourceAsStream(rulesetsProperties)) {
            if (inputStream == null) {
                throw new IOException();
            }
            props.load(inputStream);
        }
        assertRulesetsAndCategoriesProperties(props);
    }

    /**
     * If a rulesets.properties file still exists, test it as well.
     *
     * @throws Exception
     *             any error
     */
    @ParameterizedTest
    @MethodSource("data")
    void testOldRegisteredRulesets(TestDescriptor testDescriptor) throws Exception {
        // only check for languages, that support rules
        if (testDescriptor.getExpected() == null) {
            return;
        }

        Properties props = new Properties();
        String rulesetsProperties = "/rulesets/" + testDescriptor.getSimpleTerseName() + "/rulesets.properties";
        InputStream inputStream = getClass().getResourceAsStream(rulesetsProperties);
        if (inputStream != null) {
            // rulesets.properties file exists
            try (InputStream in = inputStream) {
                props.load(in);
            }
            assertRulesetsAndCategoriesProperties(props);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    void testVersionsAreDistinct(TestDescriptor testDescriptor) {
        LanguageVersion expected = testDescriptor.getExpected();
        if (expected == null) {
            return;
        }

        Language lang = expected.getLanguage();

        int count = 0;
        for (LanguageVersion lv : lang.getVersions()) {
            if (lv.equals(expected)) {
                count++;
            }
        }

        assertEquals(1, count, "Expected exactly one occurrence of " + expected
                + " in the language versions of its language");
    }

    private void assertRulesetsAndCategoriesProperties(Properties props) throws IOException {
        String rulesetFilenames = props.getProperty("rulesets.filenames");
        assertNotNull(rulesetFilenames);

        RuleSetLoader rulesetLoader = new RuleSetLoader();

        if (rulesetFilenames.trim().isEmpty()) {
            return;
        }

        String[] rulesets = rulesetFilenames.split(",");
        for (String r : rulesets) {
            RuleSet ruleset = rulesetLoader.loadFromResource(r);
            assertNotNull(ruleset);
        }
    }
}
