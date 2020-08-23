/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.rule.xpath.DeprecatedAttribute;


/**
 * Abstract class for type declarations nodes.
 */
abstract class AbstractAnyTypeDeclaration extends AbstractTypedSymbolDeclarator<JClassSymbol> implements ASTAnyTypeDeclaration, LeftRecursiveNode {

    private String binaryName;
    private @Nullable String canonicalName;

    AbstractAnyTypeDeclaration(int i) {
        super(i);
    }

    /**
     * @deprecated Use {@link #getSimpleName()}
     */
    @Deprecated
    @DeprecatedAttribute(replaceWith = "@SimpleName")
    @Override
    public String getImage() {
        return getSimpleName();
    }

    @NonNull
    @Override
    public String getSimpleName() {
        assert super.getImage() != null : "Null simple name";
        return super.getImage();
    }

    @NonNull
    @Override
    public String getBinaryName() {
        assert binaryName != null : "Null binary name";
        return binaryName;
    }

    @Nullable
    @Override
    public String getCanonicalName() {
        assert binaryName != null : "Canonical name wasn't set";
        return canonicalName;
    }

    @Override
    public Visibility getVisibility() {
        return isLocal() ? Visibility.V_LOCAL : ASTAnyTypeDeclaration.super.getVisibility();
    }

    void setBinaryName(String binaryName, @Nullable String canon) {
        assert binaryName != null : "Null binary name";
        this.binaryName = binaryName;
        this.canonicalName = canon;
    }
}

