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
    private final PathId pathId;
    private final LanguageVersion languageVersion;

    StringTextFile(CharSequence source, PathId pathId, LanguageVersion languageVersion) {
        AssertionUtil.requireParamNotNull("source text", source);
        AssertionUtil.requireParamNotNull("file name", pathId);
        AssertionUtil.requireParamNotNull("language version", languageVersion);

        this.languageVersion = languageVersion;
        this.content = TextFileContent.fromCharSeq(source);
        this.pathId = pathId;
    }

    @Override
    public @NonNull LanguageVersion getLanguageVersion() {
        return languageVersion;
    }

    @Override
    public @NonNull String getDisplayName() {
        return pathId.toUriString();
    }

    @Override
    public PathId getPathId() {
        return pathId;
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
