/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTUnmodifiedClassDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.ast.ASTVariableInitializer;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ImmutableFieldRule extends AbstractRule {

    static private final int MUTABLE = 0;
    static private final int IMMUTABLE = 1;
    static private final int CHECKDECL = 2;
    
    public Object visit(ASTUnmodifiedClassDeclaration node, Object data) {
        Map vars = node.getScope().getVariableDeclarations();
        for (Iterator i = vars.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration decl = (VariableNameDeclaration) i.next();
            if (!decl.getAccessNodeParent().isPrivate() || decl.getAccessNodeParent().isFinal()) {
                continue;
            }
            int result = initializedInConstructor((List)vars.get(decl));
            if (result == MUTABLE) {
            	continue;
            }
            if ((result == IMMUTABLE) ||
                ((result == CHECKDECL) && initializedInDeclaration(decl.getAccessNodeParent()))) {
                ((RuleContext) data).getReport().addRuleViolation(createRuleViolation((RuleContext) data, decl.getLine(), MessageFormat.format(getMessage(), new Object[]{decl.getImage()})));
            }
        }
        return super.visit(node, data);
    }

    private int initializedInConstructor(List usages) {
        int rc = MUTABLE, initCount = 0;
        boolean setInConstructor = false;
		boolean foundUsage = false;

        for (Iterator j = usages.iterator(); j.hasNext();) {
        	foundUsage = true;
        	NameOccurrence occurance = (NameOccurrence)j.next();
            if (occurance.isOnLeftHandSide()) {
            	SimpleNode node = occurance.getLocation();
            	if (node.getFirstParentOfType(ASTConstructorDeclaration.class) != null) {
            		setInConstructor = true;
            		initCount++;
 				}
 				else {
 					if (node.getFirstParentOfType(ASTMethodDeclaration.class) != null) {
 						initCount++;
 					}
 				}
 			}
        }
        if (!foundUsage || (initCount == 0)) {
        	rc = CHECKDECL;
        }
        else {
        	if ((initCount == 1) && setInConstructor) {
        		rc = IMMUTABLE;
        	}
        }
        return rc;
    }

    private boolean initializedInDeclaration(SimpleNode node) {
    	boolean setInInitializer = false;
        List results = new Vector();

		node.findChildrenOfType(ASTVariableInitializer.class, results, true);
		if (results.size()>0) {
			setInInitializer = true;
		}
 		return setInInitializer;
    }
}
