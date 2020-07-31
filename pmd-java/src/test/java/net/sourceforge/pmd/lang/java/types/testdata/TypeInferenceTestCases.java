/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.testdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class TypeInferenceTestCases {

    private TypeInferenceTestCases() {
    }

    public static <T, L extends List<T>> L appendL(List<? extends T> in, L top) {
        top.addAll(in);
        // this is just to add imports
        new ArrayList<>(Arrays.asList(3, 3));
        new LinkedList<>();
        return top;
    }

    public static <T> List<T> makeThree(Supplier<T> factory) {
        return Collections.emptyList();
    }

    public static <T, K> T wild(K t) {
        return null;
    }

    public static <U> List<U> m(List<U> src) {
        return null;
    }


    public static <T> T id(T t) {
        return t;
    }

}
