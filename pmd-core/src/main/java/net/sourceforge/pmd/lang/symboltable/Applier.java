/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.symboltable;

import java.util.Iterator;

import net.sourceforge.pmd.util.SearchFunction;

public final class Applier {

    private Applier() {
        // utility class
    }

    public static <E> void apply(SearchFunction<E> f, Iterator<? extends E> i) {
        while (i.hasNext() && f.applyTo(i.next())) {
            // Nothing to do
        }
    }
}
