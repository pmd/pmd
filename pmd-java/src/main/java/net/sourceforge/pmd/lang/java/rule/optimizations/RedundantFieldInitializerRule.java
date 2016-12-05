/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.optimizations;

import java.math.BigInteger;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Detects redundant field initializers, i.e. the field initializer expressions
 * the JVM would assign by default.
 *
 * @author lucian.ciufudean@gmail.com
 * @since Apr 10, 2009
 */
public class RedundantFieldInitializerRule extends AbstractJavaRule {

    public RedundantFieldInitializerRule() {
        addRuleChainVisit(ASTFieldDeclaration.class);
    }

    public Object visit(ASTFieldDeclaration fieldDeclaration, Object data) {
        // Finals can only be initialized once.
        if (fieldDeclaration.isFinal()) {
            return data;
        }

        // Look for a match to the following XPath:
        // VariableDeclarator/VariableInitializer/Expression/PrimaryExpression/PrimaryPrefix/Literal
        for (ASTVariableDeclarator variableDeclarator : fieldDeclaration
                .findChildrenOfType(ASTVariableDeclarator.class)) {
            if (variableDeclarator.jjtGetNumChildren() > 1) {
                final Node variableInitializer = variableDeclarator.jjtGetChild(1);
                if (variableInitializer.jjtGetChild(0) instanceof ASTExpression) {
                    final Node expression = variableInitializer.jjtGetChild(0);
                    final Node primaryExpression;
                    if (expression.jjtGetNumChildren() == 1) {
                        if (expression.jjtGetChild(0) instanceof ASTPrimaryExpression) {
                            primaryExpression = expression.jjtGetChild(0);
                        } else if (expression.jjtGetChild(0) instanceof ASTCastExpression
                                && expression.jjtGetChild(0).jjtGetChild(1) instanceof ASTPrimaryExpression) {
                            primaryExpression = expression.jjtGetChild(0).jjtGetChild(1);
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                    final Node primaryPrefix = primaryExpression.jjtGetChild(0);
                    if (primaryPrefix.jjtGetNumChildren() == 1 && primaryPrefix.jjtGetChild(0) instanceof ASTLiteral) {
                        final ASTLiteral literal = (ASTLiteral) primaryPrefix.jjtGetChild(0);
                        if (isRef(fieldDeclaration, variableDeclarator)) {
                            // Reference type
                            if (literal.jjtGetNumChildren() == 1 && literal.jjtGetChild(0) instanceof ASTNullLiteral) {
                                addViolation(data, variableDeclarator);
                            }
                        } else {
                            // Primitive type
                            if (literal.jjtGetNumChildren() == 1
                                    && literal.jjtGetChild(0) instanceof ASTBooleanLiteral) {
                                // boolean type
                                ASTBooleanLiteral booleanLiteral = (ASTBooleanLiteral) literal.jjtGetChild(0);
                                if (!booleanLiteral.isTrue()) {
                                    addViolation(data, variableDeclarator);
                                }
                            } else if (literal.jjtGetNumChildren() == 0) {
                                // numeric type
                                // Note: Not catching NumberFormatException, as
                                // it shouldn't be happening on valid source
                                // code.
                                Number value = -1;
                                if (literal.isIntLiteral()) {
                                    value = parseInteger(literal.getImage());
                                } else if (literal.isLongLiteral()) {
                                    String s = literal.getImage();
                                    // remove the ending "l" or "L" for long
                                    // values
                                    s = s.substring(0, s.length() - 1);
                                    value = parseInteger(s);
                                } else if (literal.isFloatLiteral()) {
                                    String s = literal.getImage();
                                    // remove the ending "f" or "F" for float
                                    // values
                                    s = s.substring(0, s.length() - 1);
                                    value = Float.valueOf(s);
                                } else if (literal.isDoubleLiteral()) {
                                    value = Double.valueOf(literal.getImage());
                                } else if (literal.isCharLiteral()) {
                                    value = (int) literal.getImage().charAt(1);
                                }

                                if (value.doubleValue() == 0) {
                                    addViolation(data, variableDeclarator);
                                }
                            }
                        }
                    }
                }
            }
        }

        return data;
    }

    /**
     * Checks if a FieldDeclaration is a reference type (includes arrays). The
     * reference information is in the FieldDeclaration for this example:
     * 
     * <pre>
     * int[] ia1
     * </pre>
     * 
     * and in the VariableDeclarator for this example:
     * 
     * <pre>
     * int ia2[];
     * </pre>
     * 
     * .
     *
     * @param fieldDeclaration
     *            the field to check.
     * @param variableDeclarator
     *            the variable declarator to check.
     * @return <code>true</code> if the field is a reference. <code>false</code>
     *         otherwise.
     */
    private boolean isRef(ASTFieldDeclaration fieldDeclaration, ASTVariableDeclarator variableDeclarator) {
        Node type = fieldDeclaration.jjtGetChild(0).jjtGetChild(0);
        if (type instanceof ASTReferenceType) {
            // Reference type, array or otherwise
            return true;
        } else {
            // Primitive array?
            return ((ASTVariableDeclaratorId) variableDeclarator.jjtGetChild(0)).isArray();
        }
    }

    private void addViolation(Object data, ASTVariableDeclarator variableDeclarator) {
        super.addViolation(data, variableDeclarator, variableDeclarator.jjtGetChild(0).getImage());
    }

    private Number parseInteger(String s) {
        boolean negative = false;
        String number = s;
        if (number.charAt(0) == '-') {
            negative = true;
            number = number.substring(1);
        }
        BigInteger result;
        if (number.startsWith("0x") || number.startsWith("0X")) {
            result = new BigInteger(number.substring(2).replaceAll("_", ""), 16);
        } else if (number.startsWith("0b") || number.startsWith("0B")) {
            result = new BigInteger(number.substring(2).replaceAll("_", ""), 8);
        } else if (number.charAt(0) == '0' && number.length() > 1) {
            result = new BigInteger(number.substring(1).replaceAll("_", ""), 8);
        } else {
            result = new BigInteger(number.replaceAll("_", ""));
        }
        if (negative) {
            result = result.negate();
        }
        return result;
    }
}
