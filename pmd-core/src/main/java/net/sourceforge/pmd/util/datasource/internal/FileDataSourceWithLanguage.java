/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.datasource.internal;

import java.io.File;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.datasource.FileDataSource;

@InternalApi
public class FileDataSourceWithLanguage extends FileDataSource implements LanguageAwareDataSource {
    private final LanguageVersion languageVersion;

    public FileDataSourceWithLanguage(File file, LanguageVersion languageVersion) {
        super(file);
        this.languageVersion = languageVersion;
    }

    @Override
    public LanguageVersion getLanguageVersion() {
        return languageVersion;
    }
}
