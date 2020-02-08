/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolEquality;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolToStrings;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ReflectSymInternals;


final class AstClassSymbol
    extends AbstractAstTParamOwner<ASTAnyTypeDeclaration>
    implements JClassSymbol {

    private final @Nullable JClassSymbol enclosing;
    private final List<JClassSymbol> declaredClasses;
    private final List<JMethodSymbol> declaredMethods;
    private final List<JConstructorSymbol> declaredCtors;
    private final List<JFieldSymbol> declaredFields;

    AstClassSymbol(ASTAnyTypeDeclaration node,
                   AstSymFactory factory) {
        this(node, factory, null);
    }

    AstClassSymbol(ASTAnyTypeDeclaration node,
                   AstSymFactory factory,
                   @Nullable JClassSymbol enclosing) {
        super(node, factory);
        this.enclosing = enclosing;

        // evaluate everything strictly
        // this populates symbols on the relevant AST nodes

        declaredClasses = node.getDeclarations()
                              .stream()
                              .filter(it -> it.getLastChild() instanceof ASTAnyTypeDeclaration)
                              .map(it -> new AstClassSymbol((ASTAnyTypeDeclaration) it.getLastChild(), factory, this))
                              .collect(Collectors.toList());

        declaredMethods = node.getDeclarations()
                              .stream()
                              .filter(it -> it.getLastChild() instanceof ASTMethodDeclaration)
                              .map(it -> new AstMethodSymbol((ASTMethodDeclaration) it.getLastChild(), factory, this))
                              .collect(Collectors.toList());

        declaredCtors = node.getDeclarations()
                            .stream()
                            .filter(it -> it.getLastChild() instanceof ASTConstructorDeclaration)
                            .map(it -> new AstCtorSymbol((ASTConstructorDeclaration) it.getLastChild(), factory, this))
                            .collect(Collectors.toList());

        declaredFields = node.getDeclarations()
                             .stream()
                             .flatMap(it -> {
                                 JavaNode decl = it.getLastChild();
                                 if (decl instanceof ASTFieldDeclaration) {
                                     return StreamSupport.stream(((ASTFieldDeclaration) decl).spliterator(), false);
                                 } else if (decl instanceof ASTEnumConstant) {
                                     return Stream.of(((ASTEnumConstant) decl).getVarId());
                                 }
                                 return Stream.empty();
                             })
                             .map(it -> new AstFieldSym(it, factory, this))
                             .collect(Collectors.toList());
    }

    @Override
    public @NonNull String getSimpleName() {
        if (isAnonymousClass()) {
            // cannot return null
            return "<anonymous>";
        } else {
            return Objects.requireNonNull(node.getSimpleName(), "Simple name is null");
        }
    }


    @Override
    public @NonNull String getBinaryName() {
        return node.getBinaryName();
    }

    @Override
    public @Nullable String getCanonicalName() {
        return node.isLocal() || node.isAnonymous()
               ? null
               : getBinaryName().replace('$', '.');
    }

    @Override
    public @Nullable JClassSymbol getEnclosingClass() {
        return enclosing;
    }

    @Override
    public boolean isUnresolved() {
        return false;
    }

    @Override
    public @Nullable JExecutableSymbol getEnclosingMethod() {
        if (node.isLocal()) {
            JavaNode enclosing =
                node.getFirstParentOfAnyType(ASTMethodOrConstructorDeclaration.class, ASTAnyTypeDeclaration.class, ASTInitializer.class);

            if (enclosing instanceof ASTInitializer || enclosing instanceof ASTAnyTypeDeclaration) {
                return null;
            }
            ASTAnyTypeDeclaration methodOwner = enclosing.getEnclosingType();
            if (enclosing instanceof ASTMethodDeclaration) {
                return new AstMethodSymbol((ASTMethodDeclaration) enclosing, factory, methodOwner.getSymbol());
            } else if (enclosing instanceof ASTConstructorDeclaration) {
                return new AstCtorSymbol((ASTConstructorDeclaration) enclosing, factory, methodOwner.getSymbol());
            }
        }
        return null;
    }

    @Override
    public List<JClassSymbol> getDeclaredClasses() {
        return declaredClasses;
    }

    @Override
    public List<JMethodSymbol> getDeclaredMethods() {
        return declaredMethods;
    }

    @Override
    public List<JConstructorSymbol> getConstructors() {
        return declaredCtors;
    }

    @Override
    public List<JFieldSymbol> getDeclaredFields() {
        return declaredFields;
    }


    @Override
    public @Nullable JClassSymbol getSuperclass() {
        if (node instanceof ASTEnumDeclaration) {
            return ReflectSymInternals.ENUM_SYM;
        } else if (node instanceof ASTClassOrInterfaceDeclaration) {
            // This is TODO, needs symbol table
        }
        return null;
    }

    // those casts only succeed if the program compiles, it relies on
    // the fact that only classes can be super interfaces, ie not String[]
    // or some type var

    @Override
    public List<JClassSymbol> getSuperInterfaces() {
        // TODO needs symbol table
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
    public boolean isInterface() {
        return node.isInterface();
    }

    @Override
    public boolean isEnum() {
        return node.isEnum();
    }

    @Override
    public boolean isAnnotation() {
        return node.isAnnotation();
    }

    @Override
    public boolean isLocalClass() {
        return node.isLocal();
    }

    @Override
    public boolean isAnonymousClass() {
        return node.isAnonymous();
    }

    @Override
    public @Nullable Class<?> getJvmRepr() {
        return null;
    }

    @Override
    public String toString() {
        return SymbolToStrings.AST.classToString(this);
    }

    @Override
    public boolean equals(Object o) {
        return SymbolEquality.CLASS.equals(this, o);
    }

    @Override
    public int hashCode() {
        return SymbolEquality.CLASS.hash(this);
    }
}
