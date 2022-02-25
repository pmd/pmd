/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import apex.jorje.semantic.ast.compilation.Compilation;

abstract class BaseApexClass<T extends Compilation> extends AbstractApexNode<T> implements ASTUserClassOrInterface<T> {

    private ApexQualifiedName qname;

    protected BaseApexClass(T node) {
        super(node);
    }

    @Override
    public boolean isFindBoundary() {
        return true;
    }

    /**
     * @deprecated Use {@link #getSimpleName()}
     */
    @Override
    @Deprecated
    @DeprecatedUntil700
    public String getImage() {
        return getSimpleName();
    }

    @Override
    public String getSimpleName() {
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
    void calculateLineNumbers(SourceCodePositioner positioner) {
        super.calculateLineNumbers(positioner);

        // when calculateLineNumbers is called, the root node (ASTApexFile) is not available yet
        if (getParent() == null) {
            // For top level classes, enums, interfaces, triggers, the end is the end of file.
            this.endLine = positioner.getLastLine();
            this.endColumn = positioner.getLastLineColumn();
        } else {
            // For nested classes, enums, interfaces, triggers, look for the position of the last child,
            // which has a real location
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

}
