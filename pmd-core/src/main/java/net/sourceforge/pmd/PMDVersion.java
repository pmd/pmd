/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores the current PMD version and provides utility methods around it.
 */
public final class PMDVersion {

    private static final Logger LOG = LoggerFactory.getLogger(PMDVersion.class);

    /**
     * Constant that contains always the current version of PMD.
     */
    public static final String VERSION;

    private static final String RELEASE_TIMESTAMP;
    private static final String GIT_COMMIT_ID;
    private static final String GIT_COMMIT_TIME;

    private static final String UNKNOWN = "unknown";

    /*
     * Determines the version from maven's generated pom.properties file.
     */
    static {
        String pmdVersion = UNKNOWN;
        String releaseTimestamp = UNKNOWN;
        String gitCommitId = UNKNOWN;
        String gitCommitTime = UNKNOWN;
        try (InputStream stream = PMDVersion.class.getResourceAsStream("pmd-core-version.properties")) {
            if (stream != null) {
                final Properties properties = new Properties();
                properties.load(stream);
                pmdVersion = properties.getProperty("version");
                releaseTimestamp = properties.getProperty("releaseTimestamp");

                gitCommitId = properties.getProperty("gitCommitId");
                gitCommitTime = properties.getProperty("gitCommitTime");
            }
        } catch (final IOException e) {
            LOG.debug("Couldn't determine version of PMD", e);
        }

        VERSION = pmdVersion;
        RELEASE_TIMESTAMP = releaseTimestamp;
        GIT_COMMIT_ID = gitCommitId;
        GIT_COMMIT_TIME = gitCommitTime;
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
            return UNKNOWN;
        }

        final int major = Integer.parseInt(VERSION.split("\\.")[0]);
        return (major + 1) + ".0.0";
    }

    /**
     * Checks if the current version is unknown.
     * @return True if an unknown version, false otherwise
     */
    @SuppressWarnings("PMD.LiteralsFirstInComparisons")
    public static boolean isUnknown() {
        return UNKNOWN.equals(VERSION);
    }

    /**
     * Checks if the current version is a snapshot.
     * @return True if a snapshot release, false otherwise
     */
    public static boolean isSnapshot() {
        return VERSION.endsWith("-SNAPSHOT");
    }

    public static String getFullVersionName() {
        if (isSnapshot()) {
            return "PMD " + VERSION + " (" + GIT_COMMIT_ID + ", " + GIT_COMMIT_TIME + ")";
        }
        return "PMD " + VERSION + " (" + GIT_COMMIT_ID + ", " + RELEASE_TIMESTAMP + ")";
    }
}
