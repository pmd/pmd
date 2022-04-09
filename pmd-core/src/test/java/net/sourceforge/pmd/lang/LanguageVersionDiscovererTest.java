/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

/**
 * @author Clément Fournier
 */
public class LanguageVersionDiscovererTest {

    public static LanguageVersionDiscoverer createForcedDiscoverer(LanguageVersion forcedVersion) {
        return new LanguageVersionDiscoverer(LanguageRegistry.PMD, forcedVersion);
    }

}
