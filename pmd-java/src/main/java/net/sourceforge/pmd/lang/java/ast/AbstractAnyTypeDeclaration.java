/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;


/**
 * Abstract class for type declarations nodes.
 */
public abstract class AbstractAnyTypeDeclaration extends AbstractJavaAccessTypeNode implements ASTAnyTypeDeclaration {

    private JavaTypeQualifiedName qualifiedName;


    AbstractAnyTypeDeclaration(int i) {
        super(i);
    }


    AbstractAnyTypeDeclaration(JavaParser parser, int i) {
        super(parser, i);
    }


    /**
     * Returns true if this type declaration is nested inside an interface, class or annotation.
     */
    @Override
    public final boolean isNested() {
        return jjtGetParent() instanceof ASTClassOrInterfaceBodyDeclaration
                || jjtGetParent() instanceof ASTAnnotationTypeMemberDeclaration;
    }


    @Override
    public final JavaTypeQualifiedName getQualifiedName() {
        return qualifiedName;
    }


    public void setQualifiedName(JavaTypeQualifiedName qualifiedName) {
        this.qualifiedName = qualifiedName;
        this.typeDefinition = JavaTypeDefinition.forClass(qualifiedName.getType());
    }
}

