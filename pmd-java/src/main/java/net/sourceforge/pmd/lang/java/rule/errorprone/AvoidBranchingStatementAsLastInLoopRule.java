/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyClause;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.util.StringUtil;

public class AvoidBranchingStatementAsLastInLoopRule extends AbstractJavaRulechainRule {

    /**
     * @since 7.19.0. Should have never been public.
     */
    @Deprecated

    public static final String CHECK_FOR = "for";
    /**
     * @since 7.19.0. Should have never been public.
     */
    @Deprecated

    public static final String CHECK_DO = "do";
    /**
     * @since 7.19.0. Should have never been public.
     */
    @Deprecated
    public static final String CHECK_WHILE = "while";

    private static final List<LoopTypes> DEFAULTS = Arrays.asList(LoopTypes.FOR, LoopTypes.DO, LoopTypes.WHILE);

    private enum LoopTypes {
        FOR, DO, WHILE;
    }

    // TODO I don't think we need this configurability.
    // I think we should tone that down to just be able to ignore some type of statement,
    // but I can't see a use case to e.g. report only breaks in 'for' loops but not in 'while'.

    /**
     * @deprecated Since 7.19.0. Should have been private.
     */
    @Deprecated
    public static final PropertyDescriptor<List<String>> CHECK_BREAK_LOOP_TYPES = propertyFor("break");

    /**
     * @deprecated Since 7.19.0. Should have been private.
     */
    @Deprecated
    public static final PropertyDescriptor<List<String>> CHECK_CONTINUE_LOOP_TYPES = propertyFor("continue");

    /**
     * @deprecated Since 7.19.0. Should have been private.
     */
    @Deprecated
    public static final PropertyDescriptor<List<String>> CHECK_RETURN_LOOP_TYPES = propertyFor("return");

    private static final PropertyDescriptor<List<LoopTypes>> CHECK_BREAK_LOOP_TYPES_NEW = propertyForNew("break");
    private static final PropertyDescriptor<List<LoopTypes>> CHECK_CONTINUE_LOOP_TYPES_NEW = propertyForNew("continue");
    private static final PropertyDescriptor<List<LoopTypes>> CHECK_RETURN_LOOP_TYPES_NEW = propertyForNew("return");



    public AvoidBranchingStatementAsLastInLoopRule() {
        super(ASTBreakStatement.class, ASTContinueStatement.class, ASTReturnStatement.class);
        definePropertyDescriptor(CHECK_BREAK_LOOP_TYPES_NEW);
        definePropertyDescriptor(CHECK_CONTINUE_LOOP_TYPES_NEW);
        definePropertyDescriptor(CHECK_RETURN_LOOP_TYPES_NEW);
    }


    @Override
    public Object visit(ASTBreakStatement node, Object data) {
        // skip breaks, that are within a switch statement
        if (node.ancestors().get(1) instanceof ASTSwitchStatement) {
            return data;
        }
        return check(CHECK_BREAK_LOOP_TYPES_NEW, node, data);
    }


    /**
     * @deprecated Since 7.19.0. Should have been private.
     */
    @Deprecated
    protected Object check(PropertyDescriptor<List<LoopTypes>> property, Node node, Object data) {
        return checkInternal(property, node, data);
    }

    private Object checkInternal(PropertyDescriptor<List<LoopTypes>> property, Node node, Object data) {
        Node parent = node.getParent();
        if (parent instanceof ASTBlock) {
            parent = parent.getParent();
            if (parent instanceof ASTFinallyClause) {
                // get the parent of the block, in which the try statement is: ForStatement/Block/TryStatement/Finally
                // e.g. a ForStatement
                parent = ((ASTFinallyClause) parent).ancestors().get(2);
            }
        }
        if (parent instanceof ASTForStatement || parent instanceof ASTForeachStatement) {
            if (hasPropertyValue(property, LoopTypes.FOR)) {
                asCtx(data).addViolation(node);
            }
        } else if (parent instanceof ASTWhileStatement) {
            if (hasPropertyValue(property, LoopTypes.WHILE)) {
                asCtx(data).addViolation(node);
            }
        } else if (parent instanceof ASTDoStatement) {
            if (hasPropertyValue(property, LoopTypes.DO)) {
                asCtx(data).addViolation(node);
            }
        }
        return data;
    }


    /**
     * @deprecated Since 7.19.0. Should have been private.
     */
    @Deprecated
    protected boolean hasPropertyValue(PropertyDescriptor<List<String>> property, String value) {
        return getProperty(property).contains(value);
    }

    private boolean hasPropertyValue(PropertyDescriptor<List<LoopTypes>> property, LoopTypes value) {
        return getProperty(property).contains(value);
    }


    @Override
    public Object visit(ASTContinueStatement node, Object data) {
        return check(CHECK_CONTINUE_LOOP_TYPES_NEW, node, data);
    }


    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        return check(CHECK_RETURN_LOOP_TYPES_NEW, node, data);
    }


    @Override
    public String dysfunctionReason() {
        return checksNothing() ? "All loop types are ignored" : null;
    }

    @Deprecated
    private static PropertyDescriptor<List<String>> propertyFor(String stmtName) {
        return PropertyFactory.stringListProperty("check" + StringUtils.capitalize(stmtName) + "LoopTypes")
                .desc("List of loop types in which " + stmtName + " statements will be checked")
                .defaultValue(DEFAULTS.stream()
                        .map(v -> StringUtil.CaseConvention.SCREAMING_SNAKE_CASE.convertTo(StringUtil.CaseConvention.CAMEL_CASE, v.name()))
                        .collect(Collectors.toList()))
                .build();
    }

    private static PropertyDescriptor<List<LoopTypes>> propertyForNew(String stmtName) {
        return PropertyFactory.enumListPropertyNew("check" + StringUtils.capitalize(stmtName) + "LoopTypes", LoopTypes.class)
                .desc("List of loop types in which " + stmtName + " statements will be checked")
                .defaultValue(DEFAULTS)
                .build();
    }

    /**
     * @deprecated Since 7.19.0. Should have been private.
     */
    @Deprecated
    public boolean checksNothing() {
        return checksNothingInternal();
    }

    private boolean checksNothingInternal() {
        return getProperty(CHECK_BREAK_LOOP_TYPES_NEW).isEmpty()
                && getProperty(CHECK_CONTINUE_LOOP_TYPES_NEW).isEmpty()
            && getProperty(CHECK_RETURN_LOOP_TYPES_NEW).isEmpty();
    }
}
