/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.reporting.InternalApiBridge;
import net.sourceforge.pmd.reporting.Report.ConfigurationError;
import net.sourceforge.pmd.reporting.Report.ProcessingError;
import net.sourceforge.pmd.reporting.RuleViolation;
import net.sourceforge.pmd.util.CollectionUtil;

class YAHTMLRendererTest extends AbstractRendererTest {

    private File outputDir;

    @TempDir
    private Path folder;

    @BeforeEach
    void setUp() {
        outputDir = folder.resolve("pmdtest").toFile();
        assertTrue(outputDir.mkdir());
    }

    private RuleViolation newRuleViolation(int beginLine, int beginColumn, int endLine, int endColumn, final String packageNameArg, final String classNameArg) {
        FileLocation loc = createLocation(beginLine, beginColumn, endLine, endColumn);
        Map<String, String> additionalInfo = CollectionUtil.mapOf(RuleViolation.PACKAGE_NAME, packageNameArg,
                                                                  RuleViolation.CLASS_NAME, classNameArg);
        return InternalApiBridge.createRuleViolation(new FooRule(), loc, "blah", additionalInfo);
    }

    @Override
    protected RuleViolation newRuleViolation(int beginLine, int beginColumn, int endLine, int endColumn, Rule rule) {
        return newRuleViolation(beginLine, beginColumn, endLine, endColumn, "net.sf.pmd.test", "YAHTMLSampleClass");
    }

    @Test
    void testReportMultipleViolations() throws Exception {
        String actual = renderReport(getRenderer(), it -> {
            it.onRuleViolation(newRuleViolation(1, 1, 1, 1, "net.sf.pmd.test", "YAHTMLSampleClass1"));
            it.onRuleViolation(newRuleViolation(1, 1, 1, 2, "net.sf.pmd.test", "YAHTMLSampleClass1"));
            it.onRuleViolation(newRuleViolation(1, 1, 1, 1, "net.sf.pmd.other", "YAHTMLSampleClass2"));
        });
        assertEquals(filter(getExpected()), filter(actual));

        String[] htmlFiles = outputDir.list();
        assertEquals(3, htmlFiles.length);
        Arrays.sort(htmlFiles);
        assertEquals("YAHTMLSampleClass1.html", htmlFiles[0]);
        assertEquals("YAHTMLSampleClass2.html", htmlFiles[1]);
        assertEquals("index.html", htmlFiles[2]);

        for (String file : htmlFiles) {
            try (FileInputStream in = new FileInputStream(new File(outputDir, file));
                    InputStream expectedIn = YAHTMLRendererTest.class.getResourceAsStream("yahtml/" + file)) {
                String data = IOUtil.readToString(in, StandardCharsets.UTF_8);
                String expected = normalizeLineSeparators(IOUtil.readToString(expectedIn, StandardCharsets.UTF_8));

                assertEquals(expected, data, "File " + file + " is different");
            }
        }
    }

    private static String normalizeLineSeparators(String s) {
        return s.replaceAll("\\R", System.lineSeparator());
    }

    @Override
    Renderer getRenderer() {
        Renderer result = new YAHTMLRenderer();
        result.setProperty(YAHTMLRenderer.OUTPUT_DIR, outputDir.getAbsolutePath());
        return result;
    }

    @Override
    String getExpected() {
        return "<h3 align=\"center\">The HTML files are located in '" + outputDir + "'.</h3>" + System.lineSeparator();
    }

    @Override
    String getExpectedEmpty() {
        return getExpected();
    }

    @Override
    String getExpectedMultiple() {
        return getExpected();
    }

    @Override
    String getExpectedError(ProcessingError error) {
        return getExpected();
    }

    @Override
    String getExpectedError(ConfigurationError error) {
        return getExpected();
    }
}
