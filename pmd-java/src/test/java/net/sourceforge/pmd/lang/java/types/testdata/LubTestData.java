/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.testdata;

public class LubTestData {


    public interface I1 {
    }

    public interface I2<T> extends I1 {
    }

    public interface I3 {
    }

    public interface I4 {
    }


    public static class Sub1 implements Comparable<Sub1>, I1, I3 {

        @Override
        public int compareTo(LubTestData.Sub1 o) {
            return 0;
        }
    }

    public static class Sub2 implements Comparable<Sub2>, I2<I3> {

        @Override
        public int compareTo(LubTestData.Sub2 o) {
            return 0;
        }
    }


    public static class GenericSuper<T> implements I3 {
    }

    public static class GenericSub<T> extends GenericSuper<T> implements I2<I1> {
    }

    public static class GenericSub2<T> extends GenericSuper<T> implements I2<I3>, I4 {
    }


}
