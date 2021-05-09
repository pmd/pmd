/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import apex.jorje.data.Identifier;
import apex.jorje.data.ast.TypeRef;
import apex.jorje.semantic.ast.compilation.UserInterface;

public final class ASTUserInterface extends BaseApexClass<UserInterface> implements ASTUserClassOrInterface<UserInterface> {

    ASTUserInterface(UserInterface userInterface) {
        super(userInterface);
    }

    @Override
    void calculateLineNumbers(SourceCodePositioner positioner) {
        super.calculateLineNumbers(positioner);

        // when calculateLineNumbers is called, the root node (ASTApexFile) is not available yet
        if (getParent() == null) {
            // For top level interfaces, the end is the end of file.
            this.endLine = positioner.getLastLine();
            this.endColumn = positioner.getLastLineColumn();
        } else {
            // For nested interfaces, look for the position of the last child, which has a real location
            for (int i = getNumChildren() - 1; i >= 0; i--) {
                ApexNode<?> child = getChild(i);
                if (child.hasRealLoc()) {
                    this.endLine = child.getEndLine();
                    this.endColumn = child.getEndColumn();
                    break;
                }
            }
        }
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
