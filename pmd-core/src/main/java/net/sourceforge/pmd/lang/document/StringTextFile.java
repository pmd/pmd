/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.io.StringReader;
import java.util.Objects;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.ReaderDataSource;
import net.sourceforge.pmd.util.datasource.internal.LanguageAwareDataSource;

/**
 * Read-only view on a string.
 *
 * @author Clément Fournier
 */
@Experimental
class StringTextFile implements TextFile {

    private final String content;
    private final String pathId;
    private final String displayName;
    private final LanguageVersion languageVersion;

    StringTextFile(String content,
                   String pathId,
                   String displayName,
                   LanguageVersion languageVersion) {
        AssertionUtil.requireParamNotNull("source text", content);
        AssertionUtil.requireParamNotNull("file name", displayName);
        AssertionUtil.requireParamNotNull("file ID", pathId);
        AssertionUtil.requireParamNotNull("language version", languageVersion);

        this.languageVersion = languageVersion;
        this.content = content;
        this.pathId = pathId;
        this.displayName = displayName;
    }


    @Override
    public LanguageVersion getLanguageVersion() {
        return languageVersion;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getPathId() {
        return pathId;
    }

    @Override
    public String readContents() {
        return content;
    }

    @Override
    public DataSource toDataSourceCompat() {
        return new LanguageAwareDataSource(new ReaderDataSource(
            new StringReader(content),
            pathId),
            languageVersion
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StringTextFile that = (StringTextFile) o;
        return Objects.equals(pathId, that.pathId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathId);
    }

    @Override
    public String toString() {
        return getPathId();
    }
}
