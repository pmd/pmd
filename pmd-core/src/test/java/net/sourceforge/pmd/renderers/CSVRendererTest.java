/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.Report.ConfigurationError;
import net.sourceforge.pmd.reporting.Report.ProcessingError;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    @EnumSource(CSVRenderer.CSVColumn.class)
    void withOptionalColumns(final CSVRenderer.CSVColumn col) throws Exception {
        CSVRenderer renderer = new CSVRenderer();
        renderer.setProperty((PropertyDescriptor<Boolean>) renderer.getPropertyDescriptor(col.id), true);
        String actual = renderReport(renderer, reportOneViolation(), Charset.defaultCharset());

        String expected = "\"Problem\",\"Package\",\"File\",\"Priority\",\"Line\",\"" + col.title + "\",\"Description\",\"Rule set\",\"Rule\"" + EOL
                + "\"1\",\"\",\"" + getSourceCodeFilename() + "\",\"5\",\"1\",\"1\",\"blah\",\"RuleSet\",\"Foo\"" + EOL;
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
