/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.asm;


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
    }

    static class NoUrlLoader extends Loader {

        private final AsmSymbolResolver resolver;
        private final String internalName;

        NoUrlLoader(AsmSymbolResolver resolver, String internalName) {
            this.resolver = resolver;
            this.internalName = internalName;
        }

        @Override
        @Nullable
        InputStream getInputStream() throws IOException {
            URL url = resolver.getUrlOfInternalName(internalName);
            return url != null ? url.openStream() : null;
        }
    }


}
