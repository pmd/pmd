/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

public class YAHTMLRendererTest extends AbstractRendererTest {

    private String outputDir;

    @Before
    public void setUp() throws IOException {
        outputDir = getTemporaryDirectory("pmdtest").getAbsolutePath();
    }

    @After
    public void cleanUp() {
        deleteDirectory(new File(outputDir));
    }

    private File getTemporaryDirectory(String prefix) throws IOException {
        // TODO: move to util class?
        File dir = File.createTempFile(prefix, "");
        dir.delete();
        dir.mkdir();
        return dir;
    }

    private void deleteDirectory(File dir) {
        // TODO: move to util class?
        File[] a = dir.listFiles();
        if (a != null) {
            for (File f : a) {
                if (f.isDirectory()) {
                    deleteDirectory(f);
                } else {
                    f.delete();
                }
            }
        }
        dir.delete();
    }

    private RuleViolation newRuleViolation(int endColumn, final String packageNameArg, final String classNameArg) {
        DummyNode node = createNode(endColumn);
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFile(new File(getSourceCodeFilename()));
        return new ParametricRuleViolation<Node>(new FooRule(), ctx, node, "blah") {
            {
                packageName = packageNameArg;
                className = classNameArg;
            }
        };
    }

    @Override
    protected RuleViolation newRuleViolation(int endColumn) {
        return newRuleViolation(endColumn, "net.sf.pmd.test", "YAHTMLSampleClass");
    }

    @Test
    public void testReportMultipleViolations() throws Exception {
        Report report = new Report();
        report.addRuleViolation(newRuleViolation(1, "net.sf.pmd.test", "YAHTMLSampleClass1"));
        report.addRuleViolation(newRuleViolation(2, "net.sf.pmd.test", "YAHTMLSampleClass1"));
        report.addRuleViolation(newRuleViolation(1, "net.sf.pmd.other", "YAHTMLSampleClass2"));
        String actual = ReportTest.render(getRenderer(), report);
        assertEquals(filter(getExpected()), filter(actual));

        String[] htmlFiles = new File(outputDir).list();
        assertEquals(3, htmlFiles.length);
        Arrays.sort(htmlFiles);
        assertEquals("YAHTMLSampleClass1.html", htmlFiles[0]);
        assertEquals("YAHTMLSampleClass2.html", htmlFiles[1]);
        assertEquals("index.html", htmlFiles[2]);

        for (String file : htmlFiles) {
            try (FileInputStream in = new FileInputStream(new File(outputDir, file));
                    InputStream expectedIn = YAHTMLRendererTest.class.getResourceAsStream("yahtml/" + file)) {
                String data = IOUtils.toString(in, StandardCharsets.UTF_8);
                String expected = normalizeLineSeparators(IOUtils.toString(expectedIn, StandardCharsets.UTF_8));

                assertEquals("File " + file + " is different", expected, data);
            }
        }
    }

    private static String normalizeLineSeparators(String s) {
        return s.replaceAll(Pattern.quote(IOUtils.LINE_SEPARATOR_WINDOWS), IOUtils.LINE_SEPARATOR_UNIX)
                .replaceAll(Pattern.quote(IOUtils.LINE_SEPARATOR_UNIX), PMD.EOL);
    }

    @Override
    public Renderer getRenderer() {
        Renderer result = new YAHTMLRenderer();
        result.setProperty(YAHTMLRenderer.OUTPUT_DIR, outputDir);
        return result;
    }

    @Override
    public String getExpected() {
        return "<h3 align=\"center\">The HTML files are located in '" + outputDir + "'.</h3>" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return getExpected();
    }

    @Override
    public String getExpectedMultiple() {
        return getExpected();
    }

    @Override
    public String getExpectedError(ProcessingError error) {
        return getExpected();
    }

    @Override
    public String getExpectedError(ConfigurationError error) {
        return getExpected();
    }
}
