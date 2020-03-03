/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;
import net.sourceforge.pmd.lang.java.symbols.table.internal.ResolveResultImpl.ClassResolveResult;
import net.sourceforge.pmd.lang.java.symbols.table.internal.ResolveResultImpl.VarResolveResult;

/**
 * Fields, methods, member types declared in a type.
 */
final class TypeMemberSymTable extends AbstractSymbolTable {

    private final @NonNull JClassSymbol typeSym;

    private final Map<String, List<JMethodSymbol>> methodResolveCache = new HashMap<>();
    private final Map<String, ResolveResult<JTypeDeclSymbol>> typeResolveCache = new HashMap<>();

    private final ASTAnyTypeDeclaration node;

    TypeMemberSymTable(JSymbolTable parent,
                       SymbolTableHelper helper,
                       ASTAnyTypeDeclaration node) {

        super(parent, helper);
        this.node = node;
        assert node != null : "Null type decl?";
        typeSym = node.getSymbol();
    }


    @Override
    protected @Nullable ResolveResult<JVariableSymbol> resolveValueNameImpl(String simpleName) {
        JVariableSymbol fieldSig = typeSym.getDeclaredField(simpleName); // TODO inherited fields
        if (fieldSig == null) {
            return null;
        }
        // type members are contributed by the class decl, to simplify impl (ie contributor is not the FieldDeclaration)
        return new VarResolveResult(fieldSig, this, node);
    }

    @Override
    protected @Nullable ResolveResult<JTypeDeclSymbol> resolveTypeNameImpl(String simpleName) {
        return typeResolveCache.computeIfAbsent(simpleName, this::findClass);
    }

    @Nullable
    private ResolveResult<JTypeDeclSymbol> findClass(String simpleName) {
        @Nullable JClassSymbol member = typeSym.getDeclaredClass(simpleName);
        if (member == null) {
            return null;
        }
        return new ClassResolveResult(member, this, node);
    }


    @Override
    protected @Nullable List<JMethodSymbol> getCachedMethodResults(String simpleName) {
        return methodResolveCache.get(simpleName);
    }

    @Override
    protected void cacheMethodResult(String simpleName, List<JMethodSymbol> sigs) {
        methodResolveCache.put(simpleName, sigs);
    }

    @Override
    protected List<JMethodSymbol> resolveMethodNamesHere(String simpleName) {
        return new ArrayList<>(typeSym.getDeclaredMethods());
    }
}
