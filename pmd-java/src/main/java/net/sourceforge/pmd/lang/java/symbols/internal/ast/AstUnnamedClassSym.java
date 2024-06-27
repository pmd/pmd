/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.EmptyClassSymbol;

class AstUnnamedClassSym extends EmptyClassSymbol {
    private final List<JMethodSymbol> declaredMethods;
    private final List<JFieldSymbol> declaredFields;

    AstUnnamedClassSym(ASTCompilationUnit node, AstSymFactory factory) {
        super(node::getTypeSystem);

        final List<JMethodSymbol> myMethods = new ArrayList<>();
        final List<JFieldSymbol> myFields = new ArrayList<>();

        node.children(ASTMethodDeclaration.class).forEach(dnode -> {
            myMethods.add(new AstMethodSym((ASTMethodDeclaration) dnode, factory, this));
        });
        node.children(ASTFieldDeclaration.class).forEach(dnode -> {
            for (ASTVariableId varId : dnode.getVarIds()) {
                myFields.add(new AstFieldSym(varId, factory, this));
            }
        });

        this.declaredMethods = Collections.unmodifiableList(myMethods);
        this.declaredFields = Collections.unmodifiableList(myFields);
    }

    @Override
    public int getModifiers() {
        return JModifier.toReflect(EnumSet.of(JModifier.FINAL));
    }

    @Override
    public @NonNull String getPackageName() {
        return "";
    }

    @Override
    public @NonNull String getBinaryName() {
        return "UnnamedClass";
    }

    @Override
    public List<JMethodSymbol> getDeclaredMethods() {
        return declaredMethods;
    }

    @Override
    public List<JFieldSymbol> getDeclaredFields() {
        return declaredFields;
    }

    @Override
    public @NonNull String getSimpleName() {
        return "<UnnamedClass>";
    }
}
