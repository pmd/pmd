/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.datasource.internal;

import java.io.Reader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.datasource.ReaderDataSource;

@InternalApi
public class ReaderDataSourceWithLanguage extends ReaderDataSource implements LanguageAwareDataSource {
    private final LanguageVersion languageVersion;

    public ReaderDataSourceWithLanguage(Reader reader, String dataSourceName, LanguageVersion languageVersion) {
        super(reader, dataSourceName);
        this.languageVersion = languageVersion;
    }

    @Override
    public LanguageVersion getLanguageVersion() {
        return languageVersion;
    }
}
