/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import net.sourceforge.pmd.lang.ast.Node;


/**
 * Constrains the return type of getDeclaration. This is used to avoid having
 * a type parameter directly on {@link JElementSymbol}, which would get
 * in the way most of the time. Not visible outside this package, it's just
 * a code organisation device.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
interface BoundToNode<N extends Node> extends JElementSymbol {

}
