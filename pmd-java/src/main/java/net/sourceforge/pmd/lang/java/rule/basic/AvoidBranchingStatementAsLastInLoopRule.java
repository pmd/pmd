/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.basic;

import net.sourceforge.pmd.PropertySource;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedMultiProperty;

public class AvoidBranchingStatementAsLastInLoopRule extends AbstractJavaRule {

    public static final String CHECK_FOR = "for";
    public static final String CHECK_DO = "do";
    public static final String CHECK_WHILE = "while";

    private static final String[] ALL_LOOP_TYPES_LABELS = new String[] { CHECK_FOR, CHECK_DO, CHECK_WHILE };
    private static final String[] ALL_LOOP_TYPES_VALUES = ALL_LOOP_TYPES_LABELS;
    private static final int[] ALL_LOOP_TYPES_DEFAULTS = new int[] { 0, 1, 2 };

    public static final EnumeratedMultiProperty<String> CHECK_BREAK_LOOP_TYPES = new EnumeratedMultiProperty(
	    "checkBreakLoopTypes", "Check for break statements in loop types", ALL_LOOP_TYPES_LABELS,
	    ALL_LOOP_TYPES_VALUES, ALL_LOOP_TYPES_DEFAULTS, 1);
    public static final EnumeratedMultiProperty<String> CHECK_CONTINUE_LOOP_TYPES = new EnumeratedMultiProperty(
	    "checkContinueLoopTypes", "Check for continue statements in loop types", ALL_LOOP_TYPES_LABELS,
	    ALL_LOOP_TYPES_VALUES, ALL_LOOP_TYPES_DEFAULTS, 2);
    public static final EnumeratedMultiProperty<String> CHECK_RETURN_LOOP_TYPES = new EnumeratedMultiProperty(
	    "checkReturnLoopTypes", "Check for return statements in loop types", ALL_LOOP_TYPES_LABELS,
	    ALL_LOOP_TYPES_VALUES, ALL_LOOP_TYPES_DEFAULTS, 3);

    public AvoidBranchingStatementAsLastInLoopRule() {
	definePropertyDescriptor(CHECK_BREAK_LOOP_TYPES);
	definePropertyDescriptor(CHECK_CONTINUE_LOOP_TYPES);
	definePropertyDescriptor(CHECK_RETURN_LOOP_TYPES);

	addRuleChainVisit(ASTBreakStatement.class);
	addRuleChainVisit(ASTContinueStatement.class);
	addRuleChainVisit(ASTReturnStatement.class);
    }

    @Override
    public Object visit(ASTBreakStatement node, Object data) {
        // skip breaks, that are within a switch statement
        if (node.getNthParent(3) instanceof ASTSwitchStatement) {
            return data;
        }
	return check(CHECK_BREAK_LOOP_TYPES, node, data);
    }

    @Override
    public Object visit(ASTContinueStatement node, Object data) {
	return check(CHECK_CONTINUE_LOOP_TYPES, node, data);
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
	return check(CHECK_RETURN_LOOP_TYPES, node, data);
    }

    protected Object check(EnumeratedMultiProperty<String> property, Node node, Object data) {
	Node parent = node.getNthParent(5);
	if (parent instanceof ASTForStatement) {
	    if (hasPropertyValue(property, CHECK_FOR)) {
		super.addViolation(data, node);
	    }
	} else if (parent instanceof ASTWhileStatement) {
	    if (hasPropertyValue(property, CHECK_WHILE)) {
		super.addViolation(data, node);
	    }
	} else if (parent instanceof ASTDoStatement) {
	    if (hasPropertyValue(property, CHECK_DO)) {
		super.addViolation(data, node);
	    }
	}
	return data;
    }

    protected boolean hasPropertyValue(EnumeratedMultiProperty<String> property, String value) {
	final Object[] values = getProperty(property);
	for (int i = 0; i < values.length; i++) {
	    if (value.equals(values[i])) {
		return true;
	    }
	}
	return false;
    }

	public boolean checksNothing() {

		return getProperty(CHECK_BREAK_LOOP_TYPES).length == 0 &&
			getProperty(CHECK_CONTINUE_LOOP_TYPES).length == 0 &&
			getProperty(CHECK_RETURN_LOOP_TYPES).length == 0 ;
	}

	/**
	 * @see PropertySource#dysfunctionReason()
	 */
	@Override
	public String dysfunctionReason() {
		return checksNothing() ?
				"All loop types are ignored" :
				null;
	}
}
