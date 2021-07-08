/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.stream.Collectors;

import apex.jorje.data.Identifier;
import apex.jorje.data.ast.TypeRef;
import apex.jorje.semantic.ast.compilation.UserClass;

public final class ASTUserClass extends BaseApexClass<UserClass> implements ASTUserClassOrInterface<UserClass> {


    ASTUserClass(UserClass userClass) {
        super(userClass);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getSuperClassName() {
        return node.getDefiningType().getCodeUnitDetails().getSuperTypeRef().map(TypeRef::getNames)
            .map(it -> it.stream().map(Identifier::getValue).collect(Collectors.joining(".")))
            .orElse("");
    }

    public List<String> getInterfaceNames() {
        return node.getDefiningType().getCodeUnitDetails().getInterfaceTypeRefs().stream()
                .map(TypeRef::getNames).map(it -> it.stream().map(Identifier::getValue).collect(Collectors.joining(".")))
                .collect(Collectors.toList());
    }
}
