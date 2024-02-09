/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import static net.sourceforge.pmd.util.CollectionUtil.mapOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.rule.RuleSet.RuleSetBuilder;
import net.sourceforge.pmd.lang.rule.internal.RuleSetReference;
import net.sourceforge.pmd.lang.rule.xpath.XPathRule;
import net.sourceforge.pmd.util.internal.xml.SchemaConstants;

/**
 * Unit test for {@link RuleSetWriter}.
 *
 */
class RuleSetWriterTest extends RulesetFactoryTestBase {

    private ByteArrayOutputStream out;
    private RuleSetWriter writer;

    /**
     * Prepare the output stream.
     */
    @BeforeEach
    void setupOutputStream() {
        out = new ByteArrayOutputStream();
        writer = new RuleSetWriter(out);
    }

    /**
     * Closes the output stream at the end.
     */
    @AfterEach
    void cleanupStream() {
        if (writer != null) {
            writer.close();
        }
    }

    /**
     * Tests the exclude rule behavior. See bug #945.
     *
     * @throws Exception
     *             any error
     */
    @Test
    void testWrite() throws Exception {
        RuleSet braces = new RuleSetLoader().loadFromResource("net/sourceforge/pmd/lang/rule/TestRuleset1.xml");
        RuleSet ruleSet = new RuleSetBuilder(new Random().nextLong())
                .withName("ruleset")
                .withDescription("ruleset description")
                .addRuleSetByReference(braces, true, "MockRule2")
                .build();

        writer.write(ruleSet);

        String written = out.toString(StandardCharsets.UTF_8.name());
        assertTrue(written.contains("<exclude name=\"MockRule2\""));
    }

    /**
     * Unit test for #1312 see https://sourceforge.net/p/pmd/bugs/1312/
     *
     * @throws Exception
     *             any error
     */
    @Test
    void testRuleReferenceOverriddenName() throws Exception {
        RuleSet rs = new RuleSetLoader().loadFromResource("rulesets/dummy/basic.xml");

        RuleReference ruleRef = new RuleReference(
                rs.getRuleByName("DummyBasicMockRule"),
                new RuleSetReference("rulesets/dummy/basic.xml"));
        ruleRef.setName("Foo"); // override the name

        RuleSet ruleSet = RuleSet.forSingleRule(ruleRef);

        writer.write(ruleSet);

        String written = out.toString(StandardCharsets.UTF_8.name());
        assertTrue(written.contains("ref=\"rulesets/dummy/basic.xml/DummyBasicMockRule\""));
    }

    @Test
    void testPropertyConstraintRange() throws Exception {
        RuleSet ruleSet = loadRuleSet("created-on-the-fly.xml",
                rulesetXml(
                        dummyRule(
                                attrs -> attrs.put(SchemaConstants.CLASS, XPathRule.class.getName()),
                                properties(
                                        propertyWithValueAttr("xpath", "//foo"),
                                        propertyDefWithValueAttr("rangeProp", "the description", "Integer", "5",
                                                mapOf(SchemaConstants.PROPERTY_MIN, "0", SchemaConstants.PROPERTY_MAX, "10"))
                                )
                        )
                )
        );

        writer.write(ruleSet);
        String written = out.toString(StandardCharsets.UTF_8.name());
        assertThat(written, containsString("min=\"0\""));
        assertThat(written, containsString("max=\"10\""));
    }

    @Test
    void testPropertyConstraintAbove() throws Exception {
        RuleSet ruleSet = loadRuleSet("created-on-the-fly.xml",
                rulesetXml(
                        dummyRule(
                                attrs -> attrs.put(SchemaConstants.CLASS, XPathRule.class.getName()),
                                properties(
                                        propertyWithValueAttr("xpath", "//foo"),
                                        propertyDefWithValueAttr("rangeProp", "the description", "Integer", "5",
                                                mapOf(SchemaConstants.PROPERTY_MIN, "0"))
                                )
                        )
                )
        );

        writer.write(ruleSet);
        String written = out.toString(StandardCharsets.UTF_8.name());
        assertThat(written, containsString("min=\"0\""));
        assertThat(written, not(containsString("max=\"")));
    }

    @Test
    void testPropertyConstraintBelow() throws Exception {
        RuleSet ruleSet = loadRuleSet("created-on-the-fly.xml",
                rulesetXml(
                        dummyRule(
                                attrs -> attrs.put(SchemaConstants.CLASS, XPathRule.class.getName()),
                                properties(
                                        propertyWithValueAttr("xpath", "//foo"),
                                        propertyDefWithValueAttr("rangeProp", "the description", "Integer", "5",
                                                mapOf(SchemaConstants.PROPERTY_MAX, "10"))
                                )
                        )
                )
        );

        writer.write(ruleSet);
        String written = out.toString(StandardCharsets.UTF_8.name());
        assertThat(written, not(containsString("min=\"")));
        assertThat(written, containsString("max=\"10\""));
    }
}
