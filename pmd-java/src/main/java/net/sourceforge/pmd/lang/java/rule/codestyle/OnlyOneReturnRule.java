/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.List;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class OnlyOneReturnRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<List<String>> IGNORED_METHOD_NAMES
            = PropertyFactory.stringListProperty("ignoredMethodNames")
                    .defaultValues("compareTo", "equals")
                    .desc("Method names that are ignored from checking for only one return.")
                    .build();

    public OnlyOneReturnRule() {
        super(ASTMethodDeclaration.class);
        definePropertyDescriptor(IGNORED_METHOD_NAMES);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.getBody() == null || isIgnoredMethod(node)) {
            return null;
        }

        NodeStream<ASTReturnStatement> returnsExceptLast =
            node.getBody().descendants(ASTReturnStatement.class).dropLast(1);

        for (ASTReturnStatement returnStmt : returnsExceptLast) {
            asCtx(data).addViolation(returnStmt);
        }
        return null;
    }

    private boolean isIgnoredMethod(ASTMethodDeclaration node) {
        return getProperty(IGNORED_METHOD_NAMES).contains(node.getName());
    }
}
