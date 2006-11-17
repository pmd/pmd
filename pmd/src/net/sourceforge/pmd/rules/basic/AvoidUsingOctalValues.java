package net.sourceforge.pmd.rules.basic;

import java.util.regex.Pattern;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTLiteral;

public class AvoidUsingOctalValues extends AbstractRule {

    public static final Pattern OCTAL_PATTERN = Pattern.compile("0[0-7]+");

    public Object visit(ASTLiteral node, Object data) {
        String img = node.getImage();
        if (img != null && OCTAL_PATTERN.matcher(img).matches()) {
            addViolation(data, node);
        }

        return data;
    }

}
