/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


public class TooManyFieldsRule extends AbstractJavaRule {

    private static final int DEFAULT_MAXFIELDS = 15;

    private static final PropertyDescriptor<Integer> MAX_FIELDS_DESCRIPTOR
            = PropertyFactory.intProperty("maxfields")
                             .desc("Max allowable fields")
                             .defaultValue(DEFAULT_MAXFIELDS)
                             .require(positive())
                             .build();

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
