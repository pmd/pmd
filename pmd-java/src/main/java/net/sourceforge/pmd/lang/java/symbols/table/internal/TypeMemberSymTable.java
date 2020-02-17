/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;
import net.sourceforge.pmd.lang.java.symbols.table.internal.ResolveResultImpl.VarResolveResult;

/**
 * Fields, methods, member types declared in a type.
 *
 * TODO inherited stuff
 */
final class TypeMemberSymTable extends AbstractSymbolTable {

    private final @NonNull JClassSymbol typeSym;
    private final ASTAnyTypeDeclaration node;

    TypeMemberSymTable(JSymbolTable parent,
                       SymbolTableHelper helper,
                       ASTAnyTypeDeclaration node) {

        super(parent, helper);
        this.node = node;
        assert node != null : "Null type decl?";
        typeSym = node.getSymbol();
        assert typeSym != null : "Null symbol?";
    }


    @Override
    protected @Nullable ResolveResult<JVariableSymbol> resolveValueNameImpl(String simpleName) {
        JVariableSymbol fieldSig = typeSym.getDeclaredField(simpleName);
        // type members are contributed by the class decl, to simplify impl (ie contributor is not the FieldDeclaration)
        return fieldSig == null ? null : new VarResolveResult(fieldSig, this, node);
    }

    @Override
    protected Stream<JMethodSymbol> resolveMethodNameImpl(String simpleName) {
        if (simpleName.equals(JConstructorSymbol.CTOR_NAME)) {
            return Stream.empty();
        }

        return typeSym.getDeclaredMethods(simpleName).stream();
    }
}
