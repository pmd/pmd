/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.schema;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;

import net.sourceforge.pmd.lang.PlainTextLanguage;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.reporting.RuleContext;

import com.github.stefanbirkner.systemlambda.SystemLambda;

/**
 * @author Clément Fournier
 */
class TestSchemaParserTest {

    @Test
    void testSchemaSimple() throws IOException {
        String file = """
                      <?xml version="1.0" encoding="UTF-8"?>
                      <test-data
                              xmlns="http://pmd.sourceforge.net/rule-tests"
                              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                              xsi:schemaLocation="http://pmd.sourceforge.net/rule-tests net/sourceforge/pmd/test/schema/rule-tests_1_0_0.xsd">
                          <test-code>
                              <description>equality operators with Double.NaN</description>
                              <expected-problems>4</expected-problems>
                              <code><![CDATA[
                                  public class Foo {
                                      private int i;
                                  }
                                  ]]></code>
                          </test-code>
                          <test-code>
                              <description>equality operators with Float.NaN</description>
                              <expected-problems>4</expected-problems>
                              <code><![CDATA[
                                  public class Foo {
                                  }
                                  ]]></code>
                          </test-code>
                      </test-data>
                      """;

        RuleTestCollection parsed = parseFile(file);

        assertEquals(2, parsed.getTests().size());
        assertThat("Indentation should be removed",
                parsed.getTests().get(0).getCode(), equalTo("public class Foo {\n    private int i;\n}"));
    }

    @Test
    void testSharedCodeFragment() throws IOException {
        String file = """
                <?xml version="1.0" encoding="UTF-8"?>
                <test-data
                        xmlns="http://pmd.sourceforge.net/rule-tests"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        xsi:schemaLocation="http://pmd.sourceforge.net/rule-tests net/sourceforge/pmd/test/schema/rule-tests_1_0_0.xsd">
                    <code-fragment id="code1"><![CDATA[
                        public class Foo {
                            private int i;
                        }
                        ]]></code-fragment>
                    <test-code>
                        <description>equality operators with Double.NaN</description>
                        <expected-problems>4</expected-problems>
                        <code-ref id="code1" />
                    </test-code>
                </test-data>
                """;

        RuleTestCollection parsed = parseFile(file);

        assertEquals(1, parsed.getTests().size());
        assertThat("Indentation should be removed",
                parsed.getTests().get(0).getCode(), equalTo("public class Foo {\n    private int i;\n}"));
    }

    @Test
    void testSchemaDeprecatedAttr() throws Exception {
        String file = """
                      <?xml version="1.0" encoding="UTF-8"?>
                      <test-data
                              xmlns="http://pmd.sourceforge.net/rule-tests"
                              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                              xsi:schemaLocation="http://pmd.sourceforge.net/rule-tests net/sourceforge/pmd/test/schema/rule-tests_1_0_0.xsd">
                          <test-code regressionTest='false'>
                              <description>equality operators with Double.NaN</description>
                              <expected-problems>4</expected-problems>
                              <code><![CDATA[
                                  public class Foo {
                                  }
                                  ]]></code>
                          </test-code>
                      </test-data>
                      """;

        String log = SystemLambda.tapSystemErr(() -> {
            RuleTestCollection parsed = parseFile(file);
            assertEquals(1, parsed.getTests().size());
        });

        assertThat(log, containsString("""
                                               6|     <test-code regressionTest='false'>
                                                                 ^^^^^^^^^^^^^^ Attribute 'regressionTest' is deprecated, use 'disabled' with inverted value
                                              """));
    }

    @Test
    void testUnknownProperty() throws Exception {
        String file = """
                <?xml version="1.0" encoding="UTF-8"?>
                <test-data
                        xmlns="http://pmd.sourceforge.net/rule-tests"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        xsi:schemaLocation="http://pmd.sourceforge.net/rule-tests net/sourceforge/pmd/test/schema/rule-tests_1_0_0.xsd">
                    <test-code>
                        <description>equality operators with Double.NaN</description>
                        <rule-property name='invalid_property'>foo</rule-property>
                        <expected-problems>0</expected-problems>
                        <code><![CDATA[
                            public class Foo {
                            }
                            ]]></code>
                    </test-code>
                </test-data>
                """;

        String log = SystemLambda.tapSystemErr(() -> {
            assertThrows(IllegalStateException.class, () -> parseFile(file));
        });

        assertThat(log, containsString("""
                                               8|         <rule-property name='invalid_property'>foo</rule-property>
                                                                         ^^^^ Unknown property, known property names are violationSuppressRegex, violationSuppressXPath
                                             """));
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
