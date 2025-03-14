/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.declaration.PropertyDeclaration;

public final class ASTProperty extends AbstractApexNode.Single<PropertyDeclaration> {

    /**
     * Prefix added to the property name to create an internal accessor name.
     */
    private static final String ACCESSOR_PREFIX = "__sfdc_";

    ASTProperty(PropertyDeclaration property) {
        super(property);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the property value's type name.
     *
     * This includes any type arguments.
     * If the type is a primitive, its case will be normalized.
     */
    public String getType() {
        return caseNormalizedTypeIfPrimitive(node.getType().asCodeString());
    }

    public ASTModifierNode getModifiers() {
        return firstChild(ASTModifierNode.class);
    }

    /**
     * Returns the internal accessor (getter/setter) name of an {@link ASTProperty}. The accessor name is the
     * constant {@link #ACCESSOR_PREFIX} prepended to the name of the property.
     */
    public static String formatAccessorName(ASTProperty property) {
        return ACCESSOR_PREFIX + property.node.getId().getString();
    }
}
