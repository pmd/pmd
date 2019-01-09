/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl;

import java.lang.reflect.Constructor;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.symbols.internal.JConstructorSymbol;


public final class JConstructorSymbolImpl extends JAccessibleDeclarationSymbolImpl<ASTConstructorDeclaration>
    implements JConstructorSymbol {

    JConstructorSymbolImpl(Constructor<?> constructor) {
        super(constructor.getModifiers(), constructor.getDeclaringClass().getSimpleName(), constructor.getDeclaringClass());
    }


    JConstructorSymbolImpl(ASTConstructorDeclaration node) {
        super(node, accessNodeToModifiers(node), node.getImage());
    }
}
