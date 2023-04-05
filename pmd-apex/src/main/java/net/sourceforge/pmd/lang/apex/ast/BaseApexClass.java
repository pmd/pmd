/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.declaration.TypeDeclaration;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;

abstract class BaseApexClass<T extends TypeDeclaration> extends AbstractApexNode.Single<T> implements ASTUserClassOrInterface<T> {

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
        return node.getId().getString();
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


}
