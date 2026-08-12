/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;

/**
 * @since 7.27.0
 */
public interface JPackageSymbol extends AnnotableSymbol,
        BoundToNode<ASTPackageDeclaration> {
}
