/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone.closeresource;

import java.io.Closeable;
import java.io.IOException;

/**
 * Helper class for #2757
 */
public class FakeContext implements Closeable, AutoCloseable {

    public <T> T getBean(Class<T> klass) {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
