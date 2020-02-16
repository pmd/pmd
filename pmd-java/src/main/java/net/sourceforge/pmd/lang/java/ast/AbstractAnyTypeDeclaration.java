/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;


/**
 * Abstract class for type declarations nodes.
 */
abstract class AbstractAnyTypeDeclaration extends AbstractJavaTypeNode implements ASTAnyTypeDeclaration, LeftRecursiveNode {

    private JavaTypeQualifiedName qualifiedName;

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

    @Override
    public final JavaTypeQualifiedName getQualifiedName() {
        return qualifiedName;
    }

    void setQualifiedName(JavaTypeQualifiedName qualifiedName) {
        this.qualifiedName = qualifiedName;
        setTypeDefinition(JavaTypeDefinition.forClass(qualifiedName.getType()));
    }

}

