/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTModuleDeclaration;

public interface JModuleSymbol extends AnnotableSymbol,
                                       BoundToNode<ASTModuleDeclaration> {

    Set<String> getExportedPackages();
}