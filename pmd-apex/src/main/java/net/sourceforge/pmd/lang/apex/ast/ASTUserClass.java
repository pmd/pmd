/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.stream.Collectors;

import com.google.summit.ast.TypeRef;
import com.google.summit.ast.declaration.ClassDeclaration;

public final class ASTUserClass extends BaseApexClass<ClassDeclaration> implements ASTUserClassOrInterface<ClassDeclaration> {

    ASTUserClass(ClassDeclaration userClass) {
        super(userClass);
    }

    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the name of the superclass of this class, or an empty string if there is none.
     *
     * The type name does NOT include type arguments.
     */
    public String getSuperClassName() {
        TypeRef extendsType = node.getExtendsType();
        if (extendsType != null) {
            return extendsType.asTypeErasedString();
        }
        return "";
    }

    /**
     * Returns a list of the names of the interfaces implemented by this class.
     *
     * The type names do NOT include type arguments. (This is tested.)
     */
    public List<String> getInterfaceNames() {
        return node.getImplementsTypes().stream()
            .map(TypeRef::asTypeErasedString)
            .collect(Collectors.toList());
    }
}
