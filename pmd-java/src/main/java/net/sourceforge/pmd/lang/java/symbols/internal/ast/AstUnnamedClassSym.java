/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

class AstUnnamedClassSym implements JClassSymbol {
    private ASTCompilationUnit node;
    private final List<JMethodSymbol> declaredMethods;
    private final List<JFieldSymbol> declaredFields;

    AstUnnamedClassSym(ASTCompilationUnit node, AstSymFactory factory) {
        this.node = node;

        final List<JMethodSymbol> myMethods = new ArrayList<>();
        final List<JFieldSymbol> myFields = new ArrayList<>();

        node.children(ASTMethodDeclaration.class).forEach(dnode -> {
            myMethods.add(new AstMethodSym((ASTMethodDeclaration) dnode, factory, this));
        });
        node.children(ASTFieldDeclaration.class).forEach(dnode -> {
            for (ASTVariableDeclaratorId varId : dnode.getVarIds()) {
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
    public @Nullable JClassSymbol getEnclosingClass() {
        return null;
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
    public @Nullable String getCanonicalName() {
        return null;
    }

    @Override
    public @Nullable JExecutableSymbol getEnclosingMethod() {
        return null;
    }

    @Override
    public List<JClassSymbol> getDeclaredClasses() {
        return Collections.emptyList();
    }

    @Override
    public List<JMethodSymbol> getDeclaredMethods() {
        return declaredMethods;
    }

    @Override
    public List<JConstructorSymbol> getConstructors() {
        return Collections.emptyList();
    }

    @Override
    public List<JFieldSymbol> getDeclaredFields() {
        return declaredFields;
    }

    @Override
    public List<JClassType> getSuperInterfaceTypes(Substitution substitution) {
        return Collections.emptyList();
    }

    @Override
    public @Nullable JClassType getSuperclassType(Substitution substitution) {
        return null;
    }

    @Override
    public @Nullable JClassSymbol getSuperclass() {
        return null;
    }

    @Override
    public List<JClassSymbol> getSuperInterfaces() {
        return Collections.emptyList();
    }

    @Override
    public @Nullable JTypeDeclSymbol getArrayComponent() {
        return null;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public boolean isRecord() {
        return false;
    }

    @Override
    public boolean isAnnotation() {
        return false;
    }

    @Override
    public boolean isLocalClass() {
        return false;
    }

    @Override
    public boolean isAnonymousClass() {
        return false;
    }

    @Override
    public TypeSystem getTypeSystem() {
        return node.getTypeSystem();
    }

    @Override
    public @NonNull String getSimpleName() {
        return "<UnnamedClass>";
    }

    @Override
    public List<JTypeVar> getTypeParameters() {
        return Collections.emptyList();
    }
}
