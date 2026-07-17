/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.internal;

import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.internal.util.AuxClasspathLoader;

/**
 * @since 7.27.0
 */
public class RuleTstLauncherSession implements LauncherSessionListener {
    private static final Logger LOG = LoggerFactory.getLogger(RuleTstLauncherSession.class);

    @Override
    public void launcherSessionOpened(LauncherSession session) {
        LOG.info("Enabling AuxClasspathLoader reuse");
        AuxClasspathLoader.enableReuse(1);
    }

    @Override
    public void launcherSessionClosed(LauncherSession session) {
        LOG.info("Closing cache AuxClasspathLoaders");
        AuxClasspathLoader.disableReuse();
    }
}
