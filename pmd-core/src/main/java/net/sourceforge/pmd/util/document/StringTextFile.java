/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.StringReader;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.ReaderDataSource;

/**
 * Collects files to analyse before a PMD run. This API allows opening
 * zip files and makes sure they will be closed at the end of a run.
 *
 * @author Clément Fournier
 */
@Experimental
public final class StringTextFile implements TextFile {

    private final String contents;
    private final String pathId;
    private final String displayName;
    private final LanguageVersion languageVersion;

    public StringTextFile(String contents,
                          String pathId,
                          String displayName,
                          LanguageVersion languageVersion) {
        this.contents = contents;
        this.pathId = pathId;
        this.displayName = displayName;
        this.languageVersion = languageVersion;
    }

    @Override
    public String getPathId() {
        return pathId;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String readContents() {
        return contents;
    }

    @Override
    public LanguageVersion getLanguageVersion() {
        return languageVersion;
    }

    @Override
    public DataSource toDataSourceCompat() {
        return new ReaderDataSource(
            new StringReader(contents),
            pathId
        );
    }
}
