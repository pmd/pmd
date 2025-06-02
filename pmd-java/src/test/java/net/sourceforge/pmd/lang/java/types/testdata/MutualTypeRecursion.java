/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.testdata;

public class MutualTypeRecursion
    <T extends MutualTypeRecursion<T, S>,
        S extends MutualTypeRecursion<S, T>> {


}
