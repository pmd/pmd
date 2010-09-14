package net.sourceforge.pmd.lang.java.rule.strings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
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

        Node parent = decl.jjtGetParent().jjtGetParent();
        for (int jx = 0; jx < parent.jjtGetNumChildren(); jx++) {
            Node achild = parent.jjtGetChild(jx);
            if (isViolation(parent, achild)) {
                addViolation(data, decl);
            }
        }

        return data;
    }

    /**
     * Check the given node if it calls either .equals or .length we need to check the target
     */
    private boolean isViolation(Node parent, Node achild) {
        if ("equals".equals(achild.getImage())) {
            List<ASTLiteral> literals = parent.findDescendantsOfType(ASTLiteral.class);
            return !literals.isEmpty() && "\"\"".equals(literals.get(0).getImage());
        } else if ("length".equals(achild.getImage())) {
            return true;
        }
        return false;
    }
}
