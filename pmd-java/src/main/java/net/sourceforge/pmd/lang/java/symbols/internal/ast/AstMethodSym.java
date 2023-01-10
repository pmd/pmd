/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeOps;

/**
 * @author Clément Fournier
 */
final class AstMethodSym
    extends AbstractAstExecSymbol<ASTMethodDeclaration>
    implements JMethodSymbol {

    AstMethodSym(ASTMethodDeclaration node, AstSymFactory factory, JClassSymbol owner) {
        super(node, factory, owner);
    }

    @Override
    public boolean isBridge() {
        return false;
    }

    @Override
    public JTypeMirror getReturnType(Substitution subst) {
        ASTType rt = node.getResultTypeNode();
        return TypeOps.subst(rt.getTypeMirror(), subst);
    }

    @Override
    public String getSimpleName() {
        return node.getName();
    }

    @Override
    public @Nullable SymbolicValue getDefaultAnnotationValue() {
        if (node.getDefaultClause() != null) {
            return AstSymbolicAnnot.ofNode(node.getDefaultClause().getConstant());
        }
        
        return null;
    }
}
