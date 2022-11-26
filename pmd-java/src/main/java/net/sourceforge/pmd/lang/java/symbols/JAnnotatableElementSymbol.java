/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import java.util.List;

import net.sourceforge.pmd.lang.java.types.JAnnotation;

/**
 * Represents declarations having annotations common to {@link JFieldSymbol},
 * {@link JClassSymbol}, {@link JMethodSymbol}, {@link JConstructorSymbol}, and {@link JTypeParameterSymbol}.
 *
 * @since 7.0.0
 */
public interface JAnnotatableElementSymbol extends JElementSymbol {

    List<JAnnotation> getDeclaredAnnotations();
}
