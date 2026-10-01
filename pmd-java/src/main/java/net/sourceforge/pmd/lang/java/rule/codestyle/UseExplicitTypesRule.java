/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTClassType;
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
        RuleContext ctx = (RuleContext) data;
        if (!node.isTypeInferred()) {
            return null;
        }

        if (getProperty(ALLOW_LITERALS) && node.children(ASTVariableDeclarator.class).descendants(ASTLiteral.class).nonEmpty()) {
            return null;
        }
        if (getProperty(ALLOW_CTORS) && node.children(ASTVariableDeclarator.class).children(ASTConstructorCall.class).nonEmpty()) {
            return null;
        }
        if (getProperty(ALLOW_CASTS) && node.children(ASTVariableDeclarator.class).children(ASTCastExpression.class).nonEmpty()) {
            return null;
        }
        if (getProperty(ALLOW_LOOP_VARIABLE) && node.getParent() instanceof ASTForeachStatement) {
            return null;
        }

        int requiredLongTypeNamesLength = getProperty(ALLOW_LONG_TYPE_NAMES);

        JTypeMirror typeMirror = node.getVarIds().first().getTypeMirror();
        String typeName = typeMirror.toString();

        // for ctor calls, just take the type verbatim
        ASTClassType ctorType = node.descendants(ASTVariableDeclarator.class)
                .children(ASTConstructorCall.class)
                .descendants(ASTClassType.class)
                .first();
        if (ctorType != null) {
            typeName = ctorType.getText().toString();
        }

        boolean allowLongTypeNames = requiredLongTypeNamesLength < Integer.MAX_VALUE;
        if (allowLongTypeNames && typeName.length() >= requiredLongTypeNamesLength) {
            return null;
        }

        if (allowLongTypeNames) {
            ctx.addViolationWithMessage(node, "The declared type ''{0}'' is not long enough (<{1}) to justify the use of var",
                    typeName, requiredLongTypeNamesLength);
        } else {
            ctx.addViolation(node);
        }

        return null;
    }
}
