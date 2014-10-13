/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.JavaRuleViolation;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.SourceFileScope;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.util.StringUtil;

/**
 * The God Class Rule detects a the God Class design flaw using metrics. A god class does too many things,
 * is very big and complex. It should be split apart to be more object-oriented.
 * The rule uses the detection strategy described in [1]. The violations are reported
 * against the entire class.
 * 
 * [1] Lanza. Object-Oriented Metrics in Practice. Page 80.
 * 
 * @since 5.0
 */
public class GodClassRule extends AbstractJavaRule {

    /**
     * Very high threshold for WMC (Weighted Method Count).
     * See: Lanza. Object-Oriented Metrics in Practice. Page 16.
     */
    private static final int WMC_VERY_HIGH = 47;
    
    /**
     * Few means between 2 and 5.
     * See: Lanza. Object-Oriented Metrics in Practice. Page 18.
     */
    private static final int FEW_THRESHOLD = 5;
    
    /**
     * One third is a low value.
     * See: Lanza. Object-Oriented Metrics in Practice. Page 17.
     */
    private static final double ONE_THIRD_THRESHOLD = 1.0/3.0;
    
    /** The Weighted Method Count metric. */
    private int wmcCounter;
    /** The Access To Foreign Data metric. */
    private int atfdCounter;

    /** Collects for each method of the current class, which local attributes are accessed. */
    private Map<String, Set<String>> methodAttributeAccess;
    /** The name of the current method. */
    private String currentMethodName;
    
    
    /**
     * Base entry point for the visitor - the compilation unit (everything within one file).
     * The metrics are initialized. Then the other nodes are visited. Afterwards
     * the metrics are evaluated against fixed thresholds.
     */
    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        wmcCounter = 0;
        atfdCounter = 0;
        methodAttributeAccess = new HashMap<String, Set<String>>();
        
        Object result = super.visit(node, data);
        
        double tcc = calculateTcc();

//        StringBuilder debug = new StringBuilder();
//            debug.append("Values for class ")
//            .append(node.getImage()).append(": ")
//            .append("WMC=").append(wmcCounter).append(", ")
//            .append("ATFD=").append(atfdCounter).append(", ")
//            .append("TCC=").append(tcc);
//        System.out.println(debug.toString());

        if (wmcCounter >= WMC_VERY_HIGH
            && atfdCounter > FEW_THRESHOLD
            && tcc < ONE_THIRD_THRESHOLD) {

            StringBuilder sb = new StringBuilder();
            sb.append(getMessage());
            sb.append(" (")
                .append("WMC=").append(wmcCounter).append(", ")
                .append("ATFD=").append(atfdCounter).append(", ")
                .append("TCC=").append(tcc).append(')');
            
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(new JavaRuleViolation(this, ctx, node, sb.toString()));
        }
        return result;
    }

    /**
     * Calculates the Tight Class Cohesion metric.
     * @return a value between 0 and 1.
     */
    private double calculateTcc() {
        double tcc = 0.0;
        int methodPairs = determineMethodPairs();
        double totalMethodPairs = calculateTotalMethodPairs();
        if (totalMethodPairs > 0) {
            tcc = methodPairs / totalMethodPairs;
        }
        return tcc;
    }

    /**
     * Calculates the number of possible method pairs.
     * Its basically the sum of the first (methodCount - 1) integers.
     * It will be 0, if no methods exist or only one method, means, if no pairs exist.
     * @return
     */
    private double calculateTotalMethodPairs() {
        int methodCount = methodAttributeAccess.size();
        int n = methodCount - 1;
        double totalMethodPairs = n * (n + 1) / 2.0;
        return totalMethodPairs;
    }
    
    /**
     * Uses the {@link #methodAttributeAccess} map to detect method pairs, that use at least
     * one common attribute of the class.
     * @return
     */
    private int determineMethodPairs() {
        List<String> methods = new ArrayList<String>(methodAttributeAccess.keySet());
        int methodCount = methods.size();
        int pairs = 0;
        
        if (methodCount > 1) {
            for (int i = 0; i < methodCount; i++) {
                for (int j = i + 1; j < methodCount; j++) {
                    String firstMethodName = methods.get(i);
                    String secondMethodName = methods.get(j);
                    Set<String> accessesOfFirstMethod = methodAttributeAccess.get(firstMethodName);
                    Set<String> accessesOfSecondMethod = methodAttributeAccess.get(secondMethodName);
                    Set<String> combinedAccesses = new HashSet<String>();

                    combinedAccesses.addAll(accessesOfFirstMethod);
                    combinedAccesses.addAll(accessesOfSecondMethod);

                    if (combinedAccesses.size() < (accessesOfFirstMethod.size() + accessesOfSecondMethod.size())) {
                        pairs++;
                    }
                }
            }
        }
        return pairs;
    }


    /**
     * The primary expression node is used to detect access to attributes and method calls.
     * If the access is not for a foreign class, then the {@link #methodAttributeAccess} map is
     * updated for the current method.
     */
    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {
        if (isForeignAttributeOrMethod(node)) {
            if (isAttributeAccess(node)
                || (isMethodCall(node) && isForeignGetterSetterCall(node))) {
                atfdCounter++;
            }
        } else {
            if (currentMethodName != null) {
                Set<String> methodAccess = methodAttributeAccess.get(currentMethodName);
                String variableName = getVariableName(node);
                VariableNameDeclaration variableDeclaration = findVariableDeclaration(variableName, node.getScope().getEnclosingScope(ClassScope.class));
                if (variableDeclaration != null) {
                    methodAccess.add(variableName);
                }
            }
        }
        
        return super.visit(node, data);
    }


    private boolean isForeignGetterSetterCall(ASTPrimaryExpression node) {

        String methodOrAttributeName = getMethodOrAttributeName(node);
        
        return methodOrAttributeName != null && StringUtil.startsWithAny(methodOrAttributeName, "get","is","set");
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

    private String getVariableName(ASTPrimaryExpression node) {
        ASTPrimaryPrefix prefix = node.getFirstDescendantOfType(ASTPrimaryPrefix.class);
        ASTName name = prefix.getFirstDescendantOfType(ASTName.class);

        String variableName = null;
        
        if (name != null) {
            int dotIndex = name.getImage().indexOf(".");
            if (dotIndex == -1) {
                variableName = name.getImage();
            } else {
                variableName = name.getImage().substring(0, dotIndex);
            }
        }
        
        return variableName;
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

    private VariableNameDeclaration findVariableDeclaration(String variableName, Scope scope) {
        VariableNameDeclaration result = null;
        
        for (VariableNameDeclaration declaration : scope.getDeclarations(VariableNameDeclaration.class).keySet()) {
            if (declaration.getImage().equals(variableName)) {
                result = declaration;
                break;
            }
        }
        
        if (result == null && scope.getParent() != null && !(scope.getParent() instanceof SourceFileScope)) {
            result = findVariableDeclaration(variableName, scope.getParent());
        }
        
        return result;
    }

    private boolean isAttributeAccess(ASTPrimaryExpression node) {
        return node.findDescendantsOfType(ASTPrimarySuffix.class).isEmpty();
    }



    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        wmcCounter++;
        
        currentMethodName = node.getFirstChildOfType(ASTMethodDeclarator.class).getImage();
        methodAttributeAccess.put(currentMethodName, new HashSet<String>());
        
        Object result = super.visit(node, data);
        
        currentMethodName = null;
        
        return result;
    }

    @Override
    public Object visit(ASTConditionalOrExpression node, Object data) {
        wmcCounter++;
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTConditionalAndExpression node, Object data) {
        wmcCounter++;
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        wmcCounter++;
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        wmcCounter++;
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {
        wmcCounter++;
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTSwitchLabel node, Object data) {
        wmcCounter++;
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTCatchStatement node, Object data) {
        wmcCounter++;
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTConditionalExpression node, Object data) {
        if (node.isTernary()) {
            wmcCounter++;
        }
        return super.visit(node, data);
    }
    
    
}
