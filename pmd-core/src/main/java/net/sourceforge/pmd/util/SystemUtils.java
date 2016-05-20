/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SystemUtils {

    /**
     * Do we have proper permissions to use multithreading?
     */
    public static final boolean MT_SUPPORTED;

    private SystemUtils() {
	// this is a utility class and cannot be instantiated
    }

    static {
	boolean error = false;
	try {
	    /*
	     * ant task ran from Eclipse with jdk 1.5.0 raises an AccessControlException
	     * when shutdown is called. Standalone pmd or ant from command line are fine.
	     *
	     * With jdk 1.6.0, ant task from Eclipse also works.
	     *
	     * Bugs related to this hack:
	     * http://sourceforge.net/p/pmd/bugs/1025/
	     * http://sourceforge.net/p/pmd/bugs/670/
	     */
	    ExecutorService executor = Executors.newFixedThreadPool(1);
	    executor.shutdown();
	} catch (RuntimeException e) {
	    error = true;
	    System.err.println("Disabling multithreading - consider to upgrade to java 1.6");
	    System.err.println("See also: http://sourceforge.net/p/pmd/bugs/670/");
	}
	MT_SUPPORTED = !error;
    }
}
