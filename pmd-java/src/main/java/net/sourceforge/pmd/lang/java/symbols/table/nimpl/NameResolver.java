/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.nimpl;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.OptionalBool;

public interface NameResolver<S> {


    List<S> resolveHere(String simpleName);


    @Nullable S resolveFirst(String simpleName);


    default @Nullable OptionalBool knows(String simpleName) {
        return OptionalBool.UNKNOWN;
    }

    /** Please implement toString to ease debugging. */
    @Override
    String toString();


    abstract class SingleSymResolver<S> implements NameResolver<S> {

        @Override
        public List<S> resolveHere(String simpleName) {
            return CollectionUtil.listOfNotNull(resolveFirst(simpleName));
        }
    }

    abstract class MultiSymResolver<S> implements NameResolver<S> {
        @Override
        public @Nullable S resolveFirst(String simpleName) {
            List<S> result = resolveHere(simpleName);
            return result.isEmpty() ? null : result.get(0);
        }
    }
}
