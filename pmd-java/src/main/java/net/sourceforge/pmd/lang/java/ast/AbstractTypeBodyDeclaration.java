/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration.DeclarationKind.ANNOTATION;
import static net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration.DeclarationKind.ANNOTATION_METHOD;
import static net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration.DeclarationKind.CLASS;
import static net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration.DeclarationKind.CONSTRUCTOR;
import static net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration.DeclarationKind.EMPTY;
import static net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration.DeclarationKind.ENUM;
import static net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration.DeclarationKind.FIELD;
import static net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration.DeclarationKind.INITIALIZER;
import static net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration.DeclarationKind.INTERFACE;
import static net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration.DeclarationKind.METHOD;

import net.sourceforge.pmd.annotation.InternalApi;


/**
 * @author Clément Fournier
 * @since 6.2.0
 */
@Deprecated
@InternalApi
abstract class AbstractTypeBodyDeclaration extends AbstractJavaNode implements ASTAnyTypeBodyDeclaration {

    private DeclarationKind kind;

    AbstractTypeBodyDeclaration(int id) {
        super(id);
    }


    AbstractTypeBodyDeclaration(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public JavaNode getDeclarationNode() {
        if (getNumChildren() == 0) {
            return null;
        }

        // skips the annotations
        AccessNode node = getFirstChildOfType(AccessNode.class);
        if (node == null) {
            return getFirstChildOfType(ASTInitializer.class);
        }

        return (JavaNode) node;
    }


    private DeclarationKind determineKind() {
        if (getNumChildren() == 0) {
            return EMPTY;
        }

        JavaNode node = getDeclarationNode();

        if (node instanceof ASTInitializer) {
            return INITIALIZER;
        } else if (node instanceof ASTConstructorDeclaration) {
            return CONSTRUCTOR;
        } else if (node instanceof ASTMethodDeclaration) {
            return METHOD;
        } else if (node instanceof ASTAnnotationMethodDeclaration) {
            return ANNOTATION_METHOD;
        } else if (node instanceof ASTFieldDeclaration) {
            return FIELD;
        } else if (node instanceof ASTClassOrInterfaceDeclaration) {
            return ((ASTClassOrInterfaceDeclaration) node).isInterface() ? INTERFACE : CLASS;
        } else if (node instanceof ASTAnnotationTypeDeclaration) {
            return ANNOTATION;
        } else if (node instanceof ASTEnumDeclaration) {
            return ENUM;
        }

        throw new IllegalStateException("Declaration node types should all be known");
    }

    @Override
    public DeclarationKind getKind() {
        if (kind == null) {
            kind = determineKind();
        }

        return kind;
    }
}
