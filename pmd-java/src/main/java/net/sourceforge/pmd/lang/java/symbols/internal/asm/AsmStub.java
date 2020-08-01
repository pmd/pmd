/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

interface AsmStub {


    AsmSymbolResolver getResolver();


    default SignatureParser sigParser() {
        return getResolver().getSigParser();
    }

    @NonNull
    static <T> List<T> toList(@Nullable T[] arr) {
        return arr == null ? Collections.emptyList() : Arrays.asList(arr);
    }

}
