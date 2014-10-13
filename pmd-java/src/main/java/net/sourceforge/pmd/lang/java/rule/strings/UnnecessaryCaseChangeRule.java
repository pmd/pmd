/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.strings;

import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.ast.Node;

public class UnnecessaryCaseChangeRule extends AbstractJavaRule {

    public Object visit(ASTPrimaryExpression exp, Object data) {
        int n = exp.jjtGetNumChildren();
        if (n < 4) {
            return data;
        }

        int first = getBadPrefixOrNull(exp, n);
        if (first == -1) {
            return data;
        }

        String second = getBadSuffixOrNull(exp, first + 2);
        if (second == null) {
            return data;
        }

        if (!(exp.jjtGetChild(first + 1) instanceof ASTPrimarySuffix)) {
            return data;
        }
        ASTPrimarySuffix methodCall = (ASTPrimarySuffix)exp.jjtGetChild(first + 1);
        if (!methodCall.isArguments() || methodCall.getArgumentCount() > 0) {
            return data;
        }

        addViolation(data, exp);
        return data;
    }

    private int getBadPrefixOrNull(ASTPrimaryExpression exp, int childrenCount) {
        // verify PrimaryPrefix/Name[ends-with(@Image, 'toUpperCase']
        for(int i = 0; i < childrenCount - 3; i++) {
            Node child = exp.jjtGetChild(i);
            String image;
            if (child instanceof ASTPrimaryPrefix) {
                if (child.jjtGetNumChildren() != 1 || !(child.jjtGetChild(0) instanceof ASTName)) {
                    continue;
                }
        
                ASTName name = (ASTName) child.jjtGetChild(0);
                image = name.getImage();
            } else if (child instanceof ASTPrimarySuffix) {
                image = ((ASTPrimarySuffix) child).getImage();
            } else {
                continue;
            }

            if (image == null || !(image.endsWith("toUpperCase") || image.endsWith("toLowerCase"))) {
                continue;
            } else {
                return i;
            }
        }
        return -1;
    }

    private String getBadSuffixOrNull(ASTPrimaryExpression exp, int equalsPosition) {
        // verify PrimarySuffix[@Image='equals']
        if (!(exp.jjtGetChild(equalsPosition) instanceof ASTPrimarySuffix)) {
            return null;
        }

        ASTPrimarySuffix suffix = (ASTPrimarySuffix) exp.jjtGetChild(equalsPosition);
        if (suffix.getImage() == null || !(suffix.hasImageEqualTo("equals") || suffix.hasImageEqualTo("equalsIgnoreCase"))) {
            return null;
        }
        return suffix.getImage();
    }

}
