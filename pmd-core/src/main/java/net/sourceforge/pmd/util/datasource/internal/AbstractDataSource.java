/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.datasource.internal;

import java.io.IOException;

import net.sourceforge.pmd.util.datasource.DataSource;

public abstract class AbstractDataSource implements DataSource {

    @Override
    public void close() throws IOException {
        // empty default implementation
    }
}
