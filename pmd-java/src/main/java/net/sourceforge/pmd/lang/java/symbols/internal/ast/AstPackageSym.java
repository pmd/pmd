/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.symbols.JPackageSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolVisitor;

final class AstPackageSym extends AbstractAstAnnotableSym<ASTPackageDeclaration> implements JPackageSymbol {
    AstPackageSym(ASTPackageDeclaration packageDeclaration, AstSymFactory factory) {
        super(packageDeclaration, factory);
    }

    @Override
    public String getSimpleName() {
        return node.getName();
    }

    @Override
    public <R, P> R acceptVisitor(SymbolVisitor<R, P> visitor, P param) {
        return visitor.visitPackage(this, param);
    }
}
