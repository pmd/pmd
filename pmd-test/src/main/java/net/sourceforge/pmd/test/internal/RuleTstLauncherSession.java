/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.internal;

import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.util.AuxClasspathLoader;

/**
 * @since 7.27.0
 */
// Note: The class must be public to be instantiable via java.util.ServiceLoader
public class RuleTstLauncherSession implements LauncherSessionListener {
    private static final Logger LOG = LoggerFactory.getLogger(RuleTstLauncherSession.class);

    @Override
    public void launcherSessionOpened(LauncherSession session) {
        LOG.debug("Enabling AuxClasspathLoader reuse");
        // Note: in tests in pmd-java, we might create multiple different instances.
        // Especially, we might create the instances only once and store them in static fields.
        // If the cache is too small, cache eviction takes place and closes the AuxClasspathLoader,
        // that might still be used via static fields.
        // E.g. net.sourceforge.pmd.lang.java.JavaParsingHelper creates one,
        // and net.sourceforge.pmd.lang.java.symbols.ClassLoadingChildFirstTest creates another one.
        AuxClasspathLoader.enableReuse(2);
    }

    @Override
    public void launcherSessionClosed(LauncherSession session) {
        LOG.debug("Closing cache AuxClasspathLoaders");
        AuxClasspathLoader.disableReuse();
    }
}
