/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.SignedNode;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSignature;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;


/**
 * Represents a field declaration in the body of a type declaration.
 *
 * <p>This statement may define several variables, possibly of different types (see {@link
 * ASTVariableDeclaratorId#getType()}).
 * The nodes corresponding to the declared variables are accessible through {@link #iterator()}.
 *
 * <p>{@link AccessNode} methods take into account the syntactic context of the
 * declaration, e.g. {@link #isPublic()} will always return true if the field is
 * declared inside an interface, regardless of whether the {@code public} modifier
 * was specified or not. If you want to know whether the modifier was explicitly
 * stated, use e.g {@link #isSyntacticallyPublic()}.
 *
 * <pre>
 *
 * FieldDeclaration ::= Modifiers {@linkplain ASTType Type} {@linkplain ASTVariableDeclarator VariableDeclarator} ( "," {@linkplain ASTVariableDeclarator VariableDeclarator} )*
 *
 * Modifiers        ::= "public" | "static" | "protected" | "private"
 *                    | "final"  | "abstract" | "synchronized"
 *                    | "native" | "transient" | "volatile" | "strictfp"
 *                    | "default"  | {@linkplain ASTAnnotation Annotation}
 *
 * </pre>
 */
public class ASTFieldDeclaration extends AbstractJavaAccessTypeNode implements Dimensionable, SignedNode<ASTFieldDeclaration>, Iterable<ASTVariableDeclaratorId> {

    private JavaFieldSignature signature;


    @InternalApi
    @Deprecated
    public ASTFieldDeclaration(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTFieldDeclaration(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean isSyntacticallyPublic() {
        return super.isPublic();
    }

    @Override
    public boolean isPublic() {
        if (isAnnotationMember() || isInterfaceMember()) {
            return true;
        }
        return super.isPublic();
    }

    public boolean isSyntacticallyStatic() {
        return super.isStatic();
    }

    @Override
    public boolean isStatic() {
        if (isAnnotationMember() || isInterfaceMember()) {
            return true;
        }
        return super.isStatic();
    }

    public boolean isSyntacticallyFinal() {
        return super.isFinal();
    }

    @Override
    public boolean isFinal() {
        if (isAnnotationMember() || isInterfaceMember()) {
            return true;
        }
        return super.isFinal();
    }

    @Override
    public boolean isPrivate() {
        if (isAnnotationMember() || isInterfaceMember()) {
            return false;
        }
        return super.isPrivate();
    }

    @Override
    public boolean isPackagePrivate() {
        if (isAnnotationMember() || isInterfaceMember()) {
            return false;
        }
        return super.isPackagePrivate();
    }

    @Override
    public boolean isProtected() {
        if (isAnnotationMember() || isInterfaceMember()) {
            return false;
        }
        return super.isProtected();
    }

    public boolean isAnnotationMember() {
        return getNthParent(2) instanceof ASTAnnotationTypeBody;
    }

    public boolean isInterfaceMember() {
        if (getNthParent(2) instanceof ASTEnumBody) {
            return false;
        }
        ASTClassOrInterfaceBody classOrInterfaceBody = getFirstParentOfType(ASTClassOrInterfaceBody.class);
        if (classOrInterfaceBody == null || classOrInterfaceBody.isAnonymousInnerClass()) {
            return false;
        }
        if (classOrInterfaceBody.getParent() instanceof ASTClassOrInterfaceDeclaration) {
            ASTClassOrInterfaceDeclaration n = (ASTClassOrInterfaceDeclaration) classOrInterfaceBody.getParent();
            return n.isInterface();
        }
        return false;
    }

    @Override
    @Deprecated
    public boolean isArray() {
        return checkType() + checkDecl() > 0;
    }

    @Override
    @Deprecated
    public int getArrayDepth() {
        if (!isArray()) {
            return 0;
        }
        return checkType() + checkDecl();
    }

    private int checkType() {
        if (getNumChildren() == 0 || !(getChild(0) instanceof ASTType)) {
            return 0;
        }
        return ((ASTType) getChild(0)).getArrayDepth();
    }

    private int checkDecl() {
        if (getNumChildren() < 2 || !(getChild(1) instanceof ASTVariableDeclarator)) {
            return 0;
        }
        return ((ASTVariableDeclaratorId) getChild(1).getChild(0)).getArrayDepth();
    }

    /**
     * Gets the variable name of this field. This method searches the first
     * VariableDeclartorId node and returns its image or <code>null</code> if
     * the child node is not found.
     *
     * @return a String representing the name of the variable
     *
     * @deprecated FieldDeclaration may declare several variables, so this is not exhaustive
     *     Iterate on the {@linkplain ASTVariableDeclaratorId VariableDeclaratorIds} instead
     */
    @Deprecated
    public String getVariableName() {
        ASTVariableDeclaratorId decl = getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        if (decl != null) {
            return decl.getImage();
        }
        return null;
    }


    @Override
    public JavaFieldSignature getSignature() {
        if (signature == null) {
            signature = JavaFieldSignature.buildFor(this);
        }

        return signature;
    }


    /**
     * Returns an iterator over the ids of the fields
     * declared in this statement.
     */
    @Override
    public Iterator<ASTVariableDeclaratorId> iterator() {
        return ASTVariableDeclarator.iterateIds(this);
    }


    /**
     * @deprecated FieldDeclaration may declare several variables with a different type
     *     It won't implement TypeNode anymore come 7.0.0
     */
    @Override
    @Deprecated
    public Class<?> getType() {
        return super.getType();
    }


    /**
     * @deprecated FieldDeclaration may declare several variables with a different type
     *     It won't implement TypeNode anymore come 7.0.0
     */
    @Override
    @Deprecated
    public JavaTypeDefinition getTypeDefinition() {
        return super.getTypeDefinition();
    }
}
