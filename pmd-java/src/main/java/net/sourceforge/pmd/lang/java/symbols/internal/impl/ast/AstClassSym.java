/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.ImplicitMemberSymbols;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolEquality;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolToStrings;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ReflectSymInternals;


final class AstClassSym
    extends AbstractAstTParamOwner<ASTAnyTypeDeclaration>
    implements JClassSymbol {

    private final @Nullable JClassSymbol enclosing;
    private final List<JClassSymbol> declaredClasses;
    private final List<JMethodSymbol> declaredMethods;
    private final List<JConstructorSymbol> declaredCtors;
    private final List<JFieldSymbol> declaredFields;

    AstClassSym(ASTAnyTypeDeclaration node,
                AstSymFactory factory) {
        this(node, factory, null);
    }

    AstClassSym(ASTAnyTypeDeclaration node,
                AstSymFactory factory,
                @Nullable JClassSymbol enclosing) {
        super(node, factory);
        this.enclosing = enclosing;

        // evaluate everything strictly
        // this populates symbols on the relevant AST nodes

        List<JClassSymbol> myClasses = new ArrayList<>();
        List<JMethodSymbol> myMethods = new ArrayList<>();
        List<JConstructorSymbol> myCtors = new ArrayList<>();
        List<JFieldSymbol> myFields = new ArrayList<>();

        for (ASTAnyTypeBodyDeclaration decl : node.getDeclarations()) {

            JavaNode dnode = decl.getDeclarationNode();

            if (dnode instanceof ASTAnyTypeDeclaration) {
                myClasses.add(new AstClassSym((ASTAnyTypeDeclaration) dnode, factory, this));
            } else if (dnode instanceof ASTMethodDeclaration) {
                myMethods.add(new AstMethodSym((ASTMethodDeclaration) dnode, factory, this));
            } else if (dnode instanceof ASTConstructorDeclaration) {
                myCtors.add(new AstCtorSym((ASTConstructorDeclaration) dnode, factory, this));
            } else if (dnode instanceof ASTFieldDeclaration) {
                for (ASTVariableDeclaratorId varId : ((ASTFieldDeclaration) dnode).getVarIds()) {
                    myFields.add(new AstFieldSym(varId, factory, this));
                }
            } else if (dnode instanceof ASTEnumConstant) {
                myFields.add(new AstFieldSym(((ASTEnumConstant) dnode).getVarId(), factory, this));
            }
        }

        if (myCtors.isEmpty()) {
            myCtors.add(ImplicitMemberSymbols.defaultCtor(this));
        }

        if (this.isEnum()) {
            myMethods.add(ImplicitMemberSymbols.enumOrdinal(this));
            myMethods.add(ImplicitMemberSymbols.enumValueOf(this));
        }

        this.declaredClasses = Collections.unmodifiableList(myClasses);
        this.declaredMethods = Collections.unmodifiableList(myMethods);
        this.declaredCtors = Collections.unmodifiableList(myCtors);
        this.declaredFields = Collections.unmodifiableList(myFields);
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
                (JavaNode)
                    node.ancestors()
                        .filter(it -> it instanceof ASTMethodOrConstructorDeclaration || it instanceof ASTAnyTypeDeclaration || it instanceof ASTInitializer)
                        .first();

            if (!(enclosing instanceof ASTMethodOrConstructorDeclaration)) {
                return null;
            }
            ASTAnyTypeDeclaration methodOwner = enclosing.getEnclosingType();
            if (enclosing instanceof ASTMethodDeclaration) {
                return new AstMethodSym((ASTMethodDeclaration) enclosing, factory, methodOwner.getSymbol());
            } else if (enclosing instanceof ASTConstructorDeclaration) {
                return new AstCtorSym((ASTConstructorDeclaration) enclosing, factory, methodOwner.getSymbol());
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
        if (isEnum()) {
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
