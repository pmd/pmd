/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.rule.xpath.DeprecatedAttribute;


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
    protected @Nullable JavaccToken getPreferredReportLocation() {
        return isAnonymous() ? null
                             : getModifiers().getLastToken().getNext();
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

    @Override
    public boolean isFindBoundary() {
        return isNested();
    }
}

