/*
 *  Copyright (c) 2002-2005, the pmd-netbeans team
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
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
            addViolation(ctx, node);
        }

        return super.visit(node, data);
    }
    
}
