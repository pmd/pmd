/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * Support for compile-time type resolution on the AST.
 */
package net.sourceforge.pmd.lang.java.types;
/*
TODO:  inference of `throws` clause
    -> pretty hard, see throws constraint formulas in JLS 18

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
            wrap(Scratch::runThrowing); // throws IOException
        } catch (IOException e) {

        }
    }

}
 */


/*
TODO: qualified super ctor call
   -> Update CtorInvocMirror.ExplicitCtorInvocMirror#getNewType


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
        Incompatible bounds: ξ349 = java.lang.Object and ξ349 <: java.lang.Comparable<? super ξ349> min(java.util.Collection<? extends T>) -> T

    LOOSE:
        Incompatible bounds: ο349 = java.lang.Object and ο349 <: java.lang.Comparable<? super ο349> min(java.util.Collection<? extends T>) -> T


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

/* TODO possibly, the type node for a diamond should have the parameterized
    type, for now it's a raw type (and untested)
    See TypesFromAst

import java.util.ArrayList;

class O {
    {
        List<String> l = new ArrayList<>();
        //                   -----------
        //                   this node has a raw type, maybe it should have type ArrayList<String>

        // Note that the whole expression has type ArrayList<String> after inference
    }
}

 */

/* TODO test bridge method execution filtering
    In AsmLoaderTest


 */

/* TODO switch on enum also depends on the type of the tested expr to
    resolve the variable name

class Scratch {

    enum SomeEnum {
        A,
        B
    }

    public static void main(String[] args) {
        SomeEnum e = SomeEnum.A;

        switch (e) {
        // neither A nor B are in scope
        case A:
            return;
        case B:
            return;
        }

        A.foo(); // this is an ambiguous name


    }
}


 */

/* TODO the condition of a ternary does not flow context

    public static double copySign(double magnitude, double sign) {
        return Math.copySign(magnitude, (Double.isNaN(sign)?1.0d:sign));
    }

 */

/* TODO the condition of a ternary is not an an

    public static double copySign(double magnitude, double sign) {
        return Math.copySign(magnitude, (Double.isNaN(sign)?1.0d:sign));
    }

 */
