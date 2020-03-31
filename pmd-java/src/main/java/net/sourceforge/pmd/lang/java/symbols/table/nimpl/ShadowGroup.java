/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.nimpl;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;

/**
 * A shadow group indexes symbols by their simple name. It's a subset
 * of a symbol table, caring about symbols of a certain kind.
 *
 * <p>Usage example:
 * <pre>{@code
 *
 *   List<JVariableSymbol> foo = group.resolve("foo");
 *   if (foo.isEmpty()) {
 *      // failed
 *   } else if (foo.size() == 1) {
 *      JVariableSymbol varFoo = foo.get(0); // this symbol
 *
 *      ShadowGroup<JVariableSymbol> next;
 *      while ((next = group.nextShadowGroup("foo")) != null) {
 *          List<JVariableSymbol> fooNext = next.resolve("foo");
 *          // if nonempty, then these names are shadowed in the original group
 *
 *      }
 *      if (next == null) {
 *          // then in the scope of "group", the name 'foo' is not shadowed
 *      } else {
 *          List<JVariableSymbol> fooNext = next.resolve("foo");
 *
 *      }
 *
 *   } else {
 *      // ambiguity between all the members of the list
 *   }
 *
 * }</pre>
 */
public interface ShadowGroup<S extends JElementSymbol> {

    /**
     * Returns the list of symbols accessible by simple name in the scope
     * of this group. No name in this list shadows another. An empty list
     * means no such symbol exist. A list with more than one element means
     * there is ambiguity. For methods, ambiguity may be resolved through
     * overload resolution, for other kinds of symbols, it causes an error.
     *
     * @param name Simple name
     *
     * @return A list of symbols
     */
    @NonNull List<S> resolve(String name);

    /**
     * Returns the next shadow group that may contain a declaration for
     * the given name. If it exists, and that group or the next ones contain
     * a declaration with the given name, then those names are shadowed
     * by this group.
     */
    @Nullable ShadowGroup<S> nextShadowGroup(String name);

}
