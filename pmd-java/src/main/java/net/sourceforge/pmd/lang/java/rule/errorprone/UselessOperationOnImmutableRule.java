/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * An operation on an Immutable object (String, BigDecimal or BigInteger) won't
 * change the object itself. The result of the operation is a new object.
 * Therefore, ignoring the operation result is an error.
 */
public class UselessOperationOnImmutableRule extends AbstractJavaRule {

    /**
     * These are the BigDecimal methods which are immutable
     */
    private static final Set<String> BIG_DECIMAL_METHODS = CollectionUtil
            .asSet(new String[] { ".abs", ".add", ".divide", ".divideToIntegralValue", ".max", ".min", ".movePointLeft",
                ".movePointRight", ".multiply", ".negate", ".plus", ".pow", ".remainder", ".round",
                ".scaleByPowerOfTen", ".setScale", ".stripTrailingZeros", ".subtract", ".ulp", });

    /**
     * These are the BigInteger methods which are immutable
     */
    private static final Set<String> BIG_INTEGER_METHODS = CollectionUtil
            .asSet(new String[] { ".abs", ".add", ".and", ".andNot", ".clearBit", ".divide", ".flipBit", ".gcd", ".max",
                ".min", ".mod", ".modInverse", ".modPow", ".multiply", ".negate", ".nextProbablePrine", ".not", ".or",
                ".pow", ".remainder", ".setBit", ".shiftLeft", ".shiftRight", ".subtract", ".xor", });

    /**
     * These are the String methods which are immutable
     */
    private static final Set<String> STRING_METHODS = CollectionUtil
            .asSet(new String[] { ".concat", ".intern", ".replace", ".replaceAll", ".replaceFirst", ".substring",
                ".toLowerCase", ".toString", ".toUpperCase", ".trim", });

    /**
     * These are the classes that the rule can apply to
     */
    private static final Map<String, Set<String>> MAP_CLASSES = new HashMap<>();

    static {
        MAP_CLASSES.put("java.math.BigDecimal", BIG_DECIMAL_METHODS);
        MAP_CLASSES.put("BigDecimal", BIG_DECIMAL_METHODS);
        MAP_CLASSES.put("java.math.BigInteger", BIG_INTEGER_METHODS);
        MAP_CLASSES.put("BigInteger", BIG_INTEGER_METHODS);
        MAP_CLASSES.put("java.lang.String", STRING_METHODS);
        MAP_CLASSES.put("String", STRING_METHODS);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {

        ASTVariableDeclaratorId var = getDeclaration(node);
        if (var == null) {
            return super.visit(node, data);
        }
        String variableName = var.getImage();
        for (NameOccurrence no : var.getUsages()) {
            // FIXME - getUsages will return everything with the same name as
            // the variable,
            // see JUnit test, case 6. Changing to Node below, revisit when
            // getUsages is fixed
            Node sn = no.getLocation();
            Node primaryExpression = sn.getParent().getParent();
            Class<? extends Node> parentClass = primaryExpression.getParent().getClass();
            if (parentClass.equals(ASTStatementExpression.class)) {
                String methodCall = sn.getImage().substring(variableName.length());
                ASTType nodeType = node.getTypeNode();
                if (nodeType != null) {
                    if (MAP_CLASSES.get(nodeType.getTypeImage()).contains(methodCall)) {
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
        if (type != null) {
            if (MAP_CLASSES.keySet().contains(type.getTypeImage())) {
                return node.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
            }
        }
        return null;
    }
}
