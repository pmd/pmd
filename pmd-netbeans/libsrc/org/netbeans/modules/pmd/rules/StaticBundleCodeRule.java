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

import java.text.MessageFormat;

import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.symboltable.*;

/** Searches for statically referenced bundles
 *
 * Taken from UnusedPrivateVariable
 * @author Radim Kubacki
 */
public class StaticBundleCodeRule extends AbstractRule {

    /**
     * TODO Skip interfaces because they don't have instance variables.
     * / 
    public Object visit(ASTInterfaceDeclaration node, Object data) {
        return data;
    }
     */

    public Object visit(ASTClassOrInterfaceBody node, Object data) {
        RuleContext ctx = (RuleContext)data;

        for (int i=0;i<node.jjtGetNumChildren(); i++) {
            SimpleNode child = (SimpleNode)node.jjtGetChild(i);
            if (child instanceof ASTClassOrInterfaceBodyDeclaration && child.jjtGetNumChildren() > 0 &&  child.jjtGetChild(0) instanceof ASTFieldDeclaration) {
                ASTFieldDeclaration field = (ASTFieldDeclaration)child.jjtGetChild(0);
                if (!field.isStatic()) {
                    continue;
                }
//                 FieldDeclaration:(private)(static)
//                  Type
//                   ReferenceType
//                    ClassOrInterfaceType:NbBundle
//                  VariableDeclarator
//                   VariableDeclaratorId:bundle
                SimpleNode target = (SimpleNode)field.jjtGetChild(0).jjtGetChild(0); // should be ReferenceType
                if (!(target instanceof ASTReferenceType)) {
                    continue; // probably PrimitiveType, skip it
                }
                target = (SimpleNode)target.jjtGetChild(0); // should be ClassOrInterfaceType
                if (!isBundleType (target.getImage())) {
                    continue;
                }
                SimpleNode var = (SimpleNode)field.jjtGetChild(1).jjtGetChild(0);

                ctx.getReport().addRuleViolation(createRuleViolation(ctx, field, MessageFormat.format(getMessage(), new Object[] {var.getImage()})));
            }
        }
        super.visit(node, data);
        return data;
    }

    private boolean isBundleType (String value) {
        if (value == null) {
            return false;
        }
        if ("ResourceBundle".equals(value)  // NOI18N
        ||  "NbBundle".equals(value)        // NOI18N
        ||  "java.util.ResourceBundle".equals(value)    // NOI18N
        ||  "org.openide.util.NbBundle".equals(value))  // NOI18N
            return true;
        
        return false;
    }
}
