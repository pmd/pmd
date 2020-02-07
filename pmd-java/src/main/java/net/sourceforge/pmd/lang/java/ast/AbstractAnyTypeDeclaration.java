/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;


/**
 * Abstract class for type declarations nodes.
 */
abstract class AbstractAnyTypeDeclaration extends AbstractJavaTypeNode implements ASTAnyTypeDeclaration, LeftRecursiveNode {

    private JavaTypeQualifiedName qualifiedName;
    private JClassSymbol symbol;


    AbstractAnyTypeDeclaration(int i) {
        super(i);
    }

    @Override
    @Deprecated
    public String getImage() {
        return super.getImage();
    }

    @Override
    public String getBinaryName() {
        return getQualifiedName().getBinaryName();
    }

    @Override
    public Visibility getVisibility() {
        return isLocal() ? Visibility.V_LOCAL : ASTAnyTypeDeclaration.super.getVisibility();
    }

    @NonNull
    @Override
    public JClassSymbol getSymbol() {
        assert symbol != null : "Symbol was null, maybe not set by qualified name resolver";
        return symbol;
    }

    @Nullable
    JClassSymbol getSymbolInternal() {
        return symbol;
    }

    void setSymbol(JClassSymbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public final JavaTypeQualifiedName getQualifiedName() {
        return qualifiedName;
    }

    void setQualifiedName(JavaTypeQualifiedName qualifiedName) {
        this.qualifiedName = qualifiedName;
        setTypeDefinition(JavaTypeDefinition.forClass(qualifiedName.getType()));
    }
}

