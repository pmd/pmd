package net.sourceforge.pmd.rules;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.AbstractJavaRule;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * An operation on an Immutable object (String, BigDecimal or BigInteger) won't change
 * the object itself. The result of the operation is a new object. Therefore,
 * ignoring the operation result is an error.
 */
public class UselessOperationOnImmutable extends AbstractJavaRule {

    /**
     * These are the BigDecimal methods which are immutable
     */
    private static final Set<String> decMethods = CollectionUtil.asSet(new String[] { ".abs", ".add", ".divide", ".divideToIntegralValue", ".max", ".min", ".movePointLeft", ".movePointRight", ".multiply", ".negate", ".plus", ".pow", ".remainder", ".round", ".scaleByPowerOfTen", ".setScale", ".stripTrailingZeros", ".subtract", ".ulp" });

    /**
     * These are the BigInteger methods which are immutable
     */
    private static final Set<String> intMethods = CollectionUtil.asSet(new String[] { ".abs", ".add", ".and", ".andNot", ".clearBit", ".divide", ".flipBit", ".gcd", ".max", ".min", ".mod", ".modInverse", ".modPow", ".multiply", ".negate", ".nextProbablePrine", ".not", ".or", ".pow", ".remainder", ".setBit", ".shiftLeft", ".shiftRight", ".subtract", ".xor" });

    /**
     * These are the String methods which are immutable
     */
    private static final Set<String> strMethods = CollectionUtil.asSet(new String[] { ".concat", ".intern", ".replace", ".replaceAll", ".replaceFirst", ".substring", ".toLowerCase", ".toString", ".toUpperCase", ".trim" });

    /**
     * These are the classes that the rule can apply to
     */
    private static final Map<String, Set<String>> mapClasses = new HashMap<String, Set<String>>();
    static {
        mapClasses.put("java.math.BigDecimal", decMethods);
        mapClasses.put("BigDecimal", decMethods);
        mapClasses.put("java.math.BigInteger", intMethods);
        mapClasses.put("BigInteger", intMethods);
        mapClasses.put("java.lang.String", strMethods);
        mapClasses.put("String", strMethods);
    }

    public Object visit(ASTLocalVariableDeclaration node, Object data) {

        ASTVariableDeclaratorId var = getDeclaration(node);
        if (var == null) {
            return super.visit(node, data);
        }
        String variableName = var.getImage();
        for (NameOccurrence no: var.getUsages()) {
            // FIXME - getUsages will return everything with the same name as the variable, 
            // see JUnit test, case 6. Changing to SimpleNode below, revisit when getUsages is fixed
            SimpleNode sn = no.getLocation();
            Node primaryExpression = sn.jjtGetParent().jjtGetParent();
            Class<? extends Node> parentClass = primaryExpression.jjtGetParent().getClass();
            if (parentClass.equals(ASTStatementExpression.class)) {
                String methodCall = sn.getImage().substring(variableName.length());
                ASTType nodeType = node.getTypeNode();
                if ( nodeType != null ) {
                    if ( mapClasses.get(nodeType.getTypeImage()).contains(methodCall)) {
                        addViolation(data, sn);
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    /**
     * This method checks the variable declaration if it is on a class we care
     * about. If it is, it returns the DeclaratorId
     * 
     * @param node
     *            The ASTLocalVariableDeclaration which is a problem
     * @return ASTVariableDeclaratorId
     */
    private ASTVariableDeclaratorId getDeclaration(ASTLocalVariableDeclaration node) {
        ASTType type = node.getTypeNode();
        if (mapClasses.keySet().contains(type.getTypeImage())) {
            return (ASTVariableDeclaratorId) node.jjtGetChild(1).jjtGetChild(0);
        }
        return null;
    }
}
