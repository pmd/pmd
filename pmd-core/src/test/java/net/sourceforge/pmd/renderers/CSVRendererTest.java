/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.Charset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextRange2d;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.Report.ConfigurationError;
import net.sourceforge.pmd.reporting.Report.ProcessingError;
import net.sourceforge.pmd.reporting.RuleViolation;

class CSVRendererTest extends AbstractRendererTest {

    @Override
    Renderer getRenderer() {
        return new CSVRenderer();
    }

    @Override
    String getExpected() {
        return getHeader()
                + "\"1\",\"\",\"" + getSourceCodeFilename() + "\",\"5\",\"1\",\"blah\",\"RuleSet\",\"Foo\"" + EOL;
    }

    @ParameterizedTest
    @CsvSource({"endLine,End Line", "beginColumn,Begin Column", "endColumn,End Column"})
    void withOptionalColumns(final String id, final String columnTitle) throws Exception {
        CSVRenderer renderer = new CSVRenderer();
        renderer.setProperty((PropertyDescriptor<Boolean>) renderer.getPropertyDescriptor(id), true);
        String actual = renderReport(renderer, reportOneViolation(), Charset.defaultCharset());

        String expected = "\"Problem\",\"Package\",\"File\",\"Priority\",\"Line\",\"" + columnTitle + "\",\"Description\",\"Rule set\",\"Rule\"" + EOL
                + "\"1\",\"\",\"" + getSourceCodeFilename() + "\",\"5\",\"1\",\"1\",\"blah\",\"RuleSet\",\"Foo\"" + EOL;
        assertEquals(filter(expected), filter(actual));
    }

    @Test
    @DisabledOnOs(OS.WINDOWS) // the character <"> is illegal in Windows filenames
    void verifyProperlyEscaping() throws Exception {
        CSVRenderer renderer = new CSVRenderer();
        String actual = renderReport(renderer,
                it -> {
                    Rule rule = createFooRule();
                    TextRange2d range2d = TextRange2d.range2d(5, 1, 5, 1);
                    FileLocation location = FileLocation.range(FileId.fromPathLikeString("Apfx\",=1+1,\".java"), range2d);
                    RuleViolation ruleViolation1 = newRuleViolation(rule, location, "message");
                    it.onRuleViolation(ruleViolation1);

                    // quote breakout
                    range2d = TextRange2d.range2d(6, 1, 6, 1);
                    location = FileLocation.range(FileId.fromPathLikeString("File2.java"), range2d);
                    RuleViolation ruleViolation2 = newRuleViolation(rule, location, "m\",=2+2,\"essage");
                    it.onRuleViolation(ruleViolation2);

                    // formula injection
                    range2d = TextRange2d.range2d(7, 1, 7, 1);
                    location = FileLocation.range(FileId.fromPathLikeString("File3.java"), range2d);
                    RuleViolation ruleViolation3 = newRuleViolation(rule, location, "=1+2");
                    it.onRuleViolation(ruleViolation3);
                },
            Charset.defaultCharset());

        String expected = getHeader()
                + "\"1\",\"\",\"Apfx\"\",=1+1,\"\".java\",\"5\",\"5\",\"message\",\"RuleSet\",\"Foo\"" + EOL
                + "\"2\",\"\",\"File2.java\",\"5\",\"6\",\"m',=2+2,'essage\",\"RuleSet\",\"Foo\"" + EOL
                + "\"3\",\"\",\"File3.java\",\"5\",\"7\",\"'=1+2\",\"RuleSet\",\"Foo\"" + EOL;
        assertEquals(filter(expected), filter(actual));
    }

    @Override
    String getExpectedEmpty() {
        return getHeader();
    }

    @Override
    String getExpectedMultiple() {
        return getHeader()
                + "\"1\",\"\",\"" + getSourceCodeFilename() + "\",\"5\",\"1\",\"blah\",\"RuleSet\",\"Foo\"" + EOL
                + "\"2\",\"\",\"" + getSourceCodeFilename() + "\",\"1\",\"1\",\"blah\",\"RuleSet\",\"Boo\"" + EOL;
    }

    @Override
    String getExpectedError(ProcessingError error) {
        return getHeader();
    }

    @Override
    String getExpectedError(ConfigurationError error) {
        return getHeader();
    }

    private String getHeader() {
        return "\"Problem\",\"Package\",\"File\",\"Priority\",\"Line\",\"Description\",\"Rule set\",\"Rule\"" + EOL;
    }
}
