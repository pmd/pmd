/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import net.sourceforge.pmd.lang.java.ast.ASTNumericLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;


public class AvoidUsingOctalValuesRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Boolean> STRICT_METHODS_DESCRIPTOR =
        booleanProperty("strict")
            .desc("Detect violations between 00 and 07")
            .defaultValue(false)
            .build();

    public AvoidUsingOctalValuesRule() {
        super(ASTNumericLiteral.class);
        definePropertyDescriptor(STRICT_METHODS_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTNumericLiteral node, Object data) {
        if (node.getBase() == 8) {
            if (getProperty(STRICT_METHODS_DESCRIPTOR) || !isBetweenZeroAnd7(node)) {
                addViolation(data, node);
            }
        }
        return null;
    }

    private boolean isBetweenZeroAnd7(ASTNumericLiteral node) {
        long value = node.getConstValue().longValue();
        return 0 <= value && value <= 7;
    }
}
