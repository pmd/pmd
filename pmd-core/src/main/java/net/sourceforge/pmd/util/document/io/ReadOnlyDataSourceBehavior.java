/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * Adapter for a {@link DataSource}.
 */
class ReadOnlyDataSourceBehavior implements TextFileBehavior {

    private final DataSource dataSource;
    private final Charset encoding;

    public ReadOnlyDataSourceBehavior(DataSource source, Charset encoding) {
        this.encoding = encoding;
        AssertionUtil.requireParamNotNull("source text", source);
        AssertionUtil.requireParamNotNull("charset", encoding);

        this.dataSource = source;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public void writeContents(CharSequence charSequence) {
        throw new UnsupportedOperationException("Readonly source");
    }

    @Override
    public CharSequence readContents() throws IOException {
        try (InputStream is = dataSource.getInputStream();
             Reader reader = new InputStreamReader(is, encoding)) {

            return IOUtils.toString(reader);
        }
    }

    @Override
    public long fetchStamp() throws IOException {
        return hashCode();
    }

    @Override
    public String toString() {
        return "ReadOnly[" + dataSource + "]";
    }

}
