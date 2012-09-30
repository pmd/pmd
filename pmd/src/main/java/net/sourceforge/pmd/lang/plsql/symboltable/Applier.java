/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.Iterator;

import net.sourceforge.pmd.util.UnaryFunction;

public class Applier {

    public static <E> void apply(UnaryFunction<E> f, Iterator<? extends E> i) {
        while (i.hasNext()) {
            f.applyTo(i.next());
        }
    }
}
