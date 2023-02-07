/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.schema;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.PlainTextLanguage;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;

import com.github.stefanbirkner.systemlambda.SystemLambda;

/**
 * @author Clément Fournier
 */
class TestSchemaParserTest {

    @Test
    void testSchemaSimple() throws IOException {
        String file = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                      + "<test-data\n"
                      + "        xmlns=\"http://pmd.sourceforge.net/rule-tests\"\n"
                      + "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                      + "        xsi:schemaLocation=\"http://pmd.sourceforge.net/rule-tests net/sourceforge/pmd/test/schema/rule-tests_1_0_0.xsd\">\n"
                      + "    <test-code>\n"
                      + "        <description>equality operators with Double.NaN</description>\n"
                      + "        <expected-problems>4</expected-problems>\n"
                      + "        <code><![CDATA[\n"
                      + "            public class Foo {\n"
                      + "            }\n"
                      + "            ]]></code>\n"
                      + "    </test-code>\n"
                      + "    <test-code>\n"
                      + "        <description>equality operators with Float.NaN</description>\n"
                      + "        <expected-problems>4</expected-problems>\n"
                      + "        <code><![CDATA[\n"
                      + "            public class Foo {\n"
                      + "            }\n"
                      + "            ]]></code>\n"
                      + "    </test-code>\n"
                      + "</test-data>\n";

        RuleTestCollection parsed = parseFile(file);

        assertEquals(2, parsed.getTests().size());
    }

    @Test
    void testSchemaDeprecatedAttr() throws Exception {
        String file = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                      + "<test-data\n"
                      + "        xmlns=\"http://pmd.sourceforge.net/rule-tests\"\n"
                      + "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                      + "        xsi:schemaLocation=\"http://pmd.sourceforge.net/rule-tests net/sourceforge/pmd/test/schema/rule-tests_1_0_0.xsd\">\n"
                      + "    <test-code regressionTest='false'>\n"
                      + "        <description>equality operators with Double.NaN</description>\n"
                      + "        <expected-problems>4</expected-problems>\n"
                      + "        <code><![CDATA[\n"
                      + "            public class Foo {\n"
                      + "            }\n"
                      + "            ]]></code>\n"
                      + "    </test-code>\n"
                      + "</test-data>\n";

        String log = SystemLambda.tapSystemErr(() -> {
            RuleTestCollection parsed = parseFile(file);
            assertEquals(1, parsed.getTests().size());
        });

        assertThat(log, containsString(" 6|     <test-code regressionTest='false'>\n"
                                              + "                   ^^^^^^^^^^^^^^ Attribute 'regressionTest' is deprecated, use 'disabled' with inverted value\n"));
    }

    @Test
    void testUnknownProperty() throws Exception {
        String file = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<test-data\n"
                + "        xmlns=\"http://pmd.sourceforge.net/rule-tests\"\n"
                + "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "        xsi:schemaLocation=\"http://pmd.sourceforge.net/rule-tests net/sourceforge/pmd/test/schema/rule-tests_1_0_0.xsd\">\n"
                + "    <test-code>\n"
                + "        <description>equality operators with Double.NaN</description>\n"
                + "        <rule-property name='invalid_property'>foo</rule-property>\n"
                + "        <expected-problems>0</expected-problems>\n"
                + "        <code><![CDATA[\n"
                + "            public class Foo {\n"
                + "            }\n"
                + "            ]]></code>\n"
                + "    </test-code>\n"
                + "</test-data>\n";

        String log = SystemLambda.tapSystemErr(() -> {
            assertThrows(IllegalStateException.class, () -> parseFile(file));
        });

        assertThat(log, containsString("  8|         <rule-property name='invalid_property'>foo</rule-property>\n"
                                             + "                            ^^^^ Unknown property, known property names are violationSuppressRegex, violationSuppressXPath\n"));
    }

    private RuleTestCollection parseFile(String file) throws IOException {
        MockRule mockRule = new MockRule();
        mockRule.setLanguage(PlainTextLanguage.getInstance());

        InputSource is = new InputSource();
        is.setSystemId("a/file.xml");
        is.setCharacterStream(new StringReader(file));

        return new TestSchemaParser().parse(mockRule, is);
    }

    public static final class MockRule extends AbstractRule {
        @Override
        public void apply(Node target, RuleContext ctx) {
            // do nothing
        }
    }

}
