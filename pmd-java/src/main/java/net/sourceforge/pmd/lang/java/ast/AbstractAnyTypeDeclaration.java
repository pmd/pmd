/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.rule.xpath.DeprecatedAttribute;
import net.sourceforge.pmd.util.document.FileLocation;


/**
 * Abstract class for type declarations nodes.
 */
abstract class AbstractAnyTypeDeclaration extends AbstractTypedSymbolDeclarator<JClassSymbol> implements ASTAnyTypeDeclaration, LeftRecursiveNode {

    private String binaryName;
    private @Nullable String canonicalName;

    AbstractAnyTypeDeclaration(int i) {
        super(i);
    }

    @Override
    public FileLocation getReportLocation() {
        if (isAnonymous()) {
            return super.getReportLocation();
        } else {
            // report on the identifier, not the entire class.
            return getModifiers().getLastToken().getNext().getReportLocation();
        }
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

    @Override
    public @NonNull String getBinaryName() {
        assert binaryName != null : "Null binary name";
        return binaryName;
    }

    @Override
    public @Nullable String getCanonicalName() {
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

    @Override
    public @NonNull JClassType getTypeMirror() {
        return (JClassType) super.getTypeMirror();
    }
}

