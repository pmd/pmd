/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTPackageBody;
import net.sourceforge.pmd.lang.plsql.ast.ASTPackageSpecification;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerTimingPointSection;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeMethod;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeSpecification;
import net.sourceforge.pmd.lang.plsql.ast.ExecutableCode;
import net.sourceforge.pmd.lang.plsql.ast.OracleObject;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import net.sourceforge.pmd.lang.plsql.rule.AbstractPLSQLRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.lang.rule.internal.CommonPropertyDescriptors;
import net.sourceforge.pmd.properties.PropertyDescriptor;


/**
 * Abstract class for rules counting the length of some node.
 *
 * @author Clément Fournier
 * @since 6.7.0
 */
// PACKAGE PRIVATE or move in internal package at most
abstract class AbstractCounterCheckRule<T extends PLSQLNode> extends AbstractPLSQLRule {

    private final Class<T> nodeType;

    private final PropertyDescriptor<Integer> reportLevel =
        CommonPropertyDescriptors.reportLevelProperty()
                                 .desc("Threshold above which a node is reported")
                                 .require(positive())
                                 .defaultValue(defaultReportLevel()).build();


    AbstractCounterCheckRule(Class<T> nodeType) {
        this.nodeType = nodeType;
        definePropertyDescriptor(reportLevel);
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        if (!(Modifier.isAbstract(nodeType.getModifiers()) || nodeType.isInterface())) {
            return RuleTargetSelector.forTypes(nodeType);
        }

        return RuleTargetSelector.forTypes(determineRulechainVisits(nodeType));
    }

    // FIXME find a generic way to add a rulechain visit on an abstract node type
    private Collection<Class<? extends Node>> determineRulechainVisits(Class<T> abstractNodeType) {
        Collection<Class<? extends Node>> classes = new HashSet<>();
        if (abstractNodeType == OracleObject.class) {
            classes.add(ASTPackageBody.class);
            classes.add(ASTPackageSpecification.class);
            classes.add(ASTProgramUnit.class);
            classes.add(ASTTriggerUnit.class);
            classes.add(ASTTypeSpecification.class);
        } else if (abstractNodeType == ExecutableCode.class) {
            classes.add(ASTMethodDeclaration.class);
            classes.add(ASTProgramUnit.class);
            classes.add(ASTTriggerTimingPointSection.class);
            classes.add(ASTTriggerUnit.class);
            classes.add(ASTTypeMethod.class);
        }
        return classes;
    }


    protected abstract int defaultReportLevel();


    /** Return true if the node should be ignored. */
    protected boolean isIgnored(T node) {
        return false;
    }

    protected Object[] getViolationParameters(T node, int metric) {
        return new Object[] {metric};
    }


    protected abstract int getMetric(T node);

    @Override
    public Object visitPlsqlNode(PLSQLNode node, Object data) {
        @SuppressWarnings("unchecked")
        T t = (T) node;
        // since we only visit this node, it's ok

        if (!isIgnored(t)) {
            int metric = getMetric(t);
            if (metric >= getProperty(reportLevel)) {
                asCtx(data).addViolation(node, getViolationParameters(t, metric));
            }
        }

        return data;
    }

    abstract static class AbstractLineLengthCheckRule<T extends PLSQLNode> extends AbstractCounterCheckRule<T> {

        AbstractLineLengthCheckRule(Class<T> nodeType) {
            super(nodeType);
        }


        @Override
        protected int getMetric(T node) {
            return node.getEndLine() - node.getBeginLine();
        }
    }


}
