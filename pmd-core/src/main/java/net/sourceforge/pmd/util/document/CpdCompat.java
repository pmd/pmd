/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;

/**
 * Compatibility APIs, to be removed before PMD 7 is out.
 */
@Deprecated
public final class CpdCompat {

    private CpdCompat() {
        // utility class
    }


    /** The language version must be non-null. */
    @Deprecated
    private static final Language DUMMY_LANG = new BaseLanguageModule("dummy", "dummy", "dummy", "dummy") {
        {
            addDefaultVersion("", () -> task -> {
                throw new UnsupportedOperationException();
            });
        }

    };

    @Deprecated
    public static LanguageVersion dummyVersion() {
        return DUMMY_LANG.getDefaultVersion();
    }

    /**
     * Bridges {@link SourceCode} with {@link TextFile}. This allows
     * javacc tokenizers to work on text documents.
     *
     * @deprecated This is only a transitional API for the PMD 7 branch
     */
    @Deprecated
    public static TextFile cpdCompat(SourceCode sourceCode) {
        return TextFile.forCharSeq(
            sourceCode.getCodeBuffer(),
            sourceCode.getFileName(),
            dummyVersion()
        );
    }
}
