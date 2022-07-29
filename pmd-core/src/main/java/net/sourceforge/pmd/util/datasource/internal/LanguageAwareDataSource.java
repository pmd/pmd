/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.datasource.internal;

import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.datasource.DataSource;

@InternalApi
public class LanguageAwareDataSource implements DataSource {
    private final DataSource base; // delegate DataSource methods to this
    private final LanguageVersion version;

    public LanguageAwareDataSource(DataSource base, LanguageVersion version) {
        this.base = base;
        this.version = version;
    }

    public LanguageVersion getLanguageVersion() {
        return version;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return base.getInputStream();
    }

    @Override
    public String getNiceFileName(boolean shortNames, String inputFileName) {
        return base.getNiceFileName(shortNames, inputFileName);
    }

    @Override
    public void close() throws IOException {
        base.close();
    }
}
