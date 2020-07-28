/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.symboltable;

import java.util.Iterator;
import java.util.function.Predicate;

public final class Applier {

    private Applier() {
        // utility class
    }

    public static <E> void apply(Predicate<E> f, Iterator<? extends E> i) {
        while (i.hasNext() && f.test(i.next())) {
            // Nothing to do
        }
    }
}
