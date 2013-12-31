/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTArguments;
import net.sourceforge.pmd.lang.plsql.ast.ASTName;
import net.sourceforge.pmd.lang.plsql.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.plsql.ast.ASTPrimarySuffix;
//import net.sourceforge.pmd.lang.plsql.ast.ASTMemberSelector;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;

public class NameFinder {

    private List<PLSQLNameOccurrence> names = new ArrayList<PLSQLNameOccurrence>();

    public NameFinder(ASTPrimaryExpression node) {
    	Node simpleNode = node.jjtGetChild(0);
        if (simpleNode instanceof ASTPrimaryPrefix) 
        {
          ASTPrimaryPrefix prefix = (ASTPrimaryPrefix) simpleNode ;
          //if (prefix.usesSuperModifier()) {
          //    add(new NameOccurrence(prefix, "super"));
          //} else 
          if (prefix.usesSelfModifier()) {
              add(new PLSQLNameOccurrence(prefix, "this"));
          }
        }
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            checkForNameChild(node.jjtGetChild(i));
        }
    }

    public List<PLSQLNameOccurrence> getNames() {
        return names;
    }

    private void checkForNameChild(Node node) {
        if (node.getImage() != null) {
            add(new PLSQLNameOccurrence((PLSQLNode) node, node.getImage()));
        }
        if (node.jjtGetNumChildren() > 0 && node.jjtGetChild(0) instanceof ASTName) {
            ASTName grandchild = (ASTName) node.jjtGetChild(0);
            for (StringTokenizer st = new StringTokenizer(grandchild.getImage(), "."); st.hasMoreTokens();) {
                add(new PLSQLNameOccurrence(grandchild, st.nextToken()));
            }
        }
        if (node instanceof ASTPrimarySuffix) {
            ASTPrimarySuffix suffix = (ASTPrimarySuffix) node;
            if (suffix.isArguments()) {
                PLSQLNameOccurrence occurrence = names.get(names.size() - 1);
                occurrence.setIsMethodOrConstructorInvocation();
                ASTArguments args = (ASTArguments) ((ASTPrimarySuffix) node).jjtGetChild(0);
                occurrence.setArgumentCount(args.getArgumentCount());
            } //else if (suffix.jjtGetNumChildren() == 1 
		//       && suffix.jjtGetChild(0) instanceof ASTMemberSelector) 
	    //{
            //    add(new NameOccurrence((SimpleNode)suffix.jjtGetChild(0), suffix.jjtGetChild(0).getImage()));
            //}
        }
    }

    private void add(PLSQLNameOccurrence name) {
        names.add(name);
        if (names.size() > 1) {
            PLSQLNameOccurrence qualifiedName = names.get(names.size() - 2);
            qualifiedName.setNameWhichThisQualifies(name);
        }
    }


    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
        for (PLSQLNameOccurrence occ: names) {
            result.append(occ.getImage());
        }
        return result.toString();
    }
}
