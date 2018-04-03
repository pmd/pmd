/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.EnumeratedMultiProperty;
import net.sourceforge.pmd.properties.PropertySource;

public class AvoidBranchingStatementAsLastInLoopRule extends AbstractJavaRule {

    public static final String CHECK_FOR = "for";
    public static final String CHECK_DO = "do";
    public static final String CHECK_WHILE = "while";

    private static final Map<String, String> LOOP_TYPES_MAPPINGS;
    private static final List<String> DEFAULTS = Arrays.asList(CHECK_FOR, CHECK_DO, CHECK_WHILE);

    static {
        Map<String, String> mappings = new HashMap<>();
        mappings.put(CHECK_FOR, CHECK_FOR);
        mappings.put(CHECK_DO, CHECK_DO);
        mappings.put(CHECK_WHILE, CHECK_WHILE);
        LOOP_TYPES_MAPPINGS = Collections.unmodifiableMap(mappings);
    }

    public static final EnumeratedMultiProperty<String> CHECK_BREAK_LOOP_TYPES = new EnumeratedMultiProperty<>(
        "checkBreakLoopTypes", "Check for break statements in loop types", LOOP_TYPES_MAPPINGS, DEFAULTS,
        String.class, 1);
    public static final EnumeratedMultiProperty<String> CHECK_CONTINUE_LOOP_TYPES = new EnumeratedMultiProperty<>(
        "checkContinueLoopTypes", "Check for continue statements in loop types", LOOP_TYPES_MAPPINGS, DEFAULTS,
        String.class, 2);
    public static final EnumeratedMultiProperty<String> CHECK_RETURN_LOOP_TYPES = new EnumeratedMultiProperty<>(
        "checkReturnLoopTypes", "Check for return statements in loop types", LOOP_TYPES_MAPPINGS, DEFAULTS,
        String.class, 3);


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
        return getProperty(property).contains(value);
    }


    @Override
    public Object visit(ASTContinueStatement node, Object data) {
        return check(CHECK_CONTINUE_LOOP_TYPES, node, data);
    }


    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        return check(CHECK_RETURN_LOOP_TYPES, node, data);
    }


    /**
     * @see PropertySource#dysfunctionReason()
     */
    @Override
    public String dysfunctionReason() {
        return checksNothing() ? "All loop types are ignored" : null;
    }


    public boolean checksNothing() {

        return getProperty(CHECK_BREAK_LOOP_TYPES).isEmpty() && getProperty(CHECK_CONTINUE_LOOP_TYPES).isEmpty()
            && getProperty(CHECK_RETURN_LOOP_TYPES).isEmpty();
    }
}
