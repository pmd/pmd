/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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

    static class UrlLoader extends Loader {

        private final @NonNull URL url;

        UrlLoader(@NonNull URL url) {
            assert url != null : "Null url";
            this.url = url;
        }


        @Override
        @Nullable
        InputStream getInputStream() throws IOException {
            return url.openStream();
        }

        @Override
        public String toString() {
            return "(URL loader)";
        }
    }


}
