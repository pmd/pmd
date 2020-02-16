/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.util.NumericConstants;

public class TooManyFieldsRule extends AbstractApexRule {

    private static final int DEFAULT_MAXFIELDS = 15;

    private Map<String, Integer> stats;
    private Map<String, ASTUserClass> nodes;

    private static final PropertyDescriptor<Integer> MAX_FIELDS_DESCRIPTOR
            = PropertyFactory.intProperty("maxfields")
                             .desc("Max allowable fields")
                             .defaultValue(DEFAULT_MAXFIELDS)
                             .require(positive())
                             .build();


    public TooManyFieldsRule() {
        definePropertyDescriptor(MAX_FIELDS_DESCRIPTOR);

        setProperty(CODECLIMATE_CATEGORIES, "Complexity");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 200);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {

        int maxFields = getProperty(MAX_FIELDS_DESCRIPTOR);

        stats = new HashMap<>(5);
        nodes = new HashMap<>(5);

        List<ASTField> fields = node.findDescendantsOfType(ASTField.class);

        for (ASTField field : fields) {
            if (field.getModifiers().isFinal() && field.getModifiers().isStatic()) {
                continue;
            }
            ASTUserClass clazz = field.getFirstParentOfType(ASTUserClass.class);
            if (clazz != null) {
                bumpCounterFor(clazz);
            }
        }
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            int val = entry.getValue();
            Node n = nodes.get(entry.getKey());
            if (val > maxFields) {
                addViolation(data, n);
            }
        }
        return data;
    }

    private void bumpCounterFor(ASTUserClass clazz) {
        String key = clazz.getImage();
        if (!stats.containsKey(key)) {
            stats.put(key, NumericConstants.ZERO);
            nodes.put(key, clazz);
        }
        Integer i = stats.get(key) + 1;
        stats.put(key, i);
    }
}
