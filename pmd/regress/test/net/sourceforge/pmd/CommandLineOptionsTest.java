/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
 (DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
 by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.CommandLineOptions;
import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.EmacsRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.IDEAJRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.VBHTMLRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;

import java.io.InputStreamReader;

public class CommandLineOptionsTest extends TestCase {

    public void testTargetJDKVersion() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic"});
        assertEquals("1.4", opt.getTargetJDK());
        opt = new CommandLineOptions(new String[]{"file", "format", "ruleset", "-targetjdk", "1.3"});
        assertEquals("1.3", opt.getTargetJDK());
        opt = new CommandLineOptions(new String[]{"file", "format", "ruleset", "-targetjdk", "1.5"});
        assertEquals("1.5", opt.getTargetJDK());
        opt = new CommandLineOptions(new String[]{"file", "format", "ruleset", "-targetjdk", "1.6"});
        assertEquals("1.6", opt.getTargetJDK());
        opt = new CommandLineOptions(new String[]{"-targetjdk", "1.6", "file", "format", "ruleset"});
        assertEquals("1.6", opt.getTargetJDK());
    }

    public void testDebug() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic", "-debug"});
        assertTrue(opt.debugEnabled());
        opt = new CommandLineOptions(new String[]{"-debug", "file", "format", "basic"});
        assertTrue(opt.debugEnabled());
    }

    public void testExcludeMarker() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic", "-excludemarker", "FOOBAR"});
        assertEquals("FOOBAR", opt.getExcludeMarker());
        opt = new CommandLineOptions(new String[]{"-excludemarker", "FOOBAR", "file", "format", "basic"});
        assertEquals("FOOBAR", opt.getExcludeMarker());
    }

    public void testShortNames() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic", "-shortnames"});
        assertTrue(opt.shortNamesEnabled());
        opt = new CommandLineOptions(new String[]{"-shortnames", "file", "format", "basic"});
        assertTrue(opt.shortNamesEnabled());
    }

    public void testEncoding() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic"});
        assertTrue(opt.getEncoding().equals((new InputStreamReader(System.in)).getEncoding()));
        opt = new CommandLineOptions(new String[]{"file", "format", "ruleset", "-encoding", "UTF-8"});
        assertTrue(opt.getEncoding().equals("UTF-8"));
        opt = new CommandLineOptions(new String[]{"-encoding", "UTF-8", "file", "format", "ruleset"});
        assertTrue(opt.getEncoding().equals("UTF-8"));
    }

    public void testInputFileName() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic"});
        assertEquals("file", opt.getInputPath());
    }

    public void testReportFormat() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic"});
        assertEquals("format", opt.getReportFormat());
    }

    public void testRulesets() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic"});
        assertEquals("rulesets/basic.xml", opt.getRulesets());
    }

    public void testCommaSeparatedFiles() {
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file1,file2,file3", "format", "basic"});
        assertTrue(opt.containsCommaSeparatedFileList());
    }

    public void testNotEnoughArgs() {
        try {
            new CommandLineOptions(new String[]{"file1", "format"});
            fail("Should have thrown an exception when only array contained < 3 args");
        } catch (RuntimeException re) {
            // cool
        }
    }

    public void testNullArgs() {
        try {
            new CommandLineOptions(null);
            fail("Should have thrown an exception when null passed to constructor");
        } catch (RuntimeException re) {
            // cool
        }
    }
    
    public void testReportFile(){
    	
        CommandLineOptions opt = new CommandLineOptions(new String[]{"file", "format", "basic", "-reportfile", "foo.txt"});
        assertSame("foo.txt", opt.getReportFile());
        opt = new CommandLineOptions(new String[]{"-reportfile", "foo.txt", "file", "format", "basic"});
        assertSame("foo.txt", opt.getReportFile());
    }

    public void testCpus() {

		CommandLineOptions opt = new CommandLineOptions(new String[] { "file", "format", "basic", "-cpus", "2" });
		assertEquals(2, opt.getCpus());
		opt = new CommandLineOptions(new String[] { "-cpus", "2", "file", "format", "basic" });
		assertEquals(2, opt.getCpus());
	}

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

        try {
            opt = new CommandLineOptions(new String[]{"file", "fiddlefaddle", "basic"});
            opt.createRenderer();
        } catch (IllegalArgumentException iae) {
            // cool
        }

        try {
            opt = new CommandLineOptions(new String[]{"file", "", "basic"});
            opt.createRenderer();
        } catch (IllegalArgumentException iae) {
            // cool
        }
    }
    
    public void testOptionsFirst(){
		CommandLineOptions opt = new CommandLineOptions(new String[] { "-cpus", "2", "-debug", "file", "format", "basic" });
		assertEquals(2, opt.getCpus());
        assertEquals("file", opt.getInputPath());
        assertEquals("format", opt.getReportFormat());
        assertEquals("rulesets/basic.xml", opt.getRulesets());
        assertTrue(opt.debugEnabled());
    }
}
