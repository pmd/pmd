/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.getMethodLevelStatement;
import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.isGuardIf;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
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
    private static final PropertyDescriptor<Boolean> ALLOW_GUARD_IFS
            = PropertyFactory.booleanProperty("allowGuardIfs")
                    .defaultValue(false)
                    .desc("Are guard ifs allowed? Guard ifs are statements \"like if (cond()) return\" or \"if (cond()) throw exception\" at the beginning of a method.")
                    .build();

    public OnlyOneReturnRule() {
        super(ASTMethodDeclaration.class);
        definePropertyDescriptor(IGNORED_METHOD_NAMES);
        definePropertyDescriptor(ALLOW_GUARD_IFS);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.getBody() == null || isIgnoredMethod(node)) {
            return null;
        }

        List<ASTReturnStatement> returnsToViolate =
            node.getBody().descendants(ASTReturnStatement.class).dropLast(1).toList();

        if (getProperty(ALLOW_GUARD_IFS)) {
            List<ASTIfStatement> guardIfs = findGuardIfs(node.getBody());
            removeGuardIfs(returnsToViolate, guardIfs);
        }

        for (ASTReturnStatement returnStmt : returnsToViolate) {
            asCtx(data).addViolation(returnStmt);
        }
        return null;
    }

    private boolean isIgnoredMethod(ASTMethodDeclaration node) {
        return getProperty(IGNORED_METHOD_NAMES).contains(node.getName());
    }

    private List<ASTIfStatement> findGuardIfs(ASTBlock body) {
        return body.children()
                .takeWhile(node -> isGuardIf(node))
                .map(node -> (ASTIfStatement) node)
                .toList();
    }

    /**
     * removes return statements, if they are part of a guard if.
     */
    private void removeGuardIfs(List<ASTReturnStatement> returnsToViolate, List<ASTIfStatement> guardIfs) {
        while (!returnsToViolate.isEmpty()
                && guardIfs.contains(getMethodLevelStatement(returnsToViolate.get(0)))
        ) {
            returnsToViolate.remove(0);
        }
    }
}
