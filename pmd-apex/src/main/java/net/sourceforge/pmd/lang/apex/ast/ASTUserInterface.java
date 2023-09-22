/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.TypeRef;
import com.google.summit.ast.declaration.InterfaceDeclaration;

public final class ASTUserInterface extends BaseApexClass<InterfaceDeclaration> implements ASTUserClassOrInterface<InterfaceDeclaration> {

    ASTUserInterface(InterfaceDeclaration userInterface) {
        super(userInterface);
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
    public String getSuperInterfaceName() {
        return node.getExtendsTypes().stream().map(TypeRef::asTypeErasedString).findFirst().orElse("");
    }
}
