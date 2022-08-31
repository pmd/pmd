/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.declaration.PropertyDeclaration;

public class ASTProperty extends AbstractApexNode.Single<PropertyDeclaration> {

    /**
     * Prefix added to the property name to create an internal accessor name.
     */
    private static final String ACCESSOR_PREFIX = "__sfdc_";

    @Deprecated
    @InternalApi
    public ASTProperty(PropertyDeclaration property) {
        super(property);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getType() {
        return node.getType().asCodeString();
    }

    public ASTModifierNode getModifiers() {
        return getFirstChildOfType(ASTModifierNode.class);
    }

    /**
     * Returns the internal accessor (getter/setter) name of an {@link ASTProperty}. The accessor name is the
     * constant {@link #ACCESSOR_PREFIX} prepended to the name of the property.
     */
    public static String formatAccessorName(ASTProperty property) {
        return ACCESSOR_PREFIX + property.node.getId().getString();
    }
}
