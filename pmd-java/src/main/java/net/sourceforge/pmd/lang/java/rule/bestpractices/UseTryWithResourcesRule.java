/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.properties.PropertyFactory.stringListProperty;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyStatement;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public final class UseTryWithResourcesRule extends AbstractJavaRule {

    private static final PropertyDescriptor<List<String>> CLOSE_METHODS =
            stringListProperty("closeMethods")
                    .desc("Method names in finally block, which trigger this rule")
                    .defaultValues("close", "closeQuietly")
                    .delim(',')
                    .build();

    public UseTryWithResourcesRule() {
        addRuleChainVisit(ASTTryStatement.class);
        definePropertyDescriptor(CLOSE_METHODS);
    }

    @Override
    public Object visit(ASTTryStatement node, Object data) {
        ASTFinallyStatement finallyClause = node.getFinallyClause();
        if (finallyClause != null) {
            List<ASTName> methods = findCloseMethods(finallyClause.findDescendantsOfType(ASTName.class));
            for (ASTName method : methods) {
                TypeNode typeNode = getType(method);
                if (TypeTestUtil.isA(AutoCloseable.class, typeNode)) {
                    addViolation(data, node);
                    break; // only report the first closeable
                }
            }
        }
        return data;
    }

    private TypeNode getType(ASTName method) {
        ASTArguments arguments = method.getNthParent(2).getFirstDescendantOfType(ASTArguments.class);
        if (arguments.size() > 0) {
            return (ASTExpression) arguments.getChild(0).getChild(0);
        }

        return method;
    }

    private List<ASTName> findCloseMethods(List<ASTName> names) {
        List<ASTName> potentialCloses = new ArrayList<>();
        for (ASTName name : names) {
            String image = name.getImage();
            int lastDot = image.lastIndexOf('.');
            if (lastDot > -1) {
                image = image.substring(lastDot + 1);
            }
            if (getProperty(CLOSE_METHODS).contains(image)) {
                potentialCloses.add(name);
            }
        }
        return potentialCloses;
    }
}
