/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;


/**
 * Abstract class for type declarations nodes.
 * This is a {@linkplain Node#isFindBoundary() find boundary} for tree traversal methods.
 */
abstract class AbstractTypeDeclaration extends AbstractTypedSymbolDeclarator<JClassSymbol> implements ASTTypeDeclaration, LeftRecursiveNode {

    private String binaryName;
    private @Nullable String canonicalName;
    private JavaccToken simpleName;

    AbstractTypeDeclaration(int i) {
        super(i);
    }

    @Override
    public FileLocation getReportLocation() {
        if (isAnonymous()) {
            ASTConstructorCall constructorCall = ancestors(ASTConstructorCall.class).first();
            if (constructorCall != null) {
                return constructorCall.getTypeNode().getReportLocation();
            }
            ASTEnumConstant enumConstant = ancestors(ASTEnumConstant.class).first();
            if (enumConstant != null) {
                return enumConstant.getReportLocation();
            }
            return super.getReportLocation();
        } else {
            // report on the identifier, not the entire class.
            return simpleName.getReportLocation();
        }
    }

    @NonNull
    @Override
    public String getSimpleName() {
        assert simpleName != null : "Null simple name";
        return simpleName.getImage();
    }

    final void setSimpleName(JavaccToken simpleName) {
        this.simpleName = simpleName;
    }

    @Override
    public @NonNull String getBinaryName() {
        assert binaryName != null : "Null binary name";
        return binaryName;
    }

    @Override
    public @Nullable String getCanonicalName() {
        assert binaryName != null : "binary name and canonical name weren't set";
        return canonicalName;
    }

    @Override
    public Visibility getVisibility() {
        return isLocal() ? Visibility.V_LOCAL : ASTTypeDeclaration.super.getVisibility();
    }

    void setBinaryName(String binaryName, @Nullable String canon) {
        assert binaryName != null : "Null binary name";
        this.binaryName = binaryName;
        this.canonicalName = canon;
    }

    @Override
    public @NonNull JClassType getTypeMirror() {
        return (JClassType) super.getTypeMirror();
    }

    @Override
    public boolean isFindBoundary() {
        return isNested();
    }
}

