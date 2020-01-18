/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.util.CollectionUtil;

public class UnnecessaryWrapperObjectCreationRule extends AbstractJavaRule {

    private static final Set<String> PREFIX_SET = CollectionUtil.asSet(new String[] { "Byte.valueOf", "Short.valueOf",
        "Integer.valueOf", "Long.valueOf", "Float.valueOf", "Double.valueOf", "Character.valueOf", });

    private static final Set<String> SUFFIX_SET = CollectionUtil.asSet(new String[] { "toString", "byteValue",
        "shortValue", "intValue", "longValue", "floatValue", "doubleValue", "charValue", });

    @Override
    public Object visit(ASTPrimaryPrefix node, Object data) {
        if (node.getNumChildren() == 0 || !(node.getChild(0) instanceof ASTName)) {
            return super.visit(node, data);
        }

        String image = ((ASTName) node.getChild(0)).getImage();
        if (image.startsWith("java.lang.")) {
            image = image.substring(10);
        }

        boolean checkBoolean = ((RuleContext) data).getLanguageVersion()
                .compareTo(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5")) >= 0;

        if (PREFIX_SET.contains(image) || checkBoolean && "Boolean.valueOf".equals(image)) {
            ASTPrimaryExpression parent = (ASTPrimaryExpression) node.getParent();
            if (parent.getNumChildren() >= 3) {
                Node n = parent.getChild(2);
                if (n instanceof ASTPrimarySuffix) {
                    ASTPrimarySuffix suffix = (ASTPrimarySuffix) n;
                    image = suffix.getImage();

                    if (SUFFIX_SET.contains(image) || checkBoolean && "booleanValue".equals(image)) {
                        super.addViolation(data, node);
                        return data;
                    }
                }
            }
        }
        return super.visit(node, data);
    }

}
