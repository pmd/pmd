/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.ast.Node;
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
import net.sourceforge.pmd.properties.BooleanProperty;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * @author Donald A. Leckie,
 *
 * @version $Revision: 5956 $, $Date: 2008-04-04 04:59:25 -0500 (Fri, 04 Apr
 *          2008) $
 * @since January 14, 2003
 */
public class CyclomaticComplexityRule extends AbstractPLSQLRule {
    private static final Logger LOGGER = Logger.getLogger(CyclomaticComplexityRule.class.getName());
    private static final String CLASS_NAME = CyclomaticComplexityRule.class.getName();

    public static final PropertyDescriptor<Integer> REPORT_LEVEL_DESCRIPTOR
            = PropertyFactory.intProperty("reportLevel")
                             .desc("Cyclomatic Complexity reporting threshold")
                             .require(positive()).defaultValue(10).build();

    public static final BooleanProperty SHOW_CLASSES_COMPLEXITY_DESCRIPTOR = new BooleanProperty(
            "showClassesComplexity", "Add class average violations to the report", true, 2.0f);

    public static final BooleanProperty SHOW_METHODS_COMPLEXITY_DESCRIPTOR = new BooleanProperty(
            "showMethodsComplexity", "Add method average violations to the report", true, 3.0f);

    private int reportLevel;
    private boolean showClassesComplexity = true;
    private boolean showMethodsComplexity = true;

    private static class Entry {
        private Node node;
        private int decisionPoints = 1;
        public int highestDecisionPoints;
        public int methodCount;

        private Entry(Node node) {
            this.node = node;
        }

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

    private Stack<Entry> entryStack = new Stack<>();

    public CyclomaticComplexityRule() {
        definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        definePropertyDescriptor(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTInput node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTInput)");
        reportLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);
        showClassesComplexity = getProperty(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        showMethodsComplexity = getProperty(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
        super.visit(node, data);
        LOGGER.exiting(CLASS_NAME, "visit(ASTInput)");
        return data;
    }

    @Override
    public Object visit(ASTElsifClause node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTElsifClause)");
        int boolCompIf = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
        // If statement always has a complexity of at least 1
        boolCompIf++;

        entryStack.peek().bumpDecisionPoints(boolCompIf);
        super.visit(node, data);
        LOGGER.exiting(CLASS_NAME, "visit(ASTElsifClause)");
        return data;
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTIfClause)");
        int boolCompIf = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
        // If statement always has a complexity of at least 1
        boolCompIf++;

        entryStack.peek().bumpDecisionPoints(boolCompIf);
        LOGGER.exiting(CLASS_NAME, "visit(ASTIfClause)");
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTExceptionHandler node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTExceptionHandler)");
        entryStack.peek().bumpDecisionPoints();
        LOGGER.exiting(CLASS_NAME, "visit(ASTExceptionHandler)");
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTForStatement)");
        int boolCompFor = NPathComplexityRule
                .sumExpressionComplexity(node.getFirstDescendantOfType(ASTExpression.class));
        // For statement always has a complexity of at least 1
        boolCompFor++;

        entryStack.peek().bumpDecisionPoints(boolCompFor);
        super.visit(node, data);
        LOGGER.exiting(CLASS_NAME, "visit(ASTForStatement)");
        return data;
    }

    @Override
    public Object visit(ASTLoopStatement node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTLoopStatement)");
        int boolCompDo = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
        // Do statement always has a complexity of at least 1
        boolCompDo++;

        entryStack.peek().bumpDecisionPoints(boolCompDo);
        super.visit(node, data);
        LOGGER.exiting(CLASS_NAME, "visit(ASTLoopStatement)");
        return data;
    }

    @Override
    public Object visit(ASTCaseStatement node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTCaseStatement)");
        Entry entry = entryStack.peek();

        int boolCompSwitch = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
        entry.bumpDecisionPoints(boolCompSwitch);

        super.visit(node, data);
        LOGGER.exiting(CLASS_NAME, "visit(ASTCaseStatement)");
        return data;
    }

    @Override
    public Object visit(ASTCaseWhenClause node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTCaseWhenClause)");
        Entry entry = entryStack.peek();

        entry.bumpDecisionPoints();
        super.visit(node, data);
        LOGGER.exiting(CLASS_NAME, "visit(ASTCaseWhenClause)");
        return data;
    }

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTWhileStatement)");
        int boolCompWhile = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
        // While statement always has a complexity of at least 1
        boolCompWhile++;

        entryStack.peek().bumpDecisionPoints(boolCompWhile);
        super.visit(node, data);
        LOGGER.exiting(CLASS_NAME, "visit(ASTWhileStatement)");
        return data;
    }

    @Override
    public Object visit(ASTConditionalOrExpression node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTPackageSpecification node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTPackageSpecification)");
        // Treat Package Specification like an Interface
        LOGGER.exiting(CLASS_NAME, "visit(ASTPackageSpecification)");
        return data;
    }

    @Override
    public Object visit(ASTTypeSpecification node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTTypeSpecification)");
        // Treat Type Specification like an Interface
        LOGGER.exiting(CLASS_NAME, "visit(ASTTypeSpecification)");
        return data;
    }

    @Override
    public Object visit(ASTPackageBody node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTPackageBody)");

        entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry classEntry = entryStack.pop();
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("ASTPackageBody: ComplexityAverage==" + classEntry.getComplexityAverage()
                    + ", highestDecisionPoint=" + classEntry.highestDecisionPoints);
        }
        if (showClassesComplexity) {
            if (classEntry.getComplexityAverage() >= reportLevel || classEntry.highestDecisionPoints >= reportLevel) {
                addViolation(data, node, new String[] { "class", node.getImage(),
                    classEntry.getComplexityAverage() + " (Highest = " + classEntry.highestDecisionPoints + ')', });
            }
        }
        LOGGER.exiting(CLASS_NAME, "visit(ASTPackageBody)");
        return data;
    }

    @Override
    public Object visit(ASTTriggerUnit node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTTriggerUnit)");

        entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry classEntry = entryStack.pop();
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("ASTTriggerUnit: ComplexityAverage==" + classEntry.getComplexityAverage()
                    + ", highestDecisionPoint=" + classEntry.highestDecisionPoints);
        }
        if (showClassesComplexity) {
            if (classEntry.getComplexityAverage() >= reportLevel || classEntry.highestDecisionPoints >= reportLevel) {
                addViolation(data, node, new String[] { "class", node.getImage(),
                    classEntry.getComplexityAverage() + " (Highest = " + classEntry.highestDecisionPoints + ')', });
            }
        }
        LOGGER.exiting(CLASS_NAME, "visit(ASTTriggerUnit)");
        return data;
    }

    @Override
    public Object visit(ASTProgramUnit node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTProgramUnit)");
        entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry methodEntry = entryStack.pop();
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("ASTProgramUnit: ComplexityAverage==" + methodEntry.getComplexityAverage()
                    + ", highestDecisionPoint=" + methodEntry.highestDecisionPoints);
        }
        if (showMethodsComplexity) {
            // Entry methodEntry = entryStack.pop();
            int methodDecisionPoints = methodEntry.decisionPoints;
            // PackageBody (including Object Type Body)
            if (null != node.getFirstParentOfType(ASTPackageBody.class)
                    // Trigger of any form
                    || null != node.getFirstParentOfType(ASTTriggerUnit.class)
            // TODO || null != node.getFirstParentOfType(ASTProgramUnit.class)
            // //Another Procedure
            // TODO || null != node.getFirstParentOfType(ASTTypeMethod.class)
            // //Another Type method
            ) {
                /*
                 * TODO This does not cope with nested methods We need the
                 * outer most ASTPackageBody ASTTriggerUni ASTProgramUnit
                 * ASTTypeMethod
                 *
                 */
                Entry classEntry = entryStack.peek();
                classEntry.methodCount++;
                classEntry.bumpDecisionPoints(methodDecisionPoints);

                if (methodDecisionPoints > classEntry.highestDecisionPoints) {
                    classEntry.highestDecisionPoints = methodDecisionPoints;
                }
            }

            ASTMethodDeclarator methodDeclarator = null;
            for (int n = 0; n < node.getNumChildren(); n++) {
                Node childNode = node.getChild(n);
                if (childNode instanceof ASTMethodDeclarator) {
                    methodDeclarator = (ASTMethodDeclarator) childNode;
                    break;
                }
            }

            if (methodEntry.decisionPoints >= reportLevel) {
                addViolation(data, node,
                        new String[] { "method", methodDeclarator == null ? "" : methodDeclarator.getImage(),
                            String.valueOf(methodEntry.decisionPoints), });
            }
        }
        LOGGER.exiting(CLASS_NAME, "visit(ASTProgramUnit)");
        return data;
    }

    @Override
    public Object visit(ASTTypeMethod node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTTypeMethod)");
        entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry methodEntry = entryStack.pop();
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("ASTProgramUnit: ComplexityAverage==" + methodEntry.getComplexityAverage()
                    + ", highestDecisionPoint=" + methodEntry.highestDecisionPoints);
        }
        if (showMethodsComplexity) {
            // Entry methodEntry = entryStack.pop();
            int methodDecisionPoints = methodEntry.decisionPoints;
            // PAckageBody (including Object Type Body)
            if (null != node.getFirstParentOfType(ASTPackageBody.class)) {
                /*
                 * TODO This does not cope with nested methods We need the
                 * outer most ASTPackageBody
                 */
                Entry classEntry = entryStack.peek();
                classEntry.methodCount++;
                classEntry.bumpDecisionPoints(methodDecisionPoints);

                if (methodDecisionPoints > classEntry.highestDecisionPoints) {
                    classEntry.highestDecisionPoints = methodDecisionPoints;
                }
            }

            ASTMethodDeclarator methodDeclarator = null;
            for (int n = 0; n < node.getNumChildren(); n++) {
                Node childNode = node.getChild(n);
                if (childNode instanceof ASTMethodDeclarator) {
                    methodDeclarator = (ASTMethodDeclarator) childNode;
                    break;
                }
            }

            if (methodEntry.decisionPoints >= reportLevel) {
                addViolation(data, node,
                        new String[] { "method", methodDeclarator == null ? "" : methodDeclarator.getImage(),
                            String.valueOf(methodEntry.decisionPoints), });
            }
        }
        LOGGER.exiting(CLASS_NAME, "visit(ASTTypeMethod)");
        return data;
    }

    @Override
    public Object visit(ASTTriggerTimingPointSection node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTTriggerTimingPointSection)");
        entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry methodEntry = entryStack.pop();
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("ASTTriggerTimingPointSection: ComplexityAverage==" + methodEntry.getComplexityAverage()
                    + ", highestDecisionPoint=" + methodEntry.highestDecisionPoints);
        }
        if (showMethodsComplexity) {
            int methodDecisionPoints = methodEntry.decisionPoints;
            Entry classEntry = entryStack.peek();
            classEntry.methodCount++;
            classEntry.bumpDecisionPoints(methodDecisionPoints);

            if (methodDecisionPoints > classEntry.highestDecisionPoints) {
                classEntry.highestDecisionPoints = methodDecisionPoints;
            }

            ASTMethodDeclarator methodDeclarator = null;
            for (int n = 0; n < node.getNumChildren(); n++) {
                Node childNode = node.getChild(n);
                if (childNode instanceof ASTMethodDeclarator) {
                    methodDeclarator = (ASTMethodDeclarator) childNode;
                    break;
                }
            }

            if (methodEntry.decisionPoints >= reportLevel) {
                addViolation(data, node,
                        new String[] { "method", methodDeclarator == null ? "" : methodDeclarator.getImage(),
                            String.valueOf(methodEntry.decisionPoints), });
            }
        }
        LOGGER.exiting(CLASS_NAME, "visit(ASTTriggerTimingPointSection)");
        return data;
    }

}
