package net.sourceforge.pmd.util;

import java.util.Iterator;

public class Applier {

    public static void apply(UnaryFunction f, Iterator i) {
        while (i.hasNext()) {
            f.applyTo(i.next());
        }
    }
}
