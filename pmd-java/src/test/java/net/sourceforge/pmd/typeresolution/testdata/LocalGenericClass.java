/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

public class LocalGenericClass {

    public static <T> void localClassInGeneric() {
        class MyLocalClass implements MyCombiner<T, Optional<T>, MyLocalClass> {
            private T state;

            @Override
            public void accept(T t) { }

            @Override
            public Optional<T> get() {
                return Optional.empty();
            }

            @Override
            public void combine(MyLocalClass other) {
                accept(other.state);
            }
        }

        new MyLocalClass();
    }

    private interface MyCombiner<R, S, T> extends MyConsumer<R>, MySupplier<S> {
        void combine(T t);
    }

    private interface MyConsumer<R> {
        void accept(R r);
    }

    private interface MySupplier<S> {
        S get();
    }

    private static class Optional<T> {
        public static <T> Optional<T> empty() {
            return new Optional<T>();
        }
    }
}
