/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import net.sourceforge.pmd.ant.SourceLanguage;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.ResourceLoader;

/**
 * Base test class for {@link LanguageVersion} implementations. <br>
 * Each language implementation should subclass this and provide a data method.
 * 
 * <pre>
 * &#64;Parameters
 *     public static Collection<Object[]> data() {
 *       return Arrays.asList(new Object[][] {
 *            { MyLanguageModule.NAME, MyLanguageModule.TERSE_NAME, "1.1",
 *              LanguageRegistry.getLanguage(MyLanguageModule.NAME).getVersion("1.1") },
 *            { MyLanguageModule.NAME, MyLanguageModule.TERSE_NAME, "1.2",
 *              LanguageRegistry.getLanguage(MyLanguageModule.NAME).getVersion("1.2") },
 *
 *            // doesn't exist
 *            { MyLanguageModule.NAME, MyLanguageModule.TERSE_NAME, "1.3",
 *              null }
 *       });
 * </pre>
 * 
 * For the parameters, see the constructor
 * {@link #AbstractLanguageVersionTest(String, String, String, LanguageVersion)}.
 */
@RunWith(Parameterized.class)
public class AbstractLanguageVersionTest {

    private String name;
    private String version;
    private String simpleTerseName;
    private String terseName;
    private LanguageVersion expected;

    /**
     * Creates a new {@link AbstractLanguageVersionTest}
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
    public AbstractLanguageVersionTest(String name, String terseName, String version, LanguageVersion expected) {
        this.name = name;
        this.version = version;
        this.simpleTerseName = terseName;
        this.terseName = terseName;
        if (version != null && !version.isEmpty()) {
            this.terseName += " " + version;
        }
        this.expected = expected;
    }

    /**
     * Checks that the expected {@link LanguageVersion} can be found by the
     * combination of {@link #terseName} and {@link #version}.
     */
    @Test
    public void testGetLanguageVersionForTerseName() {
        assertEquals(expected, LanguageRegistry.findLanguageVersionByTerseName(terseName));
    }

    /**
     * Checks that the expected {@link LanguageVersion} can be found via
     * {@link #name} and {@link #version}.
     */
    @Test
    public void testFindVersionsForLanguageNameAndVersion() {
        SourceLanguage sourceLanguage = new SourceLanguage();
        sourceLanguage.setName(name);
        sourceLanguage.setVersion(version);

        Language language = LanguageRegistry.getLanguage(sourceLanguage.getName());
        LanguageVersion languageVersion = null;
        if (language != null) {
            languageVersion = language.getVersion(sourceLanguage.getVersion());
        }

        assertEquals(expected, languageVersion);
    }

    /**
     * Makes sure, that for each language a "rulesets.properties" file exists.
     * 
     * @throws Exception
     *             any error
     */
    @Test
    public void testRegisteredRulesets() throws Exception {
        // only check for languages, that support rules
        if (expected == null || expected.getLanguage().getRuleChainVisitorClass() == null) {
            return;
        }

        Properties props = new Properties();
        String rulesetsProperties = "rulesets/" + simpleTerseName + "/rulesets.properties";
        props.load(ResourceLoader.loadResourceAsStream(rulesetsProperties));
        String rulesetFilenames = props.getProperty("rulesets.filenames");
        assertNotNull(rulesetFilenames);

        RuleSetFactory factory = new RuleSetFactory();

        if (rulesetFilenames.trim().isEmpty()) {
            return;
        }

        String[] rulesets = rulesetFilenames.split(",");
        for (String r : rulesets) {
            InputStream stream = ResourceLoader.loadResourceAsStream(r);
            assertNotNull(stream);
            RuleSet ruleset = factory.createRuleSet(r);
            assertNotNull(ruleset);
        }
    }
}
