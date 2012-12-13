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

    private List<NameOccurrence> names = new ArrayList<NameOccurrence>();

    public NameFinder(ASTPrimaryExpression node) {
    	Node simpleNode = node.jjtGetChild(0);
        if (simpleNode instanceof ASTPrimaryPrefix) 
        {
          ASTPrimaryPrefix prefix = (ASTPrimaryPrefix) simpleNode ;
          //if (prefix.usesSuperModifier()) {
          //    add(new NameOccurrence(prefix, "super"));
          //} else 
          if (prefix.usesSelfModifier()) {
              add(new NameOccurrence(prefix, "this"));
          }
        }
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            checkForNameChild(node.jjtGetChild(i));
        }
    }

    public List<NameOccurrence> getNames() {
        return names;
    }

    private void checkForNameChild(Node node) {
        if (node.getImage() != null) {
            add(new NameOccurrence((PLSQLNode) node, node.getImage()));
        }
        if (node.jjtGetNumChildren() > 0 && node.jjtGetChild(0) instanceof ASTName) {
            ASTName grandchild = (ASTName) node.jjtGetChild(0);
            for (StringTokenizer st = new StringTokenizer(grandchild.getImage(), "."); st.hasMoreTokens();) {
                add(new NameOccurrence(grandchild, st.nextToken()));
            }
        }
        if (node instanceof ASTPrimarySuffix) {
            ASTPrimarySuffix suffix = (ASTPrimarySuffix) node;
            if (suffix.isArguments()) {
                NameOccurrence occurrence = names.get(names.size() - 1);
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

    private void add(NameOccurrence name) {
        names.add(name);
        if (names.size() > 1) {
            NameOccurrence qualifiedName = names.get(names.size() - 2);
            qualifiedName.setNameWhichThisQualifies(name);
        }
    }


    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
        for (NameOccurrence occ: names) {
            result.append(occ.getImage());
        }
        return result.toString();
    }
}
