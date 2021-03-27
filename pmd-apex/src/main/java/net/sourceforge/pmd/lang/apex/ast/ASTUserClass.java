/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import apex.jorje.data.Identifier;
import apex.jorje.data.ast.TypeRef;
import apex.jorje.semantic.ast.compilation.UserClass;

public final class ASTUserClass extends AbstractApexNode<UserClass> implements ASTUserClassOrInterface<UserClass> {

    private ApexQualifiedName qname;

    ASTUserClass(UserClass userClass) {
        super(userClass);
    }

    @Override
    void calculateLineNumbers(SourceCodePositioner positioner) {
        super.calculateLineNumbers(positioner);

        if (getParent() == getRoot()) {
            // For top level classes, the end is the end of file.
            this.endLine = positioner.getLastLine();
            this.endColumn = positioner.getLastLineColumn();
        } else {
            // For nested classes, look for the position of the last child, which has a real location
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


    @Override
    public String getImage() {
        String apexName = getDefiningType();
        return apexName.substring(apexName.lastIndexOf('.') + 1);
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


    public ASTModifierNode getModifiers() {
        return getFirstChildOfType(ASTModifierNode.class);
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
