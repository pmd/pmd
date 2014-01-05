/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.optimizations;

import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.CollectionUtil;

public class UnnecessaryWrapperObjectCreationRule extends AbstractJavaRule {

    private static final Set<String> PREFIX_SET = CollectionUtil.asSet(new String[] {
        "Byte.valueOf",
        "Short.valueOf",
        "Integer.valueOf",
        "Long.valueOf",
        "Float.valueOf",
        "Double.valueOf",
        "Character.valueOf"
    });

    private static final Set<String> SUFFIX_SET = CollectionUtil.asSet(new String[] {
        "toString",
        "byteValue",
        "shortValue",
        "intValue",
        "longValue",
        "floatValue",
        "doubleValue",
        "charValue"
    });

    public Object visit(ASTPrimaryPrefix node, Object data) {
        if (node.jjtGetNumChildren() == 0 || !(node.jjtGetChild(0) instanceof ASTName)) {
            return super.visit(node, data);
        }

        String image = ((ASTName) node.jjtGetChild(0)).getImage();
        if (image.startsWith("java.lang.")) {
            image = image.substring(10);
        }

        boolean checkBoolean = ((RuleContext) data).getLanguageVersion().compareTo(LanguageVersion.JAVA_15) >= 0;

        if (PREFIX_SET.contains(image)||(checkBoolean && "Boolean.valueOf".equals(image))) {
            ASTPrimaryExpression parent = (ASTPrimaryExpression) node.jjtGetParent();
            if (parent.jjtGetNumChildren() >= 3) {
                Node n = parent.jjtGetChild(2);
                if (n instanceof ASTPrimarySuffix) {
                    ASTPrimarySuffix suffix = (ASTPrimarySuffix) n;
                    image = suffix.getImage();

                    if (SUFFIX_SET.contains(image)||(checkBoolean && "booleanValue".equals(image))) {
                        super.addViolation(data, node);
                        return data;
                    }
                }
            }
        }
        return super.visit(node, data);
    }

}
