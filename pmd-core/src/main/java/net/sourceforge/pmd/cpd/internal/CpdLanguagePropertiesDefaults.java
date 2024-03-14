/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.internal;

public final class CpdLanguagePropertiesDefaults {
    private CpdLanguagePropertiesDefaults() {}

    /**
     * Default value for the option "cpdSkipBlocksPattern", which is only supported by the
     * CppLanguageModule.
     *
     * <p>It is already needed in the CLI impl, that's why it needs to be in pmd-core, and not
     * in pmd-cpp.
     */
    public static final String DEFAULT_SKIP_BLOCKS_PATTERN = "#if 0|#endif";
}
