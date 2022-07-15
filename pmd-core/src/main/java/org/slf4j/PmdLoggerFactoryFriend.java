/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package org.slf4j;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * This class is for internal use only.
 * <p>
 * It is needed to reinitialize the underlying logging in case the configuration is changed.
 * </p>
 * @deprecated internal, do not use
 */
@Deprecated
@InternalApi
public final class PmdLoggerFactoryFriend {
    private PmdLoggerFactoryFriend() {
        // helper
    }

    public static void reset() {
        LoggerFactory.reset();
    }
}
