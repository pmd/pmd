/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;


/**
 * Groups enum, class, annotation and interface declarations.
 *
 * @author Clément Fournier
 */
public abstract class ASTAnyTypeDeclaration extends AbstractJavaAccessTypeNode implements JavaQualifiableNode, AccessNode, JavaNode {

    private JavaQualifiedName qualifiedName;


    public ASTAnyTypeDeclaration(int i) {
        super(i);
    }


    public ASTAnyTypeDeclaration(JavaParser parser, int i) {
        super(parser, i);
    }


    /**
     * Finds the type kind of this declaration.
     *
     * @return The type kind of this declaration.
     */
    public abstract TypeKind getTypeKind();


    /**
     * Retrieves the member declarations (fields, methods, classes, etc.) from the body of this type declaration.
     *
     * @return The member declarations declared in this type declaration
     */
    public abstract List<ASTAnyTypeBodyDeclaration> getDeclarations();


    /**
     * Returns true if this type declaration is nested inside an interface, class or annotation.
     */
    public final boolean isNested() {
        return jjtGetParent() instanceof ASTClassOrInterfaceBodyDeclaration
                || jjtGetParent() instanceof ASTAnnotationTypeMemberDeclaration;
    }


    @Override
    public final JavaQualifiedName getQualifiedName() {
        if (qualifiedName == null) {
            qualifiedName = buildQualifiedName();
        }

        return qualifiedName;
    }


    /** Create the qualified name, which is then cached in the node. */
    protected final JavaQualifiedName buildQualifiedName() {
        return QualifiedNameFactory.ofClass(this);
    }


    /**
     * The kind of type this node declares.
     */
    public enum TypeKind {
        CLASS, INTERFACE, ENUM, ANNOTATION
    }

}
