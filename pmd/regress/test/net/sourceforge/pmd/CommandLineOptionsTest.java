/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.InputStreamReader;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.pmd.CommandLineOptions;
import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.EmacsRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.IDEAJRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.SummaryHTMLRenderer;
import net.sourceforge.pmd.renderers.TextColorRenderer;
import net.sourceforge.pmd.renderers.TextPadRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.VBHTMLRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.renderers.XSLTRenderer;
import net.sourceforge.pmd.renderers.YAHTMLRenderer;

import org.junit.Test;

public class CommandLineOptionsTest {

    @Test
    public void testLang() {
	// Testing command line default behavior (no -lang option, means Java 1.5)
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic"});
        assertEquals("LanguageVersion[Java 1.5]", opt.getVersion().toString());
        opt = new CommandLineOptions(new String[]{"file", "format", "ruleset", "-lang","java", "1.3"});
        assertEquals("LanguageVersion[Java 1.3]", opt.getVersion().toString());
        opt = new CommandLineOptions(new String[]{"file", "format", "ruleset", "-lang","java", "1.5"});
        assertEquals("LanguageVersion[Java 1.5]", opt.getVersion().toString());
        opt = new CommandLineOptions(new String[]{"file", "format", "ruleset", "-lang","java", "1.6"});
        assertEquals("LanguageVersion[Java 1.6]", opt.getVersion().toString());
        opt = new CommandLineOptions(new String[]{"-lang","java","1.6","file", "format", "ruleset"});
        assertEquals("LanguageVersion[Java 1.6]", opt.getVersion().toString());
        opt = new CommandLineOptions(new String[]{"file", "format", "ruleset","-lang","java","1.7"});
        assertEquals("LanguageVersion[Java 1.7]", opt.getVersion().toString());
    }

    @Test
    public void testDebug() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic", "-debug"});
        assertTrue(opt.debugEnabled());
        opt = new CommandLineOptions(new String[]{"-debug", "file", "format", "basic"});
        assertTrue(opt.debugEnabled());
    }

    @Test
    public void testSuppressMarker() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic", "-suppressmarker", "FOOBAR"});
        assertEquals("FOOBAR", opt.getSuppressMarker());
        opt = new CommandLineOptions(new String[]{"-suppressmarker", "FOOBAR", "file", "format", "basic"});
        assertEquals("FOOBAR", opt.getSuppressMarker());
    }

    @Test
    public void testShortNames() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic", "-shortnames"});
        assertTrue(opt.shortNamesEnabled());
        opt = new CommandLineOptions(new String[]{"-shortnames", "file", "format", "basic"});
        assertTrue(opt.shortNamesEnabled());
    }

    @Test
    public void testEncoding() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic"});
        assertTrue(opt.getEncoding().equals(new InputStreamReader(System.in).getEncoding()));
        opt = new CommandLineOptions(new String[]{"file", "format", "ruleset", "-encoding", "UTF-8"});
        assertTrue(opt.getEncoding().equals("UTF-8"));
        opt = new CommandLineOptions(new String[]{"-encoding", "UTF-8", "file", "format", "ruleset"});
        assertTrue(opt.getEncoding().equals("UTF-8"));
    }

    @Test
    public void testInputFileName() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic"});
        assertEquals("file", opt.getInputPath());
    }

    @Test
    public void testReportFormat() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic"});
        assertEquals("format", opt.getReportFormat());
    }

    @Test
    public void testRulesets() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic"});
        assertEquals("rulesets/basic.xml", opt.getRulesets());
    }

    @Test
    public void testCommaSeparatedFiles() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file1,file2,file3", "format", "basic"});
        assertTrue(opt.containsCommaSeparatedFileList());
    }

    @Test(expected = RuntimeException.class)
    public void testNotEnoughArgs() {
        new CommandLineOptions(new String[] { "file1", "format" });
    }

    @Test(expected = RuntimeException.class)
    public void testNullArgs() {
        new CommandLineOptions(null);
    }
    
    @Test
    public void testReportFile(){
    	
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic", "-reportfile", "foo.txt"});
        assertSame("foo.txt", opt.getReportFile());
        opt = new CommandLineOptions(new String[]{"-reportfile", "foo.txt", "file", "format", "basic"});
        assertSame("foo.txt", opt.getReportFile());
    }

    @Test
    public void testCpus() {

		CommandLineOptions opt = new CommandLineOptions(new String[] { "file", "format", "basic", "-cpus", "2" });
		assertEquals(2, opt.getCpus());
		opt = new CommandLineOptions(new String[] { "-cpus", "2", "file", "format", "basic" });
		assertEquals(2, opt.getCpus());
	}

    @Test
    public void testRenderer() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "xml", "basic"});
        Renderer renderer = opt.createRenderer();
        assertTrue(renderer instanceof XMLRenderer);
        opt = new CommandLineOptions(new String[]{"file", "html", "basic"});
        renderer = opt.createRenderer();
        assertTrue(renderer instanceof HTMLRenderer);
        opt = new CommandLineOptions(new String[]{"file", "text", "basic"});
        renderer = opt.createRenderer();
        assertTrue(renderer instanceof TextRenderer);
        opt = new CommandLineOptions(new String[]{"file", "emacs", "basic"});
        renderer = opt.createRenderer();
        assertTrue(renderer instanceof EmacsRenderer);
        opt = new CommandLineOptions(new String[]{"file", "csv", "basic"});
        renderer = opt.createRenderer();
        assertTrue(renderer instanceof CSVRenderer);
        opt = new CommandLineOptions(new String[]{"file", "vbhtml", "basic"});
        renderer = opt.createRenderer();
        assertTrue(renderer instanceof VBHTMLRenderer);
        opt = new CommandLineOptions(new String[]{"file", "yahtml", "basic"});
        renderer = opt.createRenderer();
        assertTrue(renderer instanceof YAHTMLRenderer);
        opt = new CommandLineOptions(new String[]{"file", "ideaj", "basic"});
        renderer = opt.createRenderer();
        assertTrue(renderer instanceof IDEAJRenderer);
        opt = new CommandLineOptions(new String[]{"file", "summaryhtml", "basic"});
        renderer = opt.createRenderer();
        assertTrue(renderer instanceof SummaryHTMLRenderer);
        opt = new CommandLineOptions(new String[]{"file", "textcolor", "basic"});
        renderer = opt.createRenderer();
        assertTrue(renderer instanceof TextColorRenderer);
        opt = new CommandLineOptions(new String[]{"file", "textpad", "basic"});
        renderer = opt.createRenderer();
        assertTrue(renderer instanceof TextPadRenderer);
        opt = new CommandLineOptions(new String[]{"file", "xml", "basic"});
        renderer = opt.createRenderer();
        assertTrue(renderer instanceof XMLRenderer);
        opt = new CommandLineOptions(new String[]{"file", "xslt", "basic"});
        renderer = opt.createRenderer();
        assertTrue(renderer instanceof XSLTRenderer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgument1() {
        CommandLineOptions opt = new CommandLineOptions(new String[] { "file", "", "basic" });
        opt.createRenderer();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void illegalArgument2() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "fiddlefaddle", "basic"});
        opt.createRenderer();
    }
    
    @Test
    public void testOptionsFirst(){
		CommandLineOptions opt = new CommandLineOptions(new String[] { "-cpus", "2", "-debug", "file", "format", "basic" });
		assertEquals(2, opt.getCpus());
        assertEquals("file", opt.getInputPath());
        assertEquals("format", opt.getReportFormat());
        assertEquals("rulesets/basic.xml", opt.getRulesets());
        assertTrue(opt.debugEnabled());
    }

    @Test
    public void testAuxilaryClasspath() {
		CommandLineOptions opt = new CommandLineOptions(new String[] { "-auxclasspath", "classpath", "file", "format", "basic" });
		assertEquals("classpath", opt.getAuxClasspath());
	}

    @Test(expected = IllegalArgumentException.class)
    public void testAuxilaryClasspathIllegal() {
		new CommandLineOptions(new String[] { "file", "format", "basic", "-auxclasspath" });
	}

    @Test
    public void testShowSuppressed() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic"});
        assertFalse(opt.isShowSuppressedViolations());
        opt = new CommandLineOptions(new String[]{"-showsuppressed", "file", "format", "basic"});
        assertTrue(opt.isShowSuppressedViolations());
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CommandLineOptionsTest.class);
    }
}
