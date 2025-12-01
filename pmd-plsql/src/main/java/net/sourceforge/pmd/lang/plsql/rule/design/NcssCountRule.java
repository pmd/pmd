/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.lang.plsql.ast.ASTDatatype;
import net.sourceforge.pmd.lang.plsql.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.plsql.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.plsql.ast.ASTGlobal;
import net.sourceforge.pmd.lang.plsql.ast.ASTID;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.ExecutableCode;
import net.sourceforge.pmd.lang.plsql.ast.OracleObject;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import net.sourceforge.pmd.lang.plsql.metrics.PlsqlMetrics;
import net.sourceforge.pmd.lang.plsql.rule.AbstractPLSQLRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;
import net.sourceforge.pmd.util.AssertionUtil;

public class NcssCountRule extends AbstractPLSQLRule {
    private static final PropertyDescriptor<Integer> METHOD_REPORT_LEVEL_DESCRIPTOR =
            PropertyFactory.intProperty("methodReportLevel")
                    .desc("NCSS reporting threshold for methods")
                    .require(positive())
                    .defaultValue(40)
                    .build();

    private static final PropertyDescriptor<Integer> OBJECT_REPORT_LEVEL_DESCRIPTOR =
            PropertyFactory.intProperty("objectReportLevel")
                    .desc("NCSS reporting threshold for objects")
                    .require(positive())
                    .defaultValue(500)
                    .build();

    private Set<PLSQLNode> alreadyVisitedProgramUnits = new HashSet<>();

    public NcssCountRule() {
        definePropertyDescriptor(METHOD_REPORT_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(OBJECT_REPORT_LEVEL_DESCRIPTOR);
    }

    @Override
    public void start(RuleContext ctx) {
        alreadyVisitedProgramUnits.clear();
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(OracleObject.class, ExecutableCode.class);
    }

    @Override
    public Object visitPlsqlNode(PLSQLNode node, Object data) {
        int methodReportLevel = getProperty(METHOD_REPORT_LEVEL_DESCRIPTOR);
        int objectReportLevel = getProperty(OBJECT_REPORT_LEVEL_DESCRIPTOR);

        // ProgramUnit is both ExecutableCode and OracleObject, make sure, we don't report it twice.
        if (node instanceof ASTProgramUnit && alreadyVisitedProgramUnits.contains(node)) {
            return data;
        }
        // Special handling for ProgramUnit to distinguish between Object and Method
        if (node instanceof ASTProgramUnit && !(node.getParent() instanceof ASTGlobal)) {
            visitMethod((ExecutableCode) node, methodReportLevel, (RuleContext) data);
            alreadyVisitedProgramUnits.add(node);
            return data;
        } else if (node instanceof ASTProgramUnit && node.getParent() instanceof ASTGlobal) {
            visitObject((OracleObject) node, objectReportLevel, (RuleContext) data);
            alreadyVisitedProgramUnits.add(node);
            return data;
        }

        if (node instanceof ExecutableCode) {
            visitMethod((ExecutableCode) node, methodReportLevel, (RuleContext) data);
        } else if (node instanceof OracleObject) {
            visitObject((OracleObject) node, objectReportLevel, (RuleContext) data);
        } else {
            throw AssertionUtil.shouldNotReachHere("node is not handled: " + node);
        }
        return data;
    }

    private void visitMethod(ExecutableCode node, int level, RuleContext ruleContext) {
        if (PlsqlMetrics.NCSS.supports(node)) {
            int methodSize = MetricsUtil.computeMetric(PlsqlMetrics.NCSS, node);
            if (methodSize >= level) {
                ruleContext.addViolation(node, node.getXPathNodeName(), displaySignature(node), methodSize, level);
            }
        }
    }

    private String displaySignature(ExecutableCode node) {
        StringBuilder sb = new StringBuilder(node.getMethodName());
        sb.append('(');
        sb.append(node.children(ASTMethodDeclarator.class)
                    .children(ASTFormalParameters.class)
                    .children(ASTFormalParameter.class)
                    .toStream()
                    .map(p -> {
                        String id = p.firstChild(ASTID.class).getImage();
                        String type = Optional.ofNullable(p.firstChild(ASTDatatype.class))
                                .map(ASTDatatype::getTypeImage)
                                .orElse("...");
                        return id + " " + type;
                    })
                    .collect(Collectors.joining(", ")));
        sb.append(')');
        return sb.toString();
    }

    private void visitObject(OracleObject node, int level, RuleContext ruleContext) {
        if (PlsqlMetrics.NCSS.supports(node)) {
            int objectSize = MetricsUtil.computeMetric(PlsqlMetrics.NCSS, node);
            int objectHighest = (int) MetricsUtil.computeStatistics(PlsqlMetrics.NCSS, node.descendants(ExecutableCode.class)).getMax();

            if (objectSize >= level) {
                ruleContext.addViolation(node, node.getXPathNodeName(), node.getObjectName(), objectSize, level + ", highest: " + objectHighest);
            }
        }
    }
}
