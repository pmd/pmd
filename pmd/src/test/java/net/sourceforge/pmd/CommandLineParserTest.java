/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.pmd.CommandLineParser;
import net.sourceforge.pmd.Configuration;
import net.sourceforge.pmd.lang.Language;
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
import net.sourceforge.pmd.util.ClasspathClassLoader;

import org.junit.Test;

public class CommandLineParserTest {

    @Test
    public void testLang() {
	// Testing command line default behavior (no -lang option, means Java 1.5)
        CommandLineParser opt = new CommandLineParser(new String[]{"file", "format", "basic"});
        assertEquals("LanguageVersion[Java 1.7]", opt.getConfiguration().getLanguageVersionDiscoverer().getDefaultLanguageVersion(Language.JAVA).toString());
        opt = new CommandLineParser(new String[]{"file", "format", "ruleset", "-version","java", "1.3"});
        assertEquals("LanguageVersion[Java 1.3]", opt.getConfiguration().getLanguageVersionDiscoverer().getDefaultLanguageVersion(Language.JAVA).toString());
        opt = new CommandLineParser(new String[]{"file", "format", "ruleset", "-version","java", "1.5"});
        assertEquals("LanguageVersion[Java 1.5]", opt.getConfiguration().getLanguageVersionDiscoverer().getDefaultLanguageVersion(Language.JAVA).toString());
        opt = new CommandLineParser(new String[]{"file", "format", "ruleset", "-version","java", "1.6"});
        assertEquals("LanguageVersion[Java 1.6]", opt.getConfiguration().getLanguageVersionDiscoverer().getDefaultLanguageVersion(Language.JAVA).toString());
        opt = new CommandLineParser(new String[]{"-version","java","1.6","file", "format", "ruleset"});
        assertEquals("LanguageVersion[Java 1.6]", opt.getConfiguration().getLanguageVersionDiscoverer().getDefaultLanguageVersion(Language.JAVA).toString());
        opt = new CommandLineParser(new String[]{"file", "format", "ruleset","-version","java","1.7"});
        assertEquals("LanguageVersion[Java 1.7]", opt.getConfiguration().getLanguageVersionDiscoverer().getDefaultLanguageVersion(Language.JAVA).toString());
    }

    @Test
    public void testDebug() {
        CommandLineParser opt = new CommandLineParser(new String[]{"file", "format", "basic", "-debug"});
        assertTrue(opt.getConfiguration().isDebug());
        opt = new CommandLineParser(new String[]{"-debug", "file", "format", "basic"});
        assertTrue(opt.getConfiguration().isDebug());
    }

    @Test
    public void testSuppressMarker() {
        CommandLineParser opt = new CommandLineParser(new String[]{"file", "format", "basic", "-suppressmarker", "FOOBAR"});
        assertEquals("FOOBAR", opt.getConfiguration().getSuppressMarker());
        opt = new CommandLineParser(new String[]{"-suppressmarker", "FOOBAR", "file", "format", "basic"});
        assertEquals("FOOBAR", opt.getConfiguration().getSuppressMarker());
    }

    @Test
    public void testShortNames() {
        CommandLineParser opt = new CommandLineParser(new String[]{"file", "format", "basic", "-shortnames"});
        assertTrue(opt.getConfiguration().isReportShortNames());
        opt = new CommandLineParser(new String[]{"-shortnames", "file", "format", "basic"});
        assertTrue(opt.getConfiguration().isReportShortNames());
    }

    @Test
    public void testEncoding() {
        CommandLineParser opt = new CommandLineParser(new String[]{"file", "format", "basic"});
        assertEquals(Charset.forName(opt.getConfiguration().getSourceEncoding()), Charset.forName(new InputStreamReader(System.in).getEncoding()));
        opt = new CommandLineParser(new String[]{"file", "format", "ruleset", "-encoding", "UTF-8"});
        assertEquals(opt.getConfiguration().getSourceEncoding(), "UTF-8");
        opt = new CommandLineParser(new String[]{"-encoding", "UTF-8", "file", "format", "ruleset"});
        assertEquals(opt.getConfiguration().getSourceEncoding(), "UTF-8");
    }

    @Test
    public void testInputFileName() {
        CommandLineParser opt = new CommandLineParser(new String[]{"file", "format", "basic"});
        assertEquals("file", opt.getConfiguration().getInputPaths());
    }

    @Test
    public void testReportFormat() {
        CommandLineParser opt = new CommandLineParser(new String[]{"file", "format", "basic"});
        assertEquals("format", opt.getConfiguration().getReportFormat());
    }

    @Test
    public void testRulesets() {
        CommandLineParser opt = new CommandLineParser(new String[]{"file", "format", "java-basic"});
        assertEquals("rulesets/java/basic.xml", opt.getConfiguration().getRuleSets());
    }

    @Test
    public void testCommaSeparatedFiles() {
        CommandLineParser opt = new CommandLineParser(new String[]{"file1,file2,file3", "format", "basic"});
        assertEquals("file1,file2,file3", opt.getConfiguration().getInputPaths());
    }

    @Test(expected = RuntimeException.class)
    public void testNotEnoughArgs() {
        new CommandLineParser(new String[] { "file1", "format" });
    }

    @Test(expected = RuntimeException.class)
    public void testNullArgs() {
        new CommandLineParser(null);
    }
    
    @Test
    public void testReportFile(){
    	
        CommandLineParser opt = new CommandLineParser(new String[]{"file", "format", "basic", "-reportfile", "foo.txt"});
        assertSame("foo.txt", opt.getConfiguration().getReportFile());
        opt = new CommandLineParser(new String[]{"-reportfile", "foo.txt", "file", "format", "basic"});
        assertSame("foo.txt", opt.getConfiguration().getReportFile());
    }

    @Test
    public void testThreads() {

		CommandLineParser opt = new CommandLineParser(new String[] { "file", "format", "basic", "-threads", "2" });
		assertEquals(2, opt.getConfiguration().getThreads());
		opt = new CommandLineParser(new String[] { "-threads", "2", "file", "format", "basic" });
		assertEquals(2, opt.getConfiguration().getThreads());
	}

    @Test
    public void testRenderer() {
        CommandLineParser opt = new CommandLineParser(new String[]{"file", "xml", "basic"});
        Renderer renderer = opt.getConfiguration().createRenderer();
        assertTrue(renderer instanceof XMLRenderer);
        opt = new CommandLineParser(new String[]{"file", "html", "basic"});
        renderer = opt.getConfiguration().createRenderer();
        assertTrue(renderer instanceof HTMLRenderer);
        opt = new CommandLineParser(new String[]{"file", "text", "basic"});
        renderer = opt.getConfiguration().createRenderer();
        assertTrue(renderer instanceof TextRenderer);
        opt = new CommandLineParser(new String[]{"file", "emacs", "basic"});
        renderer = opt.getConfiguration().createRenderer();
        assertTrue(renderer instanceof EmacsRenderer);
        opt = new CommandLineParser(new String[]{"file", "csv", "basic"});
        renderer = opt.getConfiguration().createRenderer();
        assertTrue(renderer instanceof CSVRenderer);
        opt = new CommandLineParser(new String[]{"file", "vbhtml", "basic"});
        renderer = opt.getConfiguration().createRenderer();
        assertTrue(renderer instanceof VBHTMLRenderer);
        opt = new CommandLineParser(new String[]{"file", "yahtml", "basic"});
        renderer = opt.getConfiguration().createRenderer();
        assertTrue(renderer instanceof YAHTMLRenderer);
        opt = new CommandLineParser(new String[]{"file", "ideaj", "basic"});
        renderer = opt.getConfiguration().createRenderer();
        assertTrue(renderer instanceof IDEAJRenderer);
        opt = new CommandLineParser(new String[]{"file", "summaryhtml", "basic"});
        renderer = opt.getConfiguration().createRenderer();
        assertTrue(renderer instanceof SummaryHTMLRenderer);
        opt = new CommandLineParser(new String[]{"file", "textcolor", "basic"});
        renderer = opt.getConfiguration().createRenderer();
        assertTrue(renderer instanceof TextColorRenderer);
        opt = new CommandLineParser(new String[]{"file", "textpad", "basic"});
        renderer = opt.getConfiguration().createRenderer();
        assertTrue(renderer instanceof TextPadRenderer);
        opt = new CommandLineParser(new String[]{"file", "xml", "basic"});
        renderer = opt.getConfiguration().createRenderer();
        assertTrue(renderer instanceof XMLRenderer);
        opt = new CommandLineParser(new String[]{"file", "xslt", "basic"});
        renderer = opt.getConfiguration().createRenderer();
        assertTrue(renderer instanceof XSLTRenderer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgument1() {
        CommandLineParser opt = new CommandLineParser(new String[] { "file", "", "basic" });
        opt.getConfiguration().createRenderer();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void illegalArgument2() {
        CommandLineParser opt = new CommandLineParser(new String[]{"file", "fiddlefaddle", "basic"});
        opt.getConfiguration().createRenderer();
    }
    
    @Test
    public void testOptionsFirst(){
		CommandLineParser opt = new CommandLineParser(new String[] { "-threads", "2", "-debug", "file", "format", "java-basic" });
		assertEquals(2, opt.getConfiguration().getThreads());
        assertEquals("file", opt.getConfiguration().getInputPaths());
        assertEquals("format", opt.getConfiguration().getReportFormat());
        assertEquals("rulesets/java/basic.xml", opt.getConfiguration().getRuleSets());
        assertTrue(opt.getConfiguration().isDebug());
    }

    @Test
    public void testAuxilaryClasspath() {
	CommandLineParser opt = new CommandLineParser(new String[] { "-auxclasspath", "/classpath", "file", "format",
		"basic" });
	ClassLoader classLoader = opt.getConfiguration().getClassLoader();
	assertTrue("classloader is ClasspathClassLoader", classLoader instanceof ClasspathClassLoader);
	URL[] urls = ((ClasspathClassLoader) classLoader).getURLs();
	assertEquals("urls length", 1, urls.length);
	assertTrue("url[0]", urls[0].toString().endsWith("/classpath"));
	assertEquals("parent classLoader", Configuration.class.getClassLoader(), classLoader.getParent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAuxilaryClasspathIllegal() {
		new CommandLineParser(new String[] { "file", "format", "basic", "-auxclasspath" });
	}

    @Test
    public void testShowSuppressed() {
        CommandLineParser opt = new CommandLineParser(new String[]{"file", "format", "basic"});
        assertFalse(opt.getConfiguration().isShowSuppressedViolations());
        opt = new CommandLineParser(new String[]{"-showsuppressed", "file", "format", "basic"});
        assertTrue(opt.getConfiguration().isShowSuppressedViolations());
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CommandLineParserTest.class);
    }
}
