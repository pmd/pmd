/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Node;

import java.text.MessageFormat;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;


/**
 * Makes sure you close your database connections. It does this by
 * looking for code patterned like this:
 * <pre>
 *  Connection c = X;
 *  try {
 *   // do stuff, and maybe catch something
 *  } finally {
 *   c.close();
 *  }
 * </pre>
 */
public class CloseConnectionRule extends AbstractRule {
  public Object visit(ASTMethodDeclaration node, Object data) {
      List vars = node.findChildrenOfType(ASTLocalVariableDeclaration.class);
      List ids = new Vector();

      // find all variable references to Connection objects
      for (Iterator it = vars.iterator(); it.hasNext();) {
        ASTLocalVariableDeclaration var = (ASTLocalVariableDeclaration) it.next();
        ASTType type = (ASTType) var.jjtGetChild(0);

        if (type.jjtGetChild(0) instanceof ASTName && ((ASTName) type.jjtGetChild(0)).getImage().equals("Connection")) {
            ASTVariableDeclaratorId id = (ASTVariableDeclaratorId) var.jjtGetChild(1).jjtGetChild(0);
            ids.add(id);
        }
      }

      // if there are connections, ensure each is closed.
      for (int i = 0; i < ids.size(); i++) {
        ASTVariableDeclaratorId x = (ASTVariableDeclaratorId) ids.get(i);
        ensureClosed((ASTLocalVariableDeclaration) x.jjtGetParent()
                                                    .jjtGetParent(), x, data);
      }
      return data;
  }

  private void ensureClosed(ASTLocalVariableDeclaration var,
    ASTVariableDeclaratorId id, Object data) {
    // What are the chances of a Connection being instantiated in a
    // for-loop init block? Anyway, I'm lazy!    
      String target = id.getImage() + ".close";
      Node n = var;

      while (!((n = n.jjtGetParent()) instanceof ASTBlock))
        ;

      ASTBlock top = (ASTBlock) n;

      List tryblocks = new Vector();
      top.findChildrenOfType(ASTTryStatement.class, tryblocks, true);

      boolean closed = false;

      // look for try blocks below the line the variable was
      // introduced and make sure there is a .close call in a finally
      // block.
      for (Iterator it = tryblocks.iterator(); it.hasNext();) {
        ASTTryStatement t = (ASTTryStatement) it.next();

        if ((t.getBeginLine() > id.getBeginLine()) && (t.hasFinally())) {
          ASTBlock f = t.getFinallyBlock();
          List names = new ArrayList();
          f.findChildrenOfType(ASTName.class, names, true);
          for (Iterator it2 = names.iterator(); it2.hasNext();) {
              if (((ASTName) it2.next()).getImage().equals(target)) {
              closed = true;
            }
          }
        }
      }

      // if all is not well, complain
      if (!closed) {
        RuleContext ctx = (RuleContext) data;
        ctx.getReport().addRuleViolation(createRuleViolation(ctx, id.getBeginLine(), getMessage()));
      }
  }
}
