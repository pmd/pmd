/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.lang.reflect.Field;

import apex.jorje.data.ast.Identifier;
import apex.jorje.semantic.ast.compilation.UserClass;

public class ASTUserClass extends ApexRootNode<UserClass> implements ASTUserClassOrInterface<UserClass> {

    private ApexQualifiedName qname;


    public ASTUserClass(UserClass userClass) {
        super(userClass);
    }


    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public String getImage() {
        try {
            Field field = node.getClass().getDeclaredField("name");
            field.setAccessible(true);
            Identifier name = (Identifier) field.get(node);
            return name.value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.getImage();
    }


    @Override
    public ApexQualifiedName getQualifiedName() {
        if (qname == null) {

            ASTUserClass parent = this.getFirstParentOfType(ASTUserClass.class);

            if (parent != null) {
                qname = ApexQualifiedName.ofNestedClass(parent.getQualifiedName(), this);
            } else {
                qname = ApexQualifiedName.ofOuterClass(this);
            }
        }

        return qname;
    }


    @Override
    public TypeKind getTypeKind() {
        return TypeKind.CLASS;
    }
}
