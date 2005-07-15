/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;

import java.util.ArrayList;
import java.util.List;

/** See issue 15242 and rule description for details.
 * @author David Konecny
 */
public class MissingPrivateInnerClassConstructorRule extends AbstractRule {

    private List constructors;

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (!node.isPrivate() || !node.isNested()) {
            return super.visit(node, data);
        }
        constructors = new ArrayList();
        node.findChildrenOfType(ASTConstructorDeclaration.class, constructors);

        boolean ok = true;
        if (constructors.size() == 0) {
            ok = false;
        }/* else {  //these needs to be tuned up
            for (java.util.Iterator i = constructors.iterator(); i.hasNext();) {
                ASTConstructorDeclaration c = (ASTConstructorDeclaration)i.next();
                if (!c.isPrivate()) {
                    ok = true;
                    break;
                }
            }
        }*/
        if (!ok) {
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node));
        }

        return super.visit(node, data);
    }
    
}
