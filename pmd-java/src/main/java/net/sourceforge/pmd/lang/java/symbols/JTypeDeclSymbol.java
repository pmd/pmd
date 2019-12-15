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
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JTypeDeclSymbol extends JElementSymbol, JAccessibleElementSymbol {


    /**
     * Returns the reflected class this node represents, if it's on the auxclasspath.
     * Ideally this shouldn't be used, and the symbol API should reflect everything
     * there is to know about a class.
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
