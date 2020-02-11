/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeOps;

final class AstFieldSym extends AbstractAstVariableSym implements JFieldSymbol {

    private final JClassSymbol owner;

    AstFieldSym(ASTVariableDeclaratorId node,
                AstSymFactory factory,
                JClassSymbol owner) {
        super(node, factory);
        this.owner = owner;
    }

    @Override
    public int getModifiers() {
        return JModifier.toReflect(node.getModifiers().getEffectiveModifiers());
    }

    @Override
    public boolean isEnumConstant() {
        return node.isEnumConstant();
    }

    @Override
    public @NonNull JClassSymbol getEnclosingClass() {
        return owner;
    }

    @Override
    public JTypeMirror getTypeMirror(Substitution subst) {
        // enum constants ha
        return TypeOps.subst(node.getTypeMirror(), subst);
    }
}
