/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.rule;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.oom.Metrics;
import net.sourceforge.pmd.lang.java.oom.api.ClassMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.Metric;
import net.sourceforge.pmd.lang.java.oom.api.Metric.Version;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.ResultOption;
import net.sourceforge.pmd.lang.java.oom.metrics.CycloMetric;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;

/**
 * Refactored to use metrics.
 *
 * @author Clément Fournier
 */
public class CyclomaticComplexityRule extends AbstractJavaRule {

    public static final IntegerProperty REPORT_LEVEL_DESCRIPTOR = new IntegerProperty(
        "reportLevel", "Cyclomatic Complexity reporting threshold", 1, 30, 10, 1.0f);

    public static final BooleanProperty SHOW_CLASSES_COMPLEXITY_DESCRIPTOR = new BooleanProperty(
        "showClassesComplexity", "Add class average violations to the report", true, 2.0f);

    public static final BooleanProperty SHOW_METHODS_COMPLEXITY_DESCRIPTOR = new BooleanProperty(
        "showMethodsComplexity", "Add method average violations to the report", true, 3.0f);


    private static final String[] VERSION_LABELS = {"standard", "ignoreBooleanPaths"};

    private static final MetricVersion[] CYCLO_VERSIONS = {Metric.Version.STANDARD, CycloMetric.Version.IGNORE_BOOLEAN_PATHS};

    public static final EnumeratedProperty<MetricVersion> CYCLO_VERSION_DESCRIPTOR = new EnumeratedProperty<>(
        "cycloVersion", "Choose a variant of Cyclo or the standard",
        VERSION_LABELS, CYCLO_VERSIONS, 0, 3.0f);

    private int reportLevel;
    private boolean showClassesComplexity = true;
    private boolean showMethodsComplexity = true;
    private MetricVersion cycloVersion = Version.STANDARD;


    public CyclomaticComplexityRule() {
        definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        definePropertyDescriptor(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
        definePropertyDescriptor(CYCLO_VERSION_DESCRIPTOR);
    }


    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        reportLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);
        showClassesComplexity = getProperty(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        showMethodsComplexity = getProperty(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
        Object version = getProperty(CYCLO_VERSION_DESCRIPTOR);
        cycloVersion = version instanceof MetricVersion ? (MetricVersion) version : Version.STANDARD;

        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }

        super.visit(node, data);
        if (showClassesComplexity) {
            int classCyclo = (int) Metrics.get(ClassMetricKey.CYCLO, node, cycloVersion);
            int classHighest = (int) Metrics.get(OperationMetricKey.CYCLO, node, cycloVersion, ResultOption.HIGHEST);

            if (classCyclo >= reportLevel || classHighest >= reportLevel) {
                addViolation(data, node,
                             new String[] {"class", node.getImage(), classCyclo + " (Highest = " + classHighest + ')'});
            }
        }
        return data;
    }


    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        int cyclo = (int) Metrics.get(OperationMetricKey.CYCLO, node, cycloVersion);

        if (showMethodsComplexity && cyclo >= reportLevel) {
            addViolation(data, node, new String[] {"method", node.getQualifiedName().getOperation(), "" + cyclo});
        }
        return data;
    }


    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        int cyclo = (int) Metrics.get(OperationMetricKey.CYCLO, node);

        if (showMethodsComplexity && cyclo >= reportLevel) {
            addViolation(data, node, new String[] {"constructor", node.getQualifiedName().getOperation(), "" + cyclo});
        }

        return data;
    }
}
