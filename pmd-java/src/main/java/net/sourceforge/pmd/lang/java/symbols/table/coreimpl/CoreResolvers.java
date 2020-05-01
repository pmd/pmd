/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.coreimpl;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.NameResolver.SingleNameResolver;
import net.sourceforge.pmd.util.OptionalBool;

public final class CoreResolvers {

    private CoreResolvers() {
        // util class
    }

    public static <S> NameResolver<S> singleton(String name, S symbol) {
        final List<S> single = singletonList(symbol);
        return new SingleNameResolver<S>() {
            @Override
            public @NonNull List<S> resolveHere(String s) {
                return name.equals(s) ? single : emptyList();
            }

            @Override
            public @Nullable S resolveFirst(String simpleName) {
                return name.equals(simpleName) ? symbol : null;
            }

            @Override
            public @NonNull OptionalBool knows(String simpleName) {
                return OptionalBool.definitely(name.equals(simpleName));
            }

            @Override
            public String toString() {
                return "Single(" + symbol + ")";
            }
        };
    }

    static <S> NameResolver<S> multimapResolver(MostlySingularMultimap<String, S> symbols) {
        return new MultimapResolver<>(symbols);
    }

    public static <S> SingleNameResolver<S> singularMapResolver(Map<String, S> singular) {
        return new SingularMapResolver<>(singular);
    }

    private static class SingularMapResolver<S> implements SingleNameResolver<S> {

        private final Map<String, S> map;

        private SingularMapResolver(Map<String, S> map) {
            this.map = map;
        }

        @Nullable
        @Override
        public S resolveFirst(String simpleName) {
            return map.get(simpleName);
        }

        @Override
        public boolean isDefinitelyEmpty() {
            return map.isEmpty();
        }

        @Override
        public @NonNull OptionalBool knows(String simpleName) {
            return OptionalBool.definitely(map.containsKey(simpleName));
        }

        @Override
        public String toString() {
            return "SingularMap(" + map.values() + ")";
        }
    }

    private static class MultimapResolver<S> implements NameResolver<S> {

        private final MostlySingularMultimap<String, S> map;

        MultimapResolver(MostlySingularMultimap<String, S> map) {
            this.map = map;
        }

        @Override
        public @NonNull List<S> resolveHere(String s) {
            return map.get(s);
        }

        @Override
        public @NonNull OptionalBool knows(String simpleName) {
            return OptionalBool.definitely(map.containsKey(simpleName));
        }

        @Override
        public boolean isDefinitelyEmpty() {
            return map.isEmpty();
        }

        @Override
        public String toString() {
            return "Map(" + mapToString() + ")";
        }

        private String mapToString() {
            if (map.isEmpty()) {
                return "{}";
            }
            StringBuilder sb = new StringBuilder("{");
            map.processValuesOneByOne((k, v) -> sb.append(v).append(", "));
            return sb.substring(0, sb.length() - 2) + "}";
        }
    }

    public static <S> EmptyResolver<S> emptyResolver() {
        return EmptyResolver.INSTANCE;
    }

    private static class EmptyResolver<S> implements SingleNameResolver<S> {

        private static final EmptyResolver INSTANCE = new EmptyResolver<>();

        @Nullable
        @Override
        public S resolveFirst(String simpleName) {
            return null;
        }

        @Override
        public @NonNull List<S> resolveHere(String simpleName) {
            return emptyList();
        }

        @Override
        public @NonNull OptionalBool knows(String simpleName) {
            return OptionalBool.NO;
        }

        @Override
        public boolean isDefinitelyEmpty() {
            return true;
        }

        @Override
        public String toString() {
            return "Empty";
        }
    }
}
