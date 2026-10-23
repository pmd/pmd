/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

final class AstFieldSym extends AbstractAstVariableSym implements JFieldSymbol {

    private final JClassSymbol owner;
    private final int modifiers;

    AstFieldSym(ASTVariableId node,
                AstSymFactory factory,
                JClassSymbol owner) {
        super(node, factory);
        this.owner = owner;
        this.modifiers = JModifier.toReflect(node.getModifiers().getEffectiveModifiers());
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    @Override
    public @Nullable Object getConstValue() {
        if (!node.hasModifiers(JModifier.STATIC, JModifier.FINAL)) {
            return null;
        }

        ASTExpression initializer = node.getInitializer();
        Object value = initializer == null ? null : initializer.getConstValue();
        if (value == null) {
            return null;
        }

        JTypeMirror type = getTypeMirror(Substitution.EMPTY);
        return (type.isPrimitive() || TypeTestUtil.isExactlyA(String.class, type)) ? value : null;
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
