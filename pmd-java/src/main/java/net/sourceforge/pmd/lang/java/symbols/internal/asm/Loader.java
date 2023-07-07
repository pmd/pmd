/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;


import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

abstract class Loader implements Closeable {


    /**
     * Return an input stream that must be closed by the caller.
     * This method can be called exactly once. Return null if
     * the loader is failed, or if the stream has already been returned.
     */
    abstract @Nullable InputStream getInputStream();


    static class FailedLoader extends Loader {

        static final FailedLoader INSTANCE = new FailedLoader();


        @Override
        @Nullable InputStream getInputStream() {
            return null;
        }


        @Override
        public String toString() {
            return "(failed loader)";
        }


        @Override
        public void close() throws IOException {
            // do nothing
        }
    }

    static class StreamLoader extends Loader {

        private final @NonNull String name;
        private InputStream stream;

        StreamLoader(@NonNull String name, @NonNull InputStream stream) {
            this.name = name;
            this.stream = stream;
        }


        @Override
        @NonNull InputStream getInputStream() {
            InputStream result = stream;
            this.stream = null; // null out to avoid double close
            return result;
        }


        @Override
        public void close() throws IOException {
            if (stream != null) {
                stream.close();
            }
        }


        @Override
        public String toString() {
            return "(StreamLoader for " + name + ")";
        }
    }
}
