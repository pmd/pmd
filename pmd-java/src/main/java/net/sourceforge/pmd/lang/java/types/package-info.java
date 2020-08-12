/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * Support for compile-time type resolution on the AST.
 */
package net.sourceforge.pmd.lang.java.types;
/*
TODO:  inference of `throws` clause

import java.io.IOException;

@FunctionalInterface
interface ThrowingRunnable<E extends Throwable> {

    void run() throws E;
}

class Scratch {

    static <E extends Throwable> void wrap(ThrowingRunnable<? extends E> runnable) throws E {
        runnable.run();
    }

    static void runThrowing() throws IOException {
        throw new IOException();
    }

    {
        try {
            wrap(Scratch::runThrowing);
        } catch (IOException e) {

        }
    }

}
 */


/*
TODO: qualified super ctor call


class Outer {
    class Inner<T> {
        public Inner(T value) { }
    }
}


class Scratch extends Outer.Inner<String> {

    public Scratch(Outer o) {
        o.super("value");
    }
}

 */


/* TODO: inference with unchecked conversion

    public static <T> T min(Collection<? extends T> coll, Comparator<? super T> comp) {
        if (comp==null)
            return (T) min((Collection) coll);
        return null;
    }

    public static <T extends Object & Comparable<? super T>> T min(Collection<? extends T> coll) {
        return null;
    }


    [WARNING] CTDecl resolution failed. Summary of failures:
    STRICT:
        Incompatible bounds: ξ349 = java.lang.Object and ξ349 <: java.lang.Comparable<? super ξ349>		min(java.util.Collection<? extends T>) -> T

    LOOSE:
        Incompatible bounds: ο349 = java.lang.Object and ο349 <: java.lang.Comparable<? super ο349>		min(java.util.Collection<? extends T>) -> T


 */

/* TODO: an array initializer is an assignment context
    -> see PolyResolution to fix it

    class Scratch {

        final Runnable r[] = {
            () -> { } // is a Runnable
        }

    }
 */

/* TODO: bug with visibility and override

At:   /home/clifrr/Bureau/jdk13-src/java.base/java/util/Date.java:1174 :30..1174:71
Expr: ((ZoneInfo)tz).getOffsets(fastTime, null)
[WARNING] Ambiguity error: both methods are maximally specific
    sun.util.calendar.ZoneInfo.getOffsets(long, int[]) -> int
    java.util.TimeZone.getOffsets(long, int[]) -> int


package java.util;

public class TimeZone implements Serializable, Cloneable {


    // package visibility
    int getOffsets(long date, int[] offsets) {
        int rawoffset = getRawOffset();
        int dstoffset = 0;
        if (inDaylightTime(new Date(date))) {
            dstoffset = getDSTSavings();
        }
        if (offsets != null) {
            offsets[0] = rawoffset;
            offsets[1] = dstoffset;
        }
        return rawoffset + dstoffset;
    }

}


package sun.util.calendar;

public class ZoneInfo extends TimeZone {
    // doesn't override, because the super method is not accessible
    public int getOffsets(long utc, int[] offsets) {
        return 0;
    }
}

package java.util;

public class Date {
    {
        TimeZone tz = TimeZone.getDefaultRef();
        if (tz instanceof ZoneInfo) {
            // the one of ZoneInfo is selected
            zoneOffset = ((ZoneInfo)tz).getOffsets(fastTime, null);
        }
    }
}

 */

/* TODO: bug with glb in ReductionStep#UPPER

In: /home/clifrr/Bureau/jdk13-src/java.base/java/util/stream/Collectors.java:1070:16
java.lang.IllegalArgumentException: Bad intersection, unrelated class types capture#510 of ? super T and capture#420 of ? super T in [java.lang.Object, capture#420 of ? super T, capture#510 of ? super T]
	at net.sourceforge.pmd.lang.java.types.TypeSystem.glb(TypeSystem.java:663)
	at net.sourceforge.pmd.lang.java.types.internal.infer.ReductionStep$3.solve(ReductionStep.java:51)
	at net.sourceforge.pmd.lang.java.types.internal.infer.InferenceContext.solveBasic(InferenceContext.java:389)
	at net.sourceforge.pmd.lang.java.types.internal.infer.InferenceContext.solve(InferenceContext.java:367)
	at net.sourceforge.pmd.lang.java.types.internal.infer.InferenceContext.solve(InferenceContext.java:347)
	at net.sourceforge.pmd.lang.java.types.internal.infer.Infer.instantiateImpl(Infer.java:479)


	public static <T, K, A, D>
    Collector<T, ?, Map<K, D>> groupingBy(Function<? super T, ? extends K> classifier,
                                          Collector<? super T, A, D> downstream) {
        return Collectors.groupingBy(classifier, HashMap::new, downstream);
    }


 */
