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

	private static String languageVersionIn(CommandLineParser parser) {
		return parser.getConfiguration().getLanguageVersionDiscoverer().getDefaultLanguageVersion(Language.JAVA).toString();
	}
	
    @Test
    public void testLang() {
	// Testing command line default behavior (no -lang option, means Java 1.5)
        CommandLineParser parser = new CommandLineParser(new String[]{"file", "format", "basic"});
        assertEquals("LanguageVersion[Java 1.7]", languageVersionIn(parser));
        parser = new CommandLineParser(new String[]{"file", "format", "ruleset", "-version","java", "1.3"});
        assertEquals("LanguageVersion[Java 1.3]", languageVersionIn(parser));
        parser = new CommandLineParser(new String[]{"file", "format", "ruleset", "-version","java", "1.5"});
        assertEquals("LanguageVersion[Java 1.5]", languageVersionIn(parser));
        parser = new CommandLineParser(new String[]{"file", "format", "ruleset", "-version","java", "1.6"});
        assertEquals("LanguageVersion[Java 1.6]", languageVersionIn(parser));
        parser = new CommandLineParser(new String[]{"-version","java","1.6","file", "format", "ruleset"});
        assertEquals("LanguageVersion[Java 1.6]", languageVersionIn(parser));
        parser = new CommandLineParser(new String[]{"file", "format", "ruleset","-version","java","1.7"});
        assertEquals("LanguageVersion[Java 1.7]", languageVersionIn(parser));
    }

    @Test
    public void testDebug() {
        CommandLineParser parser = new CommandLineParser(new String[]{"file", "format", "basic", "-debug"});
        assertTrue(parser.getConfiguration().isDebug());
        parser = new CommandLineParser(new String[]{"-debug", "file", "format", "basic"});
        assertTrue(parser.getConfiguration().isDebug());
    }

    @Test
    public void testSuppressMarker() {
        CommandLineParser parser = new CommandLineParser(new String[]{"file", "format", "basic", "-suppressmarker", "FOOBAR"});
        assertEquals("FOOBAR", parser.getConfiguration().getSuppressMarker());
        parser = new CommandLineParser(new String[]{"-suppressmarker", "FOOBAR", "file", "format", "basic"});
        assertEquals("FOOBAR", parser.getConfiguration().getSuppressMarker());
    }

    @Test
    public void testShortNames() {
        CommandLineParser parser = new CommandLineParser(new String[]{"file", "format", "basic", "-shortnames"});
        assertTrue(parser.getConfiguration().isReportShortNames());
        parser = new CommandLineParser(new String[]{"-shortnames", "file", "format", "basic"});
        assertTrue(parser.getConfiguration().isReportShortNames());
    }

    @Test
    public void testEncoding() {
        CommandLineParser parser = new CommandLineParser(new String[]{"file", "format", "basic"});
        assertEquals(Charset.forName(parser.getConfiguration().getSourceEncoding()), Charset.forName(new InputStreamReader(System.in).getEncoding()));
        parser = new CommandLineParser(new String[]{"file", "format", "ruleset", "-encoding", "UTF-8"});
        assertEquals(parser.getConfiguration().getSourceEncoding(), "UTF-8");
        parser = new CommandLineParser(new String[]{"-encoding", "UTF-8", "file", "format", "ruleset"});
        assertEquals(parser.getConfiguration().getSourceEncoding(), "UTF-8");
    }

    @Test
    public void testInputFileName() {
        CommandLineParser parser = new CommandLineParser(new String[]{"file", "format", "basic"});
        assertEquals("file", parser.getConfiguration().getInputPaths());
    }

    @Test
    public void testReportFormat() {
        CommandLineParser parser = new CommandLineParser(new String[]{"file", "format", "basic"});
        assertEquals("format", parser.getConfiguration().getReportFormat());
    }

    @Test
    public void testRulesets() {
        CommandLineParser parser = new CommandLineParser(new String[]{"file", "format", "java-basic"});
        assertEquals("rulesets/java/basic.xml", parser.getConfiguration().getRuleSets());
    }

    @Test
    public void testCommaSeparatedFiles() {
        CommandLineParser parser = new CommandLineParser(new String[]{"file1,file2,file3", "format", "basic"});
        assertEquals("file1,file2,file3", parser.getConfiguration().getInputPaths());
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
    	
        CommandLineParser parser = new CommandLineParser(new String[]{"file", "format", "basic", "-reportfile", "foo.txt"});
        assertSame("foo.txt", parser.getConfiguration().getReportFile());
        parser = new CommandLineParser(new String[]{"-reportfile", "foo.txt", "file", "format", "basic"});
        assertSame("foo.txt", parser.getConfiguration().getReportFile());
    }

    @Test
    public void testThreads() {

		CommandLineParser parser = new CommandLineParser(new String[] { "file", "format", "basic", "-threads", "2" });
		assertEquals(2, parser.getConfiguration().getThreads());
		parser = new CommandLineParser(new String[] { "-threads", "2", "file", "format", "basic" });
		assertEquals(2, parser.getConfiguration().getThreads());
	}

    @Test
    public void testRenderer() {
        CommandLineParser parser = new CommandLineParser(new String[]{"file", "xml", "basic"});
        Renderer renderer = parser.getConfiguration().createRenderer();
        assertTrue(renderer instanceof XMLRenderer);
        parser = new CommandLineParser(new String[]{"file", "html", "basic"});
        renderer = parser.getConfiguration().createRenderer();
        assertTrue(renderer instanceof HTMLRenderer);
        parser = new CommandLineParser(new String[]{"file", "text", "basic"});
        renderer = parser.getConfiguration().createRenderer();
        assertTrue(renderer instanceof TextRenderer);
        parser = new CommandLineParser(new String[]{"file", "emacs", "basic"});
        renderer = parser.getConfiguration().createRenderer();
        assertTrue(renderer instanceof EmacsRenderer);
        parser = new CommandLineParser(new String[]{"file", "csv", "basic"});
        renderer = parser.getConfiguration().createRenderer();
        assertTrue(renderer instanceof CSVRenderer);
        parser = new CommandLineParser(new String[]{"file", "vbhtml", "basic"});
        renderer = parser.getConfiguration().createRenderer();
        assertTrue(renderer instanceof VBHTMLRenderer);
        parser = new CommandLineParser(new String[]{"file", "yahtml", "basic"});
        renderer = parser.getConfiguration().createRenderer();
        assertTrue(renderer instanceof YAHTMLRenderer);
        parser = new CommandLineParser(new String[]{"file", "ideaj", "basic"});
        renderer = parser.getConfiguration().createRenderer();
        assertTrue(renderer instanceof IDEAJRenderer);
        parser = new CommandLineParser(new String[]{"file", "summaryhtml", "basic"});
        renderer = parser.getConfiguration().createRenderer();
        assertTrue(renderer instanceof SummaryHTMLRenderer);
        parser = new CommandLineParser(new String[]{"file", "textcolor", "basic"});
        renderer = parser.getConfiguration().createRenderer();
        assertTrue(renderer instanceof TextColorRenderer);
        parser = new CommandLineParser(new String[]{"file", "textpad", "basic"});
        renderer = parser.getConfiguration().createRenderer();
        assertTrue(renderer instanceof TextPadRenderer);
        parser = new CommandLineParser(new String[]{"file", "xml", "basic"});
        renderer = parser.getConfiguration().createRenderer();
        assertTrue(renderer instanceof XMLRenderer);
        parser = new CommandLineParser(new String[]{"file", "xslt", "basic"});
        renderer = parser.getConfiguration().createRenderer();
        assertTrue(renderer instanceof XSLTRenderer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgument1() {
        CommandLineParser parser = new CommandLineParser(new String[] { "file", "", "basic" });
        parser.getConfiguration().createRenderer();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void illegalArgument2() {
        CommandLineParser parser = new CommandLineParser(new String[]{"file", "fiddlefaddle", "basic"});
        parser.getConfiguration().createRenderer();
    }
    
    @Test
    public void testOptionsFirst(){
		CommandLineParser parser = new CommandLineParser(new String[] { "-threads", "2", "-debug", "file", "format", "java-basic" });
		assertEquals(2, parser.getConfiguration().getThreads());
        assertEquals("file", parser.getConfiguration().getInputPaths());
        assertEquals("format", parser.getConfiguration().getReportFormat());
        assertEquals("rulesets/java/basic.xml", parser.getConfiguration().getRuleSets());
        assertTrue(parser.getConfiguration().isDebug());
    }

    @Test
    public void testAuxilaryClasspath() {
	CommandLineParser parser = new CommandLineParser(new String[] { "-auxclasspath", "/classpath", "file", "format",
		"basic" });
	ClassLoader classLoader = parser.getConfiguration().getClassLoader();
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
        CommandLineParser parser = new CommandLineParser(new String[]{"file", "format", "basic"});
        assertFalse(parser.getConfiguration().isShowSuppressedViolations());
        parser = new CommandLineParser(new String[]{"-showsuppressed", "file", "format", "basic"});
        assertTrue(parser.getConfiguration().isShowSuppressedViolations());
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CommandLineParserTest.class);
    }
}
