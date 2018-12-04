/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;


public class AvoidUsingOctalValuesRule extends AbstractJavaRule {

    public static final Pattern OCTAL_PATTERN = Pattern.compile("0[0-7]{2,}[lL]?");

    public static final Pattern STRICT_OCTAL_PATTERN = Pattern.compile("0[0-7]+[lL]?");

    private static final PropertyDescriptor<Boolean> STRICT_METHODS_DESCRIPTOR = booleanProperty("strict")
                                                                                    .desc("Detect violations between 00 and 07")
                                                                                    .defaultValue(false)
                                                                                    .build();


    public AvoidUsingOctalValuesRule() {
        definePropertyDescriptor(STRICT_METHODS_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTLiteral node, Object data) {
        boolean strict = getProperty(STRICT_METHODS_DESCRIPTOR);
        Pattern p = strict ? STRICT_OCTAL_PATTERN : OCTAL_PATTERN;

        String img = node.getImage();
        if (img != null && p.matcher(img).matches()) {
            addViolation(data, node);
        }

        return data;
    }
}
