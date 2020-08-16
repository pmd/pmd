/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.types.JTypeMirror;

/**
 * A symbol that declares a type. These include
 * <ul>
 *     <li>{@linkplain JClassSymbol class, interface, array & primitive symbols}</li>
 *     <li>{@linkplain JTypeParameterSymbol type parameters symbols}</li>
 * </ul>
 *
 * <p>Note: type symbols are not <i>types</i>, they <i>declare</i> types.
 * See {@link JTypeMirror#getSymbol()} for more details.
 *
 * @since 7.0.0
 */
public interface JTypeDeclSymbol extends JElementSymbol, JAccessibleElementSymbol {

    /**
     * Returns true if this class is a symbolic reference to an unresolved
     * class. In that case no information about the symbol are known except
     * its name, and the accessors of this class return default values.
     *
     * <p>This kind of symbol is introduced to allow for some best-effort
     * symbolic resolution. For example in:
     * <pre>{@code
     * import org.Bar;
     *
     * Bar foo = new Bar();
     * }</pre>
     * and supposing {@code org.Bar} is not on the classpath. The type
     * of {@code foo} is {@code Bar}, which we can qualify to {@code org.Bar} thanks to the
     * import (via symbol tables, and without even querying the classpath).
     * Even though we don't know what members {@code org.Bar} has, a
     * test for {@code typeIs("org.Bar")} would succeed with certainty,
     * so it makes sense to preserve the name information and not give
     * up too early.
     *
     * <p>Note that unresolved types are always created from an unresolved
     * <i>canonical name</i>, so they can't be just <i>any</i> type. For example,
     * they can't be array types, nor local classes (since those are lexically
     * scoped, so always resolvable), nor anonymous classes (can only be referenced
     * on their declaration site), type variables, etc.
     */
    @Override
    default boolean isUnresolved() {
        return false;
    }


    /**
     * Returns the reflected class this node represents, if it's on the auxclasspath.
     * There's no guarantee that this is even exists (this symbol may be notional).
     *
     * <p>This is provided to optimize some stuff, but ideally the symbol
     * API should reflect everything there is to know about classes,
     * and this method shouldn't be used.
     */
    @Nullable
    Class<?> getJvmRepr();


    /**
     * Returns the simple name of this class, as specified by
     * {@link Class#getSimpleName()}.
     */
    @Override
    @NonNull
    String getSimpleName();


    /**
     * This returns true if this is an interface. Annotation types are
     * also interface types.
     */
    default boolean isInterface() {
        return false;
    }
}
