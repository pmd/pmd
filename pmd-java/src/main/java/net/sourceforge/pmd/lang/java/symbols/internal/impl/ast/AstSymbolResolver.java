/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;

/**
 * A symbol resolver that knows about a single compilation unit.
 * It's used for the current compilation unit and is thrown away.
 * This may only be used after symbols have been populated on
 * the compilation unit.
 */
public final class AstSymbolResolver implements SymbolResolver {

    private final Map<String, ASTAnyTypeDeclaration> knownTopLevelClasses;

    public AstSymbolResolver(ASTCompilationUnit compilationUnit) {
        String packageName = compilationUnit.getPackageName();
        List<ASTAnyTypeDeclaration> typeDecls = compilationUnit.getTypeDeclarations().toList();
        knownTopLevelClasses = new HashMap<>(typeDecls.size());
        for (ASTAnyTypeDeclaration typeDecl : typeDecls) {
            String name = packageName.isEmpty() ? typeDecl.getSimpleName()
                                                : packageName + '.' + typeDecl.getSimpleName();
            knownTopLevelClasses.put(name, typeDecl);
        }
    }

    @Override
    public @Nullable JClassSymbol resolveClassFromBinaryName(@NonNull String binaryName) {
        ASTAnyTypeDeclaration known = knownTopLevelClasses.get(binaryName);
        if (known == null) {
            if (binaryName.indexOf('$') >= 0) {
                return resolveClassFromCanonicalName(binaryName.replace('$', '.'));
            }
            return null;
        } else {
            return known.getSymbol();
        }
    }

}
