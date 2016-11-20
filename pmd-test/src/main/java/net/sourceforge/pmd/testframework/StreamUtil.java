/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.testframework;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * @deprecated Just use apache {@link IOUtils}
 */
@Deprecated
public final class StreamUtil {

    private StreamUtil() {
        // utility class
    }

    /**
     * @deprecated use {@link IOUtils#toString(InputStream)} instead
     */
    @Deprecated
    public static String toString(InputStream stream) {
        try {
            return IOUtils.toString(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
