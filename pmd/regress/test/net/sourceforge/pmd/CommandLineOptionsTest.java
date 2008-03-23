/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.CommandLineOptions;
import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.EmacsRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.IDEAJRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.VBHTMLRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;

import org.junit.Test;

import java.io.InputStreamReader;

import junit.framework.JUnit4TestAdapter;

public class CommandLineOptionsTest {

    @Test
    public void testTargetJDKVersion() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic"});
        assertEquals("1.5", opt.getTargetJDK());
        opt = new CommandLineOptions(new String[]{"file", "format", "ruleset", "-targetjdk", "1.3"});
        assertEquals("1.3", opt.getTargetJDK());
        opt = new CommandLineOptions(new String[]{"file", "format", "ruleset", "-targetjdk", "1.5"});
        assertEquals("1.5", opt.getTargetJDK());
        opt = new CommandLineOptions(new String[]{"file", "format", "ruleset", "-targetjdk", "1.6"});
        assertEquals("1.6", opt.getTargetJDK());
        opt = new CommandLineOptions(new String[]{"-targetjdk", "1.6", "file", "format", "ruleset"});
        assertEquals("1.6", opt.getTargetJDK());
    }

    @Test
    public void testDebug() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic", "-debug"});
        assertTrue(opt.debugEnabled());
        opt = new CommandLineOptions(new String[]{"-debug", "file", "format", "basic"});
        assertTrue(opt.debugEnabled());
    }

    @Test
    public void testExcludeMarker() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic", "-excludemarker", "FOOBAR"});
        assertEquals("FOOBAR", opt.getExcludeMarker());
        opt = new CommandLineOptions(new String[]{"-excludemarker", "FOOBAR", "file", "format", "basic"});
        assertEquals("FOOBAR", opt.getExcludeMarker());
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
        assertTrue(opt.getEncoding().equals((new InputStreamReader(System.in)).getEncoding()));
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
        assertTrue(opt.createRenderer() instanceof XMLRenderer);
        opt = new CommandLineOptions(new String[]{"file", "html", "basic"});
        assertTrue(opt.createRenderer() instanceof HTMLRenderer);
        opt = new CommandLineOptions(new String[]{"file", "text", "basic"});
        assertTrue(opt.createRenderer() instanceof TextRenderer);
        opt = new CommandLineOptions(new String[]{"file", "emacs", "basic"});
        assertTrue(opt.createRenderer() instanceof EmacsRenderer);
        opt = new CommandLineOptions(new String[]{"file", "csv", "basic"});
        assertTrue(opt.createRenderer() instanceof CSVRenderer);
        opt = new CommandLineOptions(new String[]{"file", "vbhtml", "basic"});
        assertTrue(opt.createRenderer() instanceof VBHTMLRenderer);
        opt = new CommandLineOptions(new String[]{"file", "ideaj", "basic"});
        assertTrue(opt.createRenderer() instanceof IDEAJRenderer);
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
		CommandLineOptions opt = new CommandLineOptions(new String[] { "file", "format", "basic", "-auxclasspath" });
	}

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CommandLineOptionsTest.class);
    }
}
