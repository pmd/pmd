/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A symbol that declares a type. These include
 * <ul>
 *     <li>{@linkplain JClassSymbol class, interface, array & primitive symbols}</li>
 *     <li>{@linkplain JTypeParameterSymbol type parameters symbols}</li>
 * </ul>
 *
 * <p>Note: type symbols are not <i>types</i>, they <i>declare</i> types.
 *
 * @since 7.0.0
 */
public interface JTypeDeclSymbol extends JElementSymbol, JAccessibleElementSymbol {




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

}
