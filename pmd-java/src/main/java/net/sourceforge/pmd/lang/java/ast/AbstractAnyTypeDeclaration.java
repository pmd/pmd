/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;


/**
 * Abstract class for type declarations nodes.
 */
@Deprecated
@InternalApi
public abstract class AbstractAnyTypeDeclaration extends AbstractJavaAccessTypeNode implements ASTAnyTypeDeclaration {

    private JavaTypeQualifiedName qualifiedName;


    AbstractAnyTypeDeclaration(int i) {
        super(i);
    }


    AbstractAnyTypeDeclaration(JavaParser parser, int i) {
        super(parser, i);
    }


    @Override
    public final boolean isNested() {
        return getParent() instanceof ASTClassOrInterfaceBodyDeclaration
            || getParent() instanceof ASTAnnotationTypeMemberDeclaration;
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
    public String getSimpleName() {
        return getImage();
    }

    /**
     * Returns true if the enclosing type of this type declaration
     * is any of the given kinds. If this declaration is a top-level
     * declaration, returns false. This won't consider anonymous classes
     * until #905 is tackled. TODO 7.0.0
     *
     * @param kinds Kinds to test
     */
    // TODO 7.0.0 move that up to ASTAnyTypeDeclaration
    public final boolean enclosingTypeIsA(TypeKind... kinds) {

        ASTAnyTypeDeclaration parent = getEnclosingTypeDeclaration();
        if (parent == null) {
            return false;
        }

        for (TypeKind k : kinds) {
            if (parent.getTypeKind() == k) {
                return true;
            }
        }

        return false;
    }


    /**
     * Returns the enclosing type of this type, if it is nested.
     * Otherwise returns null. This won't consider anonymous classes
     * until #905 is tackled. TODO 7.0.0
     */
    public final ASTAnyTypeDeclaration getEnclosingTypeDeclaration() {
        if (!isNested()) {
            return null;
        }
        Node parent = getNthParent(3);

        return parent instanceof ASTAnyTypeDeclaration ? (ASTAnyTypeDeclaration) parent : null;
    }

    @Override
    public final JavaTypeQualifiedName getQualifiedName() {
        return qualifiedName;
    }


    @InternalApi
    @Deprecated
    public void setQualifiedName(JavaTypeQualifiedName qualifiedName) {
        this.qualifiedName = qualifiedName;
        this.typeDefinition = JavaTypeDefinition.forClass(qualifiedName.getType());
    }
}

