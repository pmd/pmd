/*
 * AssignmentToNonFinalStaticRule.java
 *
 * Created on October 24, 2004, 8:56 AM
 */

package net.sourceforge.pmd.rules.design;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTUnmodifiedClassDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;


/**
 *
 * @author Eric Olander
 */
public class AssignmentToNonFinalStatic extends AbstractRule {
    
    public Object visit(ASTUnmodifiedClassDeclaration node, Object data) {
        Map vars = node.getScope().getVariableDeclarations();
        for (Iterator i = vars.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration decl = (VariableNameDeclaration) i.next();
            if (!decl.getAccessNodeParent().isStatic() || decl.getAccessNodeParent().isFinal()) {
                continue;
            }
            
            if (initializedInConstructor((List)vars.get(decl))) {
                ((RuleContext) data).getReport().addRuleViolation(createRuleViolation((RuleContext) data, decl.getLine(), MessageFormat.format(getMessage(), new Object[]{decl.getImage()})));
            }
        }
        return super.visit(node, data);
    }
    
    private boolean initializedInConstructor(List usages) {
        boolean initInConstructor = false;
        
        for (Iterator j = usages.iterator(); j.hasNext();) {
            NameOccurrence occ = (NameOccurrence)j.next();
            if (occ.isOnLeftHandSide()) { // specifically omitting prefix and postfix operators as there are legitimate usages of these with static fields, e.g. typesafe enum pattern.
                SimpleNode node = occ.getLocation();
                SimpleNode constructor = (SimpleNode)node.getFirstParentOfType(ASTConstructorDeclaration.class);
                if (constructor != null) {
                    initInConstructor = true;
                }
            }
        }
        
        return initInConstructor;
    }
    
}
