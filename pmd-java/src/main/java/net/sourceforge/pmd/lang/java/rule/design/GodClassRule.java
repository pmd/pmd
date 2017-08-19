/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.JavaRuleViolation;
import net.sourceforge.pmd.util.StringUtil;

/**
 * The God Class Rule detects a the God Class design flaw using metrics. A god class does too many things, is very big
 * and complex. It should be split apart to be more object-oriented. The rule uses the detection strategy described in
 * [1]. The violations are reported against the entire class.
 *
 * [1] Lanza. Object-Oriented Metrics in Practice. Page 80.
 *
 * @since 5.0
 */
public class GodClassRule extends AbstractJavaRule {

    /**
     * Very high threshold for WMC (Weighted Method Count). See: Lanza. Object-Oriented Metrics in Practice. Page 16.
     */
    private static final int WMC_VERY_HIGH = 47;

    /**
     * Few means between 2 and 5. See: Lanza. Object-Oriented Metrics in Practice. Page 18.
     */
    private static final int FEW_THRESHOLD = 5;

    /**
     * One third is a low value. See: Lanza. Object-Oriented Metrics in Practice. Page 17.
     */
    private static final double TCC_THRESHOLD = 1.0 / 3.0;


    /** The Access To Foreign Data metric. */
    private int atfdCounter;


    /**
     * Base entry point for the visitor - the compilation unit (everything within one file). The metrics are
     * initialized. Then the other nodes are visited. Afterwards the metrics are evaluated against fixed thresholds.
     */
    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        int wmc = (int) JavaMetrics.get(JavaClassMetricKey.WMC, node);
        double tcc = JavaMetrics.get(JavaClassMetricKey.TCC, node);


        atfdCounter = 0;

        Object result = super.visit(node, data);


        // StringBuilder debug = new StringBuilder();
        // debug.append("Values for class ")
        // .append(node.getImage()).append(": ")
        // .append("WMC=").append(wmcCounter).append(", ")
        // .append("ATFD=").append(atfdCounter).append(", ")
        // .append("TCC=").append(tcc);
        // System.out.println(debug.toString());

        if (wmc >= WMC_VERY_HIGH && atfdCounter > FEW_THRESHOLD && tcc < TCC_THRESHOLD) {

            String sb = getMessage() + " (" + "WMC=" + wmc + ", " + "ATFD="
                + atfdCounter + ", " + "TCC=" + tcc + ')';

            RuleContext ctx = (RuleContext) data;
            ctx.getReport().addRuleViolation(new JavaRuleViolation(this, ctx, node, sb));
        }
        return result;
    }


    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {
        if (isForeignAttributeOrMethod(node)) {
            if (isAttributeAccess(node) || isMethodCall(node) && isForeignGetterSetterCall(node)) {
                atfdCounter++;
            }
        }
        return super.visit(node, data);
    }


    private boolean isForeignGetterSetterCall(ASTPrimaryExpression node) {

        String methodOrAttributeName = getMethodOrAttributeName(node);

        return methodOrAttributeName != null && StringUtil.startsWithAny(methodOrAttributeName, "get", "is", "set");
    }


    private boolean isMethodCall(ASTPrimaryExpression node) {
        boolean result = false;
        List<ASTPrimarySuffix> suffixes = node.findDescendantsOfType(ASTPrimarySuffix.class);
        if (suffixes.size() == 1) {
            result = suffixes.get(0).isArguments();
        }
        return result;
    }


    private boolean isForeignAttributeOrMethod(ASTPrimaryExpression node) {
        boolean result = false;
        String nameImage = getNameImage(node);

        if (nameImage != null && (!nameImage.contains(".") || nameImage.startsWith("this."))) {
            result = false;
        } else if (nameImage == null && node.getFirstDescendantOfType(ASTPrimaryPrefix.class).usesThisModifier()) {
            result = false;
        } else if (nameImage == null && node.hasDecendantOfAnyType(ASTLiteral.class, ASTAllocationExpression.class)) {
            result = false;
        } else {
            result = true;
        }

        return result;
    }


    private String getNameImage(ASTPrimaryExpression node) {
        ASTPrimaryPrefix prefix = node.getFirstDescendantOfType(ASTPrimaryPrefix.class);
        ASTName name = prefix.getFirstDescendantOfType(ASTName.class);

        String image = null;
        if (name != null) {
            image = name.getImage();
        }
        return image;
    }


    private String getMethodOrAttributeName(ASTPrimaryExpression node) {
        ASTPrimaryPrefix prefix = node.getFirstDescendantOfType(ASTPrimaryPrefix.class);
        ASTName name = prefix.getFirstDescendantOfType(ASTName.class);

        String methodOrAttributeName = null;

        if (name != null) {
            int dotIndex = name.getImage().indexOf(".");
            if (dotIndex > -1) {
                methodOrAttributeName = name.getImage().substring(dotIndex + 1);
            }
        }

        return methodOrAttributeName;
    }


    private boolean isAttributeAccess(ASTPrimaryExpression node) {
        return node.findDescendantsOfType(ASTPrimarySuffix.class).isEmpty();
    }


}
