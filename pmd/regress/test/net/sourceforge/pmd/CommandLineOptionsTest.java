package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.CommandLineOptions;
import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.EmacsRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.IDEAJRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;

public class CommandLineOptionsTest extends TestCase {

    public void testDebug() {
        CommandLineOptions opt = new CommandLineOptions(new String[] {"file", "format", "ruleset", "-debug"});
        assertTrue(opt.debugEnabled());
    }

    public void testTargetJDKVersion() {
        CommandLineOptions opt = new CommandLineOptions(new String[] {"file", "format", "ruleset"});
        assertFalse(opt.jdk13());
        opt = new CommandLineOptions(new String[] {"file", "format", "ruleset", "-jdk13"});
        assertTrue(opt.jdk13());
    }

    public void testShortNames() {
        CommandLineOptions opt = new CommandLineOptions(new String[] {"file", "format", "ruleset", "-shortnames"});
        assertTrue(opt.shortNamesEnabled());
    }

    public void testInputFileName() {
        CommandLineOptions opt = new CommandLineOptions(new String[] {"file", "format", "ruleset"});
        assertEquals("file", opt.getInputFileName());
    }

    public void testReportFormat() {
        CommandLineOptions opt = new CommandLineOptions(new String[] {"file", "format", "ruleset"});
        assertEquals("format", opt.getReportFormat());
    }

    public void testRulesets() {
        CommandLineOptions opt = new CommandLineOptions(new String[] {"file", "format", "ruleset"});
        assertEquals("ruleset", opt.getRulesets());
    }

    public void testCommaSeparatedFiles() {
        CommandLineOptions opt = new CommandLineOptions(new String[] {"file1,file2,file3", "format", "ruleset"});
        assertTrue(opt.containsCommaSeparatedFileList());
    }

    public void testNotEnoughArgs() {
        try {
            new CommandLineOptions(new String[] {"file1", "format"});
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

    public void testRenderer() {
        CommandLineOptions opt = new CommandLineOptions(new String[] {"file", "xml", "ruleset"});
        assertTrue(opt.createRenderer() instanceof XMLRenderer);
        opt = new CommandLineOptions(new String[] {"file", "html", "ruleset"});
        assertTrue(opt.createRenderer() instanceof HTMLRenderer);
        opt = new CommandLineOptions(new String[] {"file", "text", "ruleset"});
        assertTrue(opt.createRenderer() instanceof TextRenderer);
        opt = new CommandLineOptions(new String[] {"file", "emacs", "ruleset"});
        assertTrue(opt.createRenderer() instanceof EmacsRenderer);
        opt = new CommandLineOptions(new String[] {"file", "csv", "ruleset"});
        assertTrue(opt.createRenderer() instanceof CSVRenderer);
        opt = new CommandLineOptions(new String[] {"file", "ideaj", "ruleset"});
        assertTrue(opt.createRenderer() instanceof IDEAJRenderer);

        try {
            opt = new CommandLineOptions(new String[] {"file", "fiddlefaddle", "ruleset"});
            opt.createRenderer();
        } catch (IllegalArgumentException iae) {
            // cool
        }

        try {
            opt = new CommandLineOptions(new String[] {"file", "", "ruleset"});
            opt.createRenderer();
        } catch (IllegalArgumentException iae) {
            // cool
        }
    }
}
