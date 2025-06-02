/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.testdata;

import java.util.function.Supplier;

public final class GenericMethodReference {
    {
        // the constructor is not generic, so <Integer> is unused, but that should just be ignored
        Supplier<GenericMethodReference> supplier1 = GenericMethodReference::<Integer>new;
        Supplier<GenericMethodReference> supplier2 = GenericMethodReference::<Integer, String>new;

        // the create1 method is not generic - any provided types should be ignored
        Supplier<GenericMethodReference> supplier3 = GenericMethodReference::<Integer>create1;
        Supplier<GenericMethodReference> supplier4 = GenericMethodReference::<Integer, String>create1;

        // the create2 method is generic, but takes only one parameter
        Supplier<GenericMethodReference> supplier5 = GenericMethodReference::<Integer>create2;

        // providing too many parameter here is a compile error
        //Supplier<GenericMethodReference> supplier6 = GenericMethodReference::<Integer, String>create2;
    }

    public static GenericMethodReference create1() {
        return null;
    }

    public static <T> GenericMethodReference create2() {
        return null;
    }

    private GenericMethodReference() {
    }
}
