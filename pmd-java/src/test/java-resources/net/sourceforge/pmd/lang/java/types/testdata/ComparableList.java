/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.testdata;

import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

// test class for intersections
public class ComparableList<T> extends ArrayList<T> implements Comparable<List<T>> {

    @Override
    public int compareTo(@NonNull List<T> o) {
        return 0;
    }

}
