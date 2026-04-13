/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.plsql.ast.ASTCaseStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTCaseWhenClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTElsifClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTExceptionHandler;
import net.sourceforge.pmd.lang.plsql.ast.ASTExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTForStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.plsql.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.plsql.ast.ASTPackageBody;
import net.sourceforge.pmd.lang.plsql.ast.ASTPackageSpecification;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerTimingPointSection;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeMethod;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeSpecification;
import net.sourceforge.pmd.lang.plsql.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.plsql.rule.AbstractPLSQLRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;


/**
 * @author Donald A. Leckie,
 *
 * @version $Revision: 5956 $, $Date: 2008-04-04 04:59:25 -0500 (Fri, 04 Apr
 *          2008) $
 * @since January 14, 2003
 */
public class CyclomaticComplexityRule extends AbstractPLSQLRule {
    private static final Logger LOG = LoggerFactory.getLogger(CyclomaticComplexityRule.class);

    public static final PropertyDescriptor<Integer> REPORT_LEVEL_DESCRIPTOR
            = PropertyFactory.intProperty("reportLevel")
                             .desc("Cyclomatic Complexity reporting threshold")
                             .require(positive()).defaultValue(10).build();

    public static final PropertyDescriptor<Boolean> SHOW_CLASSES_COMPLEXITY_DESCRIPTOR =
        PropertyFactory.booleanProperty("showClassesComplexity")
                       .desc("Add class average violations to the report")
                       .defaultValue(true).build();

    public static final PropertyDescriptor<Boolean> SHOW_METHODS_COMPLEXITY_DESCRIPTOR =
        PropertyFactory.booleanProperty("showMethodsComplexity")
                       .desc("Add method average violations to the report")
                       .defaultValue(true).build();

    private int reportLevel;
    private boolean showClassesComplexity = true;
    private boolean showMethodsComplexity = true;

    private static final class Entry {
        private int decisionPoints = 1;
        public int highestDecisionPoints;
        public int methodCount;

        public void bumpDecisionPoints() {
            decisionPoints++;
        }

        public void bumpDecisionPoints(int size) {
            decisionPoints += size;
        }

        public int getComplexityAverage() {
            return (double) methodCount == 0 ? 1 : (int) Math.rint((double) decisionPoints / (double) methodCount);
        }
    }

    private Deque<Entry> entryStack = new ArrayDeque<>();

    public CyclomaticComplexityRule() {
        definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        definePropertyDescriptor(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
    }

    @Override
    public RuleContext visit(ASTInput node, RuleContext data) {
        reportLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);
        showClassesComplexity = getProperty(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        showMethodsComplexity = getProperty(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
        super.visit(node, data);
        return data;
    }

    @Override
    public RuleContext visit(ASTElsifClause node, RuleContext data) {
        int boolCompIf = NPathComplexityRule.sumExpressionComplexity(node.firstChild(ASTExpression.class));
        // If statement always has a complexity of at least 1
        boolCompIf++;

        entryStack.peek().bumpDecisionPoints(boolCompIf);
        super.visit(node, data);
        return data;
    }

    @Override
    public RuleContext visit(ASTIfStatement node, RuleContext data) {
        int boolCompIf = NPathComplexityRule.sumExpressionComplexity(node.firstChild(ASTExpression.class));
        // If statement always has a complexity of at least 1
        boolCompIf++;

        entryStack.peek().bumpDecisionPoints(boolCompIf);
        super.visit(node, data);
        return data;
    }

    @Override
    public RuleContext visit(ASTExceptionHandler node, RuleContext data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public RuleContext visit(ASTForStatement node, RuleContext data) {
        int boolCompFor = NPathComplexityRule
                .sumExpressionComplexity(node.descendants(ASTExpression.class).first());
        // For statement always has a complexity of at least 1
        boolCompFor++;

        entryStack.peek().bumpDecisionPoints(boolCompFor);
        super.visit(node, data);
        return data;
    }

    @Override
    public RuleContext visit(ASTLoopStatement node, RuleContext data) {
        int boolCompDo = NPathComplexityRule.sumExpressionComplexity(node.firstChild(ASTExpression.class));
        // Do statement always has a complexity of at least 1
        boolCompDo++;

        entryStack.peek().bumpDecisionPoints(boolCompDo);
        super.visit(node, data);
        return data;
    }

    @Override
    public RuleContext visit(ASTCaseStatement node, RuleContext data) {
        Entry entry = entryStack.peek();

        int boolCompSwitch = NPathComplexityRule.sumExpressionComplexity(node.firstChild(ASTExpression.class));
        entry.bumpDecisionPoints(boolCompSwitch);

        super.visit(node, data);
        return data;
    }

    @Override
    public RuleContext visit(ASTCaseWhenClause node, RuleContext data) {
        Entry entry = entryStack.peek();

        entry.bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public RuleContext visit(ASTWhileStatement node, RuleContext data) {
        int boolCompWhile = NPathComplexityRule.sumExpressionComplexity(node.firstChild(ASTExpression.class));
        // While statement always has a complexity of at least 1
        boolCompWhile++;

        entryStack.peek().bumpDecisionPoints(boolCompWhile);
        super.visit(node, data);
        return data;
    }

    @Override
    public RuleContext visit(ASTConditionalOrExpression node, RuleContext data) {
        return data;
    }

    @Override
    public RuleContext visit(ASTPackageSpecification node, RuleContext data) {
        // Treat Package Specification like an Interface
        return data;
    }

    @Override
    public RuleContext visit(ASTTypeSpecification node, RuleContext data) {
        // Treat Type Specification like an Interface
        return data;
    }

    @Override
    public RuleContext visit(ASTPackageBody node, RuleContext data) {

        entryStack.push(new Entry());
        super.visit(node, data);
        Entry classEntry = entryStack.pop();
        LOG.debug("ASTPackageBody: ComplexityAverage=={}, highestDecisionPoint={}",
                classEntry.getComplexityAverage(), classEntry.highestDecisionPoints);
        if (showClassesComplexity) {
            if (classEntry.getComplexityAverage() >= reportLevel || classEntry.highestDecisionPoints >= reportLevel) {
                data.addViolation(node, "class", node.getImage(),
                                  classEntry.getComplexityAverage() + " (Highest = " + classEntry.highestDecisionPoints + ')');
            }
        }
        return data;
    }

    @Override
    public RuleContext visit(ASTTriggerUnit node, RuleContext data) {
        entryStack.push(new Entry());
        super.visit(node, data);
        Entry classEntry = entryStack.pop();
        LOG.debug("ASTTriggerUnit: ComplexityAverage=={}, highestDecisionPoint={}",
                classEntry.getComplexityAverage(),
                classEntry.highestDecisionPoints);
        if (showClassesComplexity) {
            if (classEntry.getComplexityAverage() >= reportLevel || classEntry.highestDecisionPoints >= reportLevel) {
                data.addViolation(node, "class", node.getImage(),
                                  classEntry.getComplexityAverage() + " (Highest = " + classEntry.highestDecisionPoints + ')');
            }
        }
        return data;
    }

    private void updateClassEntry(int methodDecisionPoints) {
        Entry classEntry = entryStack.peek();
        classEntry.methodCount++;
        classEntry.bumpDecisionPoints(methodDecisionPoints);

        if (methodDecisionPoints > classEntry.highestDecisionPoints) {
            classEntry.highestDecisionPoints = methodDecisionPoints;
        }
    }
    
    @Override
    public RuleContext visit(ASTProgramUnit node, RuleContext data) {
        entryStack.push(new Entry());
        super.visit(node, data);
        Entry methodEntry = entryStack.pop();
        LOG.debug("ASTProgramUnit: ComplexityAverage=={}, highestDecisionPoint={}",
                methodEntry.getComplexityAverage(),
                methodEntry.highestDecisionPoints);
        if (showMethodsComplexity) {
            // Entry methodEntry = entryStack.pop();
            int methodDecisionPoints = methodEntry.decisionPoints;
            // PackageBody (including Object Type Body)
            if (null != node.ancestors(ASTPackageBody.class).first()
                    // Trigger of any form
                    || null != node.ancestors(ASTTriggerUnit.class).first()
            // TODO || null != node.ancestors(ASTProgramUnit.class).first()
            // //Another Procedure
            // TODO || null != node.ancestors(ASTTypeMethod.class).first()
            // //Another Type method
            ) {
                /*
                 * TODO This does not cope with nested methods We need the
                 * outer most ASTPackageBody ASTTriggerUni ASTProgramUnit
                 * ASTTypeMethod
                 *
                 */
                updateClassEntry(methodDecisionPoints);
            }

            ASTMethodDeclarator methodDeclarator = node.firstChild(ASTMethodDeclarator.class);
            if (methodEntry.decisionPoints >= reportLevel) {
                data.addViolation(node,
                                  "method", methodDeclarator == null ? "" : methodDeclarator.getImage(),
                                  String.valueOf(methodEntry.decisionPoints));
            }
        }
        return data;
    }

    @Override
    public RuleContext visit(ASTTypeMethod node, RuleContext data) {
        entryStack.push(new Entry());
        super.visit(node, data);
        Entry methodEntry = entryStack.pop();
        LOG.debug("ASTProgramUnit: ComplexityAverage=={}, highestDecisionPoint={}",
                methodEntry.getComplexityAverage(),
                methodEntry.highestDecisionPoints);
        if (showMethodsComplexity) {
            // Entry methodEntry = entryStack.pop();
            int methodDecisionPoints = methodEntry.decisionPoints;
            // PAckageBody (including Object Type Body)
            if (null != node.ancestors(ASTPackageBody.class).first()) {
                /*
                 * TODO This does not cope with nested methods We need the
                 * outer most ASTPackageBody
                 */
                updateClassEntry(methodDecisionPoints);
            }

            ASTMethodDeclarator methodDeclarator = node.firstChild(ASTMethodDeclarator.class);
            if (methodEntry.decisionPoints >= reportLevel) {
                data.addViolation(node,
                                  "method", methodDeclarator == null ? "" : methodDeclarator.getImage(),
                                  String.valueOf(methodEntry.decisionPoints));
            }
        }
        return data;
    }

    @Override
    public RuleContext visit(ASTTriggerTimingPointSection node, RuleContext data) {
        entryStack.push(new Entry());
        super.visit(node, data);
        Entry methodEntry = entryStack.pop();
        LOG.debug("ASTTriggerTimingPointSection: ComplexityAverage=={}, highestDecisionPoint={}",
                methodEntry.getComplexityAverage(),
                methodEntry.highestDecisionPoints);
        if (showMethodsComplexity) {
            int methodDecisionPoints = methodEntry.decisionPoints;
            Entry classEntry = entryStack.peek();
            classEntry.methodCount++;
            classEntry.bumpDecisionPoints(methodDecisionPoints);

            if (methodDecisionPoints > classEntry.highestDecisionPoints) {
                classEntry.highestDecisionPoints = methodDecisionPoints;
            }

            ASTMethodDeclarator methodDeclarator = node.firstChild(ASTMethodDeclarator.class);
            if (methodEntry.decisionPoints >= reportLevel) {
                data.addViolation(node,
                                  "method", methodDeclarator == null ? "" : methodDeclarator.getImage(),
                                  String.valueOf(methodEntry.decisionPoints));
            }
        }
        return data;
    }
}
