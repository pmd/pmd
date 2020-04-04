/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal.coreimpl;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;
import java.util.Map.Entry;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.table.internal.coreimpl.NameResolver.MultiSymResolver;
import net.sourceforge.pmd.lang.java.symbols.table.internal.coreimpl.NameResolver.SingleSymResolver;
import net.sourceforge.pmd.util.OptionalBool;

public final class CoreResolvers {

    public static <S> NameResolver<S> singleton(String name, S symbol) {
        final List<S> single = singletonList(symbol);
        return new SingleSymResolver<S>() {
            @Override
            public List<S> resolveHere(String s) {
                return name.equals(s) ? single : emptyList();
            }

            @Override
            public @Nullable S resolveFirst(String simpleName) {
                return name.equals(simpleName) ? symbol : null;
            }

            @Override
            public @Nullable OptionalBool knows(String simpleName) {
                return OptionalBool.definitely(name.equals(simpleName));
            }

            @Override
            public String toString() {
                return "Single(" + symbol + ")";
            }
        };
    }

    @NonNull
    public static <S> NameResolver<S> mapResolver(MostlySingularMultimap.Builder<String, S> symbols) {
        Entry<String, S> pair = symbols.singleOrNull();
        if (pair != null) {
            return singleton(pair.getKey(), pair.getValue());
        } else if (symbols.isEmpty()) {
            return emptyResolver();
        }

        MostlySingularMultimap<String, S> map = symbols.build();

        return new MultiSymResolver<S>() {
            @Override
            public List<S> resolveHere(String s) {
                return map.get(s);
            }

            @Override
            public @Nullable OptionalBool knows(String simpleName) {
                return OptionalBool.definitely(map.containsKey(simpleName));
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
        };
    }

    public static <S> EmptyResolver<S> emptyResolver() {
        return EmptyResolver.INSTANCE;
    }

    private static class EmptyResolver<S> implements NameResolver<S> {

        private static final EmptyResolver INSTANCE = new EmptyResolver<>();

        @Nullable
        @Override
        public S resolveFirst(String simpleName) {
            return null;
        }

        @Override
        public List<S> resolveHere(String simpleName) {
            return emptyList();
        }

        @Override
        public @Nullable OptionalBool knows(String simpleName) {
            return OptionalBool.NO;
        }

        @Override
        public String toString() {
            return "Empty";
        }
    }
}
