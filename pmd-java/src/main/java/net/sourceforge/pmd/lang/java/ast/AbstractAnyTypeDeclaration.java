/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;


/**
 * Abstract class for type declarations nodes.
 */
abstract class AbstractAnyTypeDeclaration extends AbstractTypedSymbolDeclarator<JClassSymbol> implements ASTAnyTypeDeclaration, LeftRecursiveNode {

    private String binaryName;

    AbstractAnyTypeDeclaration(int i) {
        super(i);
    }

    @Override
    @Deprecated
    public String getImage() {
        return super.getImage();
    }

    @NonNull
    @Override
    public String getSimpleName() {
        assert getImage() != null : "Null simple name";
        return getImage();
    }

    @NonNull
    @Override
    public String getBinaryName() {
        assert binaryName != null : "Null binary name";
        return binaryName;
    }

    @Override
    public Visibility getVisibility() {
        return isLocal() ? Visibility.V_LOCAL : ASTAnyTypeDeclaration.super.getVisibility();
    }

    void setBinaryName(String binaryName) {
        assert binaryName != null : "Null binary name";
        this.binaryName = binaryName;
    }
}

