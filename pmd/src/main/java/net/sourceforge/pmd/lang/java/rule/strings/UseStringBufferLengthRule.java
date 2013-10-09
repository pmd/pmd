package net.sourceforge.pmd.lang.java.rule.strings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;


/**
 * This rule finds places where StringBuffer.toString() is called just to see if
 * the string is 0 length by either using .equals("") or toString().length()
 * <p/>
 * <pre>
 * StringBuffer sb = new StringBuffer(&quot;some string&quot;);
 * if (sb.toString().equals(&quot;&quot;)) {
 *     // this is wrong
 * }
 * if (sb.length() == 0) {
 *     // this is right
 * }
 * </pre>
 *
 * @author acaplan
 * @author Philip Graf
 */
public class UseStringBufferLengthRule extends AbstractJavaRule {

    // FIXME  Need to remove this somehow.
    /*
    Specifically, we need a symbol tree that can be traversed downwards, so that instead
    of visiting each name and then visiting the declaration for that name, we should visit all
    the declarations and check their usages.
    With that in place, this rule would be reduced to:
    - find all StringBuffer declarations
    - check each usage
    - flag those that involve variable.toString()
    */
    private Set<VariableNameDeclaration> alreadySeen = new HashSet<VariableNameDeclaration>();

    @Override
    public Object visit(ASTMethodDeclaration acu, Object data) {
        alreadySeen.clear();
        return super.visit(acu, data);
    }

    @Override
    public Object visit(ASTName decl, Object data) {
        if (!decl.getImage().endsWith("toString")) {
            return data;
        }
        NameDeclaration nd = decl.getNameDeclaration();
        if (!(nd instanceof VariableNameDeclaration)) {
            return data;
        }
        VariableNameDeclaration vnd = (VariableNameDeclaration) nd;
        if (alreadySeen.contains(vnd) || 
        		TypeHelper.isNeither(vnd, StringBuffer.class, StringBuilder.class)) {
            return data;
        }
        alreadySeen.add(vnd);

        if (isViolation(decl)) {
            addViolation(data, decl);
        }

        return data;
    }

    /**
     * Returns true for the following violations:
     * 
     * <pre>
     * StringBuffer sb = new StringBuffer(&quot;some string&quot;);
     * if (sb.toString().equals(&quot;&quot;)) {
     *     // this is a violation
     * }
     * if (sb.toString().length() == 0) {
     *     // this is a violation
     * }
     * if (sb.length() == 0) {
     *     // this is ok
     * }
     * </pre>
     */
    private boolean isViolation(ASTName decl) {
        // the (grand)parent of a violation has four children
        Node parent = decl.jjtGetParent().jjtGetParent();
        if (parent.jjtGetNumChildren() == 4) {
            // 1. child: sb.toString where sb is a VariableNameDeclaration for a StringBuffer or StringBuilder
            if (parent.jjtGetChild(0).getFirstChildOfType(ASTName.class).getImage().endsWith(".toString")) {
                // 2. child: the arguments of toString
                // no need to check as both StringBuffer and StringBuilder only have one toString method
                // 3. child: equals or length, 4. child: their arguments
                return isEqualsViolation(parent) || isLengthViolation(parent);
            }
        }
        return false;
    }
    
    private boolean isEqualsViolation(Node parent) {
        // 3. child: equals
        if (parent.jjtGetChild(2).hasImageEqualTo("equals")) {
            // 4. child: the arguments of equals, there must be exactly one and it must be ""
            List<ASTArgumentList> argList = parent.jjtGetChild(3).findDescendantsOfType(ASTArgumentList.class);
            if (argList.size() == 1) {
                List<ASTLiteral> literals = argList.get(0).findDescendantsOfType(ASTLiteral.class);
                return literals.size() == 1 && literals.get(0).hasImageEqualTo("\"\"");
            }
        }
        return false;
    }
    
    private boolean isLengthViolation(Node parent) {
        // 3. child: length
        return parent.jjtGetChild(2).hasImageEqualTo("length");
        // 4. child: the arguments of length
        // no need to check as String has only one length method
    }
    
}
