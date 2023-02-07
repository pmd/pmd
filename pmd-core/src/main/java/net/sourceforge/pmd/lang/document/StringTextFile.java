/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Read-only view on a string.
 */
class StringTextFile implements TextFile {

    private final TextFileContent content;
    private final String name;
    private final LanguageVersion languageVersion;

    StringTextFile(CharSequence source, String name, LanguageVersion languageVersion) {
        AssertionUtil.requireParamNotNull("source text", source);
        AssertionUtil.requireParamNotNull("file name", name);
        AssertionUtil.requireParamNotNull("language version", languageVersion);

        this.languageVersion = languageVersion;
        this.content = TextFileContent.fromCharSeq(source);
        this.name = name;
    }

    @Override
    public @NonNull LanguageVersion getLanguageVersion() {
        return languageVersion;
    }

    @Override
    public @NonNull String getDisplayName() {
        return name;
    }

    @Override
    public String getPathId() {
        return name;
    }

    @Override
    public TextFileContent readContents() {
        return content;
    }

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public String toString() {
        return "ReadOnlyString[" + StringUtil.elide(content.getNormalizedText().toString(), 40, "...") + "]";
    }

}
