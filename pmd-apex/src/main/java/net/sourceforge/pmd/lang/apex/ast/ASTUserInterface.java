/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.stream.Collectors;

import apex.jorje.data.Identifier;
import apex.jorje.data.ast.TypeRef;
import apex.jorje.semantic.ast.compilation.UserInterface;

public final class ASTUserInterface extends BaseApexClass<UserInterface> implements ASTUserClassOrInterface<UserInterface> {

    ASTUserInterface(UserInterface userInterface) {
        super(userInterface);
    }

    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    public String getSuperInterfaceName() {
        return node.getDefiningType().getCodeUnitDetails().getInterfaceTypeRefs().stream().map(TypeRef::getNames)
                .map(it -> it.stream().map(Identifier::getValue).collect(Collectors.joining(".")))
                .findFirst().orElse("");
    }
}
