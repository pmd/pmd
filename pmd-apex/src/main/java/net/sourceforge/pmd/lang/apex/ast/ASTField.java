/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.member.Field;
import net.sourceforge.pmd.Rule;

public class ASTField extends AbstractApexNode<Field> implements CanSuppressWarnings {

    public ASTField(Field field) {
        super(field);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getFieldInfo().getName();
    }

    public boolean hasSuppressWarningsAnnotationFor(Rule rule) {
    	for(ASTAnnotation a : findDescendantsOfType(ASTAnnotation.class)) {
            if (a.suppresses(rule)) {
                return true;
            }
        }
        return false;
    }
}