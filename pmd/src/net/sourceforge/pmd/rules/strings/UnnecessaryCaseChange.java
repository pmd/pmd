package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;

public class UnnecessaryCaseChange extends AbstractRule {

    public Object visit(ASTPrimaryExpression exp, Object data) {
        if (exp.jjtGetNumChildren() < 4) {
            return data;
        }

        String first = getBadPrefixOrNull(exp);
        if (first == null) {
            return data;
        }

        String second = getBadSuffixOrNull(exp);
        if (second == null) {
            return data;
        }

        if (!(exp.jjtGetChild(1) instanceof ASTPrimarySuffix)) {
            return data;
        }
        ASTPrimarySuffix methodCall = (ASTPrimarySuffix)exp.jjtGetChild(1);
        if (!methodCall.isArguments() || methodCall.getArgumentCount() > 0) {
            return data;
        }

        addViolation(data, exp);
        return data;
    }

    private String getBadPrefixOrNull(ASTPrimaryExpression exp) {
        // verify PrimaryPrefix/Name[ends-with(@Image, 'toUpperCase']
        if (!(exp.jjtGetChild(0) instanceof ASTPrimaryPrefix)) {
            return null;
        }

        ASTPrimaryPrefix prefix = (ASTPrimaryPrefix) exp.jjtGetChild(0);
        if (prefix.jjtGetNumChildren() != 1 || !(prefix.jjtGetChild(0) instanceof ASTName)) {
            return null;
        }

        ASTName name = (ASTName) prefix.jjtGetChild(0);
        if (name.getImage() == null || !(name.getImage().endsWith("toUpperCase") || name.getImage().endsWith("toLowerCase"))) {
            return null;
        }
        return name.getImage();
    }

    private String getBadSuffixOrNull(ASTPrimaryExpression exp) {
        // verify PrimarySuffix[@Image='equals']
        if (!(exp.jjtGetChild(2) instanceof ASTPrimarySuffix)) {
            return null;
        }

        ASTPrimarySuffix suffix = (ASTPrimarySuffix) exp.jjtGetChild(2);
        if (suffix.getImage() == null || !(suffix.hasImageEqualTo("equals") || suffix.hasImageEqualTo("equalsIgnoreCase"))) {
            return null;
        }
        return suffix.getImage();
    }

}
