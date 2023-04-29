/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;


/**
 * Abstract class for type declarations nodes.
 * This is a {@linkplain Node#isFindBoundary() find boundary} for tree traversal methods.
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

    @Override
    public @NonNull String getSimpleName() {
        assert getImageInternal() != null : "Null simple name";
        return getImageInternal();
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

    @Override
    public boolean isFindBoundary() {
        return isNested();
    }
}

