/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stores the current PMD version and provides utility methods around it.
 */
public final class PMDVersion {

    private static final Logger LOG = Logger.getLogger(PMDVersion.class.getName());

    /**
     * Constant that contains always the current version of PMD.
     */
    public static final String VERSION;

    private static final String UNKNOWN_VERSION = "unknown";

    /**
     * Determines the version from maven's generated pom.properties file.
     */
    static {
        String pmdVersion = UNKNOWN_VERSION;
        try (InputStream stream = PMDVersion.class.getResourceAsStream("/META-INF/maven/net.sourceforge.pmd/pmd-core/pom.properties")) {
            if (stream != null) {
                final Properties properties = new Properties();
                properties.load(stream);
                pmdVersion = properties.getProperty("version");
            }
        } catch (final IOException e) {
            LOG.log(Level.FINE, "Couldn't determine version of PMD", e);
        }

        VERSION = pmdVersion;
    }

    private PMDVersion() {
        throw new AssertionError("Can't instantiate utility classes");
    }

    /**
     * Retrieves the next major release to be expected.
     * Useful when logging deprecation messages to indicate when support will be removed.
     *
     * @return The next major release to be expected.
     */
    public static String getNextMajorRelease() {
        if (isUnknown()) {
            return UNKNOWN_VERSION;
        }

        final int major = Integer.parseInt(VERSION.split("\\.")[0]);
        return (major + 1) + ".0.0";
    }

    /**
     * Checks if the current version is unknown.
     * @return True if an unknown version, false otherwise
     */
    public static boolean isUnknown() {
        return UNKNOWN_VERSION.equals(VERSION);
    }

    /**
     * Checks if the current version is a snapshot.
     * @return True if a snapshot release, false otherwise
     */
    public static boolean isSnapshot() {
        return VERSION.endsWith("-SNAPSHOT");
    }
}
