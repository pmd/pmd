/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.nimpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;

public final class LazyShadowGroup<S extends JElementSymbol> implements ShadowGroup<S> {

    private final Map<String, List<S>> cache;

    private final ShadowGroup<S> next;
    private final Function<String, @Nullable List<S>> resolver;

    LazyShadowGroup(@Nullable ShadowGroup<S> next,
                    Map<String, List<S>> known,
                    Function<String, @Nullable List<S>> resolver) {
        this.next = next;
        this.resolver = resolver;
        this.cache = known;
    }

    @Override
    public @NonNull List<S> resolve(String name) {
        List<S> result = cache.get(name);
        if (result != null) {
            return result;
        }
        result = resolver.apply(name);
        if (result == null && next != null) {
            result = next.resolve(name);
        } else if (result == null) {
            result = Collections.emptyList();
        }
        cache.put(name, result);
        return result;
    }

    @Override
    public @Nullable ShadowGroup<S> nextShadowGroup(String name) {
        return next;
    }
}
