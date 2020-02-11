/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.testdata;

import static java.util.EnumSet.noneOf;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class Overloads {


    public static void ambig(String s1, String s2, CharSequence... args) {

    }

    public static void ambig(String s1, CharSequence... args) {

    }

    @SafeVarargs
    public static <T> List<T> genericOf(T... args) {
        return Collections.emptyList();
    }

    public static <T> List<T> genericOf(T arg) {
        return Collections.emptyList();
    }


    // these are disambiguated with phases

    @SafeVarargs
    public static <E extends Enum<E>> EnumSet<E> of(E first, E... rest) {
        EnumSet<E> result = noneOf(first.getDeclaringClass());
        result.add(first);
        Collections.addAll(result, rest);
        return result;
    }

    public static <E extends Enum<E>> EnumSet<E> of(E e1, E e2) {
        EnumSet<E> result = noneOf(e1.getDeclaringClass());
        result.add(e1);
        result.add(e2);
        return result;
    }

    public static <E extends Enum<E>> EnumSet<E> of(E e) {
        EnumSet<E> result = noneOf(e.getDeclaringClass());
        result.add(e);
        return result;
    }
}
