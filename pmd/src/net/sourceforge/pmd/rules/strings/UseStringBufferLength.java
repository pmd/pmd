package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameDeclaration;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * This rule finds places where StringBuffer.toString() is called just to see if
 * the string is 0 length by either using .equals("") or toString().size()
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
public class UseStringBufferLength extends AbstractRule {

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
    private Set alreadySeen = new HashSet();

    public Object visit(ASTCompilationUnit acu, Object data) {
        alreadySeen.clear();
        return super.visit(acu, data);
    }

    public Object visit(ASTName decl, Object data) {
        if (!decl.getImage().endsWith("toString")) {
            return data;
        }
        NameDeclaration nd = decl.getNameDeclaration();
        if (!(nd instanceof VariableNameDeclaration)) {
            return data;
        }
        VariableNameDeclaration vnd = (VariableNameDeclaration) nd;
        if (!vnd.getTypeImage().equals("StringBuffer") || alreadySeen.contains(vnd)) {
            return data;
        }
        alreadySeen.add(vnd);

        SimpleNode parent = (SimpleNode) decl.jjtGetParent().jjtGetParent();
        for (int jx = 0; jx < parent.jjtGetNumChildren(); jx++) {
            SimpleNode achild = (SimpleNode) parent.jjtGetChild(jx);
            if (isViolation(parent, achild)) {
                addViolation(data, decl);
            }
        }

        return data;
    }

    /**
     * Check the given node if it calls either .equals or .size we need to check the target
     */
    private boolean isViolation(SimpleNode parent, SimpleNode achild) {
        if ("equals".equals(achild.getImage())) {
            List literals = parent.findChildrenOfType(ASTLiteral.class);
            return (!literals.isEmpty() && "\"\"".equals(((SimpleNode) literals.get(0)).getImage()));
        } else if ("length".equals(achild.getImage())) {
            return true;
        }
        return false;
    }


}
