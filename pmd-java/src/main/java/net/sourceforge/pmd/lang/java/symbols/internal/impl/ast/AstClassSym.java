/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnonymousClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.ImplicitMemberSymbols;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ReflectSymInternals;
import net.sourceforge.pmd.util.CollectionUtil;


final class AstClassSym
    extends AbstractAstTParamOwner<ASTAnyTypeDeclaration>
    implements JClassSymbol {

    private final @Nullable JTypeParameterOwnerSymbol enclosing;
    private final List<JClassSymbol> declaredClasses;
    private final List<JMethodSymbol> declaredMethods;
    private final List<JConstructorSymbol> declaredCtors;
    private final List<JFieldSymbol> declaredFields;

    AstClassSym(ASTAnyTypeDeclaration node,
                AstSymFactory factory,
                @Nullable JTypeParameterOwnerSymbol enclosing) {
        super(node, factory);
        this.enclosing = enclosing;

        // evaluate everything strictly
        // this populates symbols on the relevant AST nodes

        List<JClassSymbol> myClasses = new ArrayList<>();
        List<JMethodSymbol> myMethods = new ArrayList<>();
        List<JConstructorSymbol> myCtors = new ArrayList<>();
        List<JFieldSymbol> myFields = new ArrayList<>();

        if (node instanceof ASTEnumDeclaration) {
            node.getEnumConstants().forEach(constant -> myFields.add(new AstFieldSym(constant.getVarId(), factory, this)));
        }

        for (ASTBodyDeclaration dnode : node.getDeclarations()) {

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
            }
        }

        if (myCtors.isEmpty() && isClass() && !isAnonymousClass()) {
            myCtors.add(ImplicitMemberSymbols.defaultCtor(this));
        }

        if (this.isEnum()) {
            myMethods.add(ImplicitMemberSymbols.enumValues(this));
            myMethods.add(ImplicitMemberSymbols.enumValueOf(this));
        }

        this.declaredClasses = Collections.unmodifiableList(myClasses);
        this.declaredMethods = Collections.unmodifiableList(myMethods);
        this.declaredCtors = Collections.unmodifiableList(myCtors);
        this.declaredFields = Collections.unmodifiableList(myFields);
    }

    @Override
    public @NonNull String getSimpleName() {
        return node.getSimpleName();
    }


    @Override
    public @NonNull String getBinaryName() {
        return node.getBinaryName();
    }

    @Override
    public @Nullable String getCanonicalName() {
        return node.getCanonicalName();
    }

    @Override
    public boolean isUnresolved() {
        return false;
    }

    @Override
    public @Nullable JClassSymbol getEnclosingClass() {
        if (enclosing instanceof JClassSymbol) {
            return (JClassSymbol) enclosing;
        } else if (enclosing instanceof JExecutableSymbol) {
            return enclosing.getEnclosingClass();
        }
        assert enclosing == null;
        return null;
    }

    @Override
    public @Nullable JExecutableSymbol getEnclosingMethod() {
        return enclosing instanceof JExecutableSymbol ? (JExecutableSymbol) enclosing : null;
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
            ASTClassOrInterfaceType sup = ((ASTClassOrInterfaceDeclaration) node).getSuperClassTypeNode();
            return sup == null
                   ? ReflectSymInternals.OBJECT_SYM
                   : (JClassSymbol) sup.getReferencedSym();

        } else if (node instanceof ASTAnonymousClassDeclaration) {

            if (node.getParent() instanceof ASTEnumConstant) {

                return node.getEnclosingType().getSymbol();

            } else if (node.getParent() instanceof ASTConstructorCall) {

                JTypeDeclSymbol sym = ((ASTConstructorCall) node.getParent()).getTypeNode().getReferencedSym();

                return sym instanceof JClassSymbol && !sym.isInterface()
                       ? (JClassSymbol) sym
                       : ReflectSymInternals.OBJECT_SYM;

            }
        }
        // TODO records
        return null;
    }

    @Override
    public List<JClassSymbol> getSuperInterfaces() {
        return CollectionUtil.map(node.getSuperInterfaces(), n -> (JClassSymbol) n.getReferencedSym());
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

}
