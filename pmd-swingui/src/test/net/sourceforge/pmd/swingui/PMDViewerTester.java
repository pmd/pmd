package net.sourceforge.pmd.swingui;

import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 * This is a tester for the PMD Viewer.
 *
 * @author Brant Gurganus
 * @version 0.1
 * @since 0.1
 */
public class PMDViewerTester extends TestCase {
    /**
     * Creates the tester.
     */
    public PMDViewerTester() {
        super("PMD Viewer Test");
        disableLogging();
    }
    
    /**
     * Creates the tester with the given name.
     */
    public PMDViewerTester(String name) {
        super(name);
        disableLogging();
    }
    
    /**
     * Disables logging so that confusion does not happen.
     */
    private void disableLogging() {
        final LogManager manager = LogManager.getLogManager();
        final Enumeration loggers = manager.getLoggerNames();
        while (loggers.hasMoreElements()) {
            final String logName = (String) loggers.nextElement();
            Logger log = manager.getLogger(logName);
            log.setLevel(Level.OFF);
        }
    }
    
    /**
     * Ensures that {@link net.sourceforge.pmd.swingui.PMDViewer#translateKey}
     * works properly.
     */
    public void testTranslateKey() {
        assertTrue(PMDViewer.translateKey("A") == KeyEvent.VK_A);
        assertTrue(PMDViewer.translateKey(null) == -1);
    }
}