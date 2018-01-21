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
    
    public static String getNextMajorRelease() {
        if (UNKNOWN_VERSION.equals(VERSION)) {
            return UNKNOWN_VERSION;
        }
        
        final int major = Integer.parseInt(VERSION.split("\\.")[0]);
        return (major + 1) + ".0.0";
    }
}
