/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.JavaNode;


/**
 * Constrains the return type of getDeclaration. This is used to avoid having
 * a type parameter directly on {@link JElementSymbol}, which would get
 * in the way most of the time. Not visible outside this package, it's just
 * a code organisation device.
 *
 * @since 7.0.0
 */
interface BoundToNode<N extends JavaNode> extends JElementSymbol {


    @Override
    default @Nullable N tryGetNode() {
        return null;
    }
}
