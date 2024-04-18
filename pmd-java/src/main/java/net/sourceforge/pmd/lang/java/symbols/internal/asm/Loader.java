/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;


import java.io.IOException;
import java.io.InputStream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

abstract class Loader {


    @Nullable
    abstract InputStream getInputStream() throws IOException;


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

    }

    static class StreamLoader extends Loader {
        private final @NonNull String name;
        private final @NonNull InputStream stream;

        StreamLoader(@NonNull String name, @NonNull InputStream stream) {
            this.name = name;
            this.stream = stream;
        }

        @Override
        @NonNull InputStream getInputStream() {
            return stream;
        }

        @Override
        public String toString() {
            return "StreamLoader(for " + name + ")";
        }
    }
}
