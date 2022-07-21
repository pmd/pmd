/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.PlainTextLanguage;

/**
 * Compatibility APIs, to be removed before PMD 7 is out.
 */
@Deprecated
public final class CpdCompat {

    private CpdCompat() {
        // utility class
    }

    @Deprecated
    public static LanguageVersion dummyVersion() {
        return PlainTextLanguage.getInstance().getDefaultVersion();
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
