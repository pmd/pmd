/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics.CycloOption;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * Cyclomatic complexity rule using metrics.
 *
 * @author Clément Fournier, based on work by Alan Hohn and Donald A. Leckie
 * @version 6.0.0
 */
public class CyclomaticComplexityRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Integer> CLASS_LEVEL_DESCRIPTOR
        = PropertyFactory.intProperty("classReportLevel")
                         .desc("Total class complexity reporting threshold")
                         .require(positive()).defaultValue(80).build();

    private static final PropertyDescriptor<Integer> METHOD_LEVEL_DESCRIPTOR
        = PropertyFactory.intProperty("methodReportLevel")
                         .desc("Cyclomatic complexity reporting threshold")
                         .require(positive()).defaultValue(10).build();

    private static final Map<String, CycloOption> OPTION_MAP;

    static {
        OPTION_MAP = new HashMap<>();
        OPTION_MAP.put(CycloOption.IGNORE_BOOLEAN_PATHS.valueName(), CycloOption.IGNORE_BOOLEAN_PATHS);
        OPTION_MAP.put(CycloOption.CONSIDER_ASSERT.valueName(), CycloOption.CONSIDER_ASSERT);
    }

    private static final PropertyDescriptor<List<CycloOption>> CYCLO_OPTIONS_DESCRIPTOR
            = PropertyFactory.enumListProperty("cycloOptions", OPTION_MAP)
                             .desc("Choose options for the computation of Cyclo")
                             .emptyDefaultValue()
                             .build();


    public CyclomaticComplexityRule() {
        super(ASTMethodOrConstructorDeclaration.class, ASTAnyTypeDeclaration.class);
        definePropertyDescriptor(CLASS_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(METHOD_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(CYCLO_OPTIONS_DESCRIPTOR);
    }


    @Override
    public Object visitJavaNode(JavaNode node, Object param) {
        if (node instanceof ASTAnyTypeDeclaration) {
            visitTypeDecl((ASTAnyTypeDeclaration) node, param);
        }
        return null;
    }

    public Object visitTypeDecl(ASTAnyTypeDeclaration node, Object data) {

        MetricOptions cycloOptions = MetricOptions.ofOptions(getProperty(CYCLO_OPTIONS_DESCRIPTOR));

        if (JavaMetrics.WEIGHED_METHOD_COUNT.supports(node)) {
            int classWmc = MetricsUtil.computeMetric(JavaMetrics.WEIGHED_METHOD_COUNT, node, cycloOptions);

            if (classWmc >= getProperty(CLASS_LEVEL_DESCRIPTOR)) {
                int classHighest = (int) MetricsUtil.computeStatistics(JavaMetrics.CYCLO, node.getOperations(), cycloOptions).getMax();

                String[] messageParams = {PrettyPrintingUtil.getPrintableNodeKind(node),
                                          node.getSimpleName(),
                                          " total",
                                          classWmc + " (highest " + classHighest + ")", };

                addViolation(data, node, messageParams);
            }
        }
        return data;
    }


    @Override
    public final Object visit(ASTMethodDeclaration node, Object data) {
        visitMethodLike(node, data);
        return super.visit(node, data);
    }

    @Override
    public final Object visit(ASTConstructorDeclaration node, Object data) {
        visitMethodLike(node, data);
        return super.visit(node, data);
    }

    private void visitMethodLike(ASTMethodOrConstructorDeclaration node, Object data) {
        MetricOptions cycloOptions = MetricOptions.ofOptions(getProperty(CYCLO_OPTIONS_DESCRIPTOR));

        if (JavaMetrics.CYCLO.supports(node)) {
            int cyclo = MetricsUtil.computeMetric(JavaMetrics.CYCLO, node, cycloOptions);
            if (cyclo >= getProperty(METHOD_LEVEL_DESCRIPTOR)) {


                String opname = PrettyPrintingUtil.displaySignature(node);

                String kindname = node instanceof ASTConstructorDeclaration ? "constructor" : "method";


                addViolation(data, node, new String[] {kindname,
                                                       opname,
                                                       "",
                                                       "" + cyclo, });
            }
        }
    }

}
