/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * @since 7.0.0 (as XPath)
 * @since 7.28.0 (as Java rule)
 */
public class UseExplicitTypesRule extends AbstractJavaRulechainRule {
    private static final PropertyDescriptor<Boolean> ALLOW_LITERALS = PropertyFactory
            .booleanProperty("allowLiterals")
            .desc("Allow when variables are directly initialized with literals")
            .defaultValue(false)
            .build();
    private static final PropertyDescriptor<Boolean> ALLOW_CTORS = PropertyFactory
            .booleanProperty("allowCtors")
            .desc("Allow when variables are directly initialized with a constructor call")
            .defaultValue(false)
            .build();
    private static final PropertyDescriptor<Boolean> ALLOW_CASTS = PropertyFactory
            .booleanProperty("allowCasts")
            .desc("Allow when variables are directly initialized with a the result of a cast")
            .defaultValue(false)
            .build();
    private static final PropertyDescriptor<Boolean> ALLOW_LOOP_VARIABLE = PropertyFactory
            .booleanProperty("allowLoopVariable")
            .desc("Allow when variables are used as loop variables in enhanced for loops")
            .defaultValue(false)
            .build();
    private static final PropertyDescriptor<Integer> ALLOW_LONG_TYPE_NAMES = PropertyFactory
            .intProperty("allowLongTypeNames")
            .desc("Allow when the type name would be longer than ...")
            .defaultValue(Integer.MAX_VALUE)
            .build();

    public UseExplicitTypesRule() {
        super(ASTLocalVariableDeclaration.class);
        definePropertyDescriptor(ALLOW_LITERALS);
        definePropertyDescriptor(ALLOW_CTORS);
        definePropertyDescriptor(ALLOW_CASTS);
        definePropertyDescriptor(ALLOW_LOOP_VARIABLE);
        definePropertyDescriptor(ALLOW_LONG_TYPE_NAMES);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        if (!node.isTypeInferred()) {
            return null;
        }

        boolean flag = true;
        flag &= !getProperty(ALLOW_LITERALS) || node.children(ASTVariableDeclarator.class).descendants(ASTLiteral.class).isEmpty();
        flag &= !getProperty(ALLOW_CTORS) || node.children(ASTVariableDeclarator.class).children(ASTConstructorCall.class).isEmpty();
        flag &= !getProperty(ALLOW_CASTS) || node.children(ASTVariableDeclarator.class).children(ASTCastExpression.class).isEmpty();
        flag &= !getProperty(ALLOW_LOOP_VARIABLE) || !(node.getParent() instanceof ASTForeachStatement);

        JTypeMirror typeMirror = node.getVarIds().first().getTypeMirror();
        flag &= typeMirror.toString().length() < getProperty(ALLOW_LONG_TYPE_NAMES);

        if (flag) {
            RuleContext ruleContext = (RuleContext) data;
            ruleContext.addViolation(node);
        }

        return null;
    }
}
