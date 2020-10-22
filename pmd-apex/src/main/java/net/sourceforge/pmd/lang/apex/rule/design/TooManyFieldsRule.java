/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class TooManyFieldsRule extends AbstractApexRule {

    private static final int DEFAULT_MAXFIELDS = 15;

    private static final PropertyDescriptor<Integer> MAX_FIELDS_DESCRIPTOR
            = PropertyFactory.intProperty("maxfields")
                             .desc("Max allowable fields")
                             .defaultValue(DEFAULT_MAXFIELDS)
                             .require(positive())
                             .build();


    public TooManyFieldsRule() {
        definePropertyDescriptor(MAX_FIELDS_DESCRIPTOR);
    }


    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTUserClass.class);
    }


    @Override
    public Object visit(ASTUserClass node, Object data) {

        List<ASTField> fields = node.findChildrenOfType(ASTField.class);

        int val = 0;
        for (ASTField field : fields) {
            if (field.getModifiers().isFinal() && field.getModifiers().isStatic()) {
                continue;
            }
            val++;
        }
        if (val > getProperty(MAX_FIELDS_DESCRIPTOR)) {
            addViolation(data, node);
        }
        return data;
    }

}
