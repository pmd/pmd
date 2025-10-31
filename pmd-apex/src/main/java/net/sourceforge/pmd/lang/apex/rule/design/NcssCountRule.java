/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ASTUserTrigger;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetrics;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;
import net.sourceforge.pmd.util.AssertionUtil;


/**
 * Simple rule for Ncss.
 */
public final class NcssCountRule extends AbstractApexRule {


    private static final PropertyDescriptor<Integer> METHOD_REPORT_LEVEL_DESCRIPTOR =
        PropertyFactory.intProperty("methodReportLevel")
                       .desc("NCSS reporting threshold for methods")
                       .require(positive())
                       .defaultValue(60)
                       .build();

    private static final PropertyDescriptor<Integer> CLASS_REPORT_LEVEL_DESCRIPTOR =
        PropertyFactory.intProperty("classReportLevel")
                       .desc("NCSS reporting threshold for classes")
                       .require(positive())
                       .defaultValue(1500)
                       .build();

    public NcssCountRule() {
        definePropertyDescriptor(METHOD_REPORT_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(CLASS_REPORT_LEVEL_DESCRIPTOR);
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTUserClassOrInterface.class, ASTMethod.class);
    }

    @Override
    public Object visitApexNode(ApexNode<?> node, Object data) {
        int methodReportLevel = getProperty(METHOD_REPORT_LEVEL_DESCRIPTOR);
        int classReportLevel = getProperty(CLASS_REPORT_LEVEL_DESCRIPTOR);

        if (node instanceof ASTUserClassOrInterface) {
            visitTypeDecl((ASTUserClassOrInterface<?>) node, classReportLevel, (RuleContext) data);
        } else if (node instanceof ASTMethod) {
            visitMethod((ASTMethod) node, methodReportLevel, (RuleContext) data);
        } else {
            throw AssertionUtil.shouldNotReachHere("node is not handled: " + node);
        }
        return data;
    }


    private void visitTypeDecl(ASTUserClassOrInterface<?> node,
                               int level,
                               RuleContext data) {

        if (ApexMetrics.NCSS.supports(node)) {
            int classSize = MetricsUtil.computeMetric(ApexMetrics.NCSS, node);
            int classHighest = (int) MetricsUtil.computeStatistics(ApexMetrics.NCSS, node.getMethods()).getMax();

            if (classSize >= level) {
                String[] messageParams = {getPrintableNodeKind(node),
                                          node.getSimpleName(),
                                          classSize + " (Highest = " + classHighest + ")", };

                asCtx(data).addViolation(node, (Object[]) messageParams);
            }
        }
    }

    private static String getPrintableNodeKind(ASTUserClassOrInterface<?> node) {
        if (node instanceof ASTUserClass) {
            return "class";
        } else if (node instanceof ASTUserInterface) {
            return "interface";
        } else if (node instanceof ASTUserEnum) {
            return "enum";
        } else if (node instanceof ASTUserTrigger) {
            return "trigger";
        }
        throw new IllegalStateException("unknown type: " + node.getClass().getName());
    }


    private void visitMethod(ASTMethod node,
                             int level,
                             RuleContext data) {

        if (ApexMetrics.NCSS.supports(node)) {
            int methodSize = MetricsUtil.computeMetric(ApexMetrics.NCSS, node);
            if (methodSize >= level) {
                asCtx(data).addViolation(node, node.isConstructor() ? "constructor" : "method",
                                         displaySignature(node), "" + methodSize);
            }
        }
    }

    private String displaySignature(ASTMethod node) {
        StringBuilder sb = new StringBuilder(node.getCanonicalName()).append('(');
        if (node.getArity() > 0) {
            sb.append(node.children(ASTParameter.class).toStream().map(ASTParameter::getType)
                    .collect(Collectors.joining(",")));
        }
        sb.append(')');
        return sb.toString();
    }

}
