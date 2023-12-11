/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.NumericConstraints.positive;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * CouplingBetweenObjects attempts to capture all unique Class attributes, local
 * variables, and return types to determine how many objects a class is coupled
 * to. This is only a gauge and isn't a hard and fast rule. The threshold value
 * is configurable and should be determined accordingly
 *
 * @author aglover
 * @since Feb 20, 2003
 */
public class CouplingBetweenObjectsRule extends AbstractJavaRule {

    private static final PropertyDescriptor<Integer> THRESHOLD_DESCRIPTOR
        = PropertyFactory.intProperty("threshold")
                         .desc("Unique type reporting threshold")
                         .require(positive()).defaultValue(20).build();

    private int couplingCount;
    private boolean inInterface;
    private final Set<JTypeMirror> typesFoundSoFar = new HashSet<>();

    public CouplingBetweenObjectsRule() {
        definePropertyDescriptor(THRESHOLD_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTCompilationUnit cu, Object data) {
        super.visit(cu, data);

        if (couplingCount > getProperty(THRESHOLD_DESCRIPTOR)) {
            addViolation(data, cu,
                         "A value of " + couplingCount + " may denote a high amount of coupling within the class");
        }

        couplingCount = 0;
        typesFoundSoFar.clear();
        return null;
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        boolean prev = inInterface;
        inInterface = node.isInterface();
        super.visit(node, data);
        inInterface = prev;
        return null;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        ASTType type = node.getResultTypeNode();
        checkVariableType(type);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        ASTType type = node.getTypeNode();
        checkVariableType(type);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTFormalParameter node, Object data) {
        ASTType type = node.getTypeNode();
        checkVariableType(type);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        ASTType type = node.getTypeNode();
        checkVariableType(type);
        return super.visit(node, data);
    }

    /**
     * performs a check on the variable and updates the counter. Counter is
     * instance for a class and is reset upon new class scan.
     *
     * @param typeNode The variable type.
     */
    private void checkVariableType(ASTType typeNode) {
        if (inInterface || typeNode == null) {
            return;
        }
        // if the field is of any type other than the class type
        // increment the count
        JTypeMirror t = typeNode.getTypeMirror();
        if (!this.ignoreType(typeNode, t) && this.typesFoundSoFar.add(t)) {
            couplingCount++;
        }
    }

    /**
     * Filters variable type - we don't want primitives, wrappers, strings, etc.
     * This needs more work. I'd like to filter out super types and perhaps
     * interfaces
     *
     * @param t The variable type.
     *
     * @return boolean true if variableType is not what we care about
     */
    private boolean ignoreType(ASTType typeNode, JTypeMirror t) {
        if (typeNode.getEnclosingType() != null && typeNode.getEnclosingType().getSymbol().equals(t.getSymbol())) {
            return true;
        }
        JTypeDeclSymbol symbol = t.getSymbol();
        return symbol == null
            || JAccessibleElementSymbol.PRIMITIVE_PACKAGE.equals(symbol.getPackageName())
            || t.isPrimitive()
            || t.isBoxedPrimitive();
    }

}
