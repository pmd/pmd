/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codesize;

import java.util.Stack;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.Metrics;
import net.sourceforge.pmd.lang.java.oom.Metrics.OperationMetricKey;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;

/**
 * Implements the standard cyclomatic complexity rule.
 *
 * <p>Standard rules: +1 for each decision point, including case statements but not
 * including boolean operators unlike CyclomaticComplexityRule.
 *
 * @author Alan Hohn, based on work by Donald A. Leckie
 * @version Revised June 12th, 2017 (Clément Fournier)
 * @see net.sourceforge.pmd.lang.java.oom.metrics.StdCycloMetric
 * @since June 18, 2014
 */
public class StdCyclomaticComplexityRule extends AbstractJavaRule {

    public static final IntegerProperty REPORT_LEVEL_DESCRIPTOR
        = new IntegerProperty("reportLevel",
                              "Cyclomatic Complexity reporting threshold",
                              1, 30, 10, 1.0f);

    public static final BooleanProperty SHOW_CLASSES_COMPLEXITY_DESCRIPTOR
        = new BooleanProperty("showClassesComplexity",
                              "Add class average violations to the report",
                              true, 2.0f);

    public static final BooleanProperty SHOW_METHODS_COMPLEXITY_DESCRIPTOR
        = new BooleanProperty("showMethodsComplexity",
                              "Add method average violations to the report",
                              true, 3.0f);

    protected int reportLevel;
    protected boolean showClassesComplexity = true;
    protected boolean showMethodsComplexity = true;
    Stack<ClassEntry> entryStack = new Stack<>();

    protected OperationMetricKey metricKey = OperationMetricKey.StdCYCLO;

    public StdCyclomaticComplexityRule() {
        definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        definePropertyDescriptor(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        reportLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);
        showClassesComplexity = getProperty(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        showMethodsComplexity = getProperty(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }

        entryStack.push(new ClassEntry());
        super.visit(node, data);
        ClassEntry classEntry = entryStack.pop();

        if (showClassesComplexity) {
            if (classEntry.getCycloAverage() >= reportLevel || classEntry.maxCyclo >= reportLevel) {
                addViolation(data, node, new String[]
                    {"class",
                     node.getImage(),
                     classEntry.getCycloAverage() + " (Highest = " + classEntry.maxCyclo + ')', });
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        entryStack.push(new ClassEntry());
        super.visit(node, data);
        ClassEntry classEntry = entryStack.pop();

        if (showClassesComplexity) {
            if (classEntry.getCycloAverage() >= reportLevel || classEntry.maxCyclo >= reportLevel) {
                addViolation(data, node, new String[]
                    {"class",
                     node.getImage(),
                     classEntry.getCycloAverage() + " (Highest = " + classEntry.maxCyclo + ')', });
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        return visit((ASTMethodOrConstructorDeclaration) node, data);
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        return visit((ASTMethodOrConstructorDeclaration) node, data);
    }


    public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
        if (!isSuppressed(node)) {
            ClassEntry classEntry = entryStack.peek();

            int cyclo = (int) Metrics.get(metricKey, node);
            classEntry.numMethods++;
            classEntry.totalCyclo += cyclo;
            if (cyclo > classEntry.maxCyclo) {
                classEntry.maxCyclo = cyclo;
            }

            if (showMethodsComplexity && cyclo >= reportLevel) {
                addViolation(data, node, new String[]
                    {node instanceof ASTMethodDeclaration ? "method" : "constructor",
                     node.getQualifiedName().getOperation(),
                     String.valueOf(cyclo), });
            }
        }
        return data;
    }

    protected static class ClassEntry {
        int numMethods = 0;
        int totalCyclo = 1;
        int maxCyclo = 0;

        int getCycloAverage() {
            return (double) numMethods == 0 ? 1 : (int) Math.rint((double) totalCyclo / (double) numMethods);
        }
    }
}
