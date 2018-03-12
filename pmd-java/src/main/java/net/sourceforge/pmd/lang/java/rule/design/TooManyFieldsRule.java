/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.IntegerProperty;

public class TooManyFieldsRule extends AbstractJavaRule {

    private static final int DEFAULT_MAXFIELDS = 15;

    private static final IntegerProperty MAX_FIELDS_DESCRIPTOR = new IntegerProperty("maxfields",
            "Max allowable fields", 1, 300, DEFAULT_MAXFIELDS, 1.0f);

    public TooManyFieldsRule() {
        definePropertyDescriptor(MAX_FIELDS_DESCRIPTOR);
        addRuleChainVisit(ASTClassOrInterfaceDeclaration.class);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        final int maxFields = getProperty(MAX_FIELDS_DESCRIPTOR);
        int counter = 0;

        final List<ASTFieldDeclaration> l = node.findDescendantsOfType(ASTFieldDeclaration.class);

        for (ASTFieldDeclaration fd : l) {
            if (fd.isFinal() && fd.isStatic()) {
                continue;
            }
            counter++;
        }

        if (counter > maxFields) {
            addViolation(data, node);
        }
        
        return data;
    }
}
