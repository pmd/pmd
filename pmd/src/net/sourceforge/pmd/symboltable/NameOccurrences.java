/*
 * User: tom
 * Date: Oct 18, 2002
 * Time: 1:50:23 PM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTName;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class NameOccurrences {

    // TODO could this be a Stack?
    private List names = new ArrayList();

    public NameOccurrences(ASTPrimaryExpression node) {
        buildOccurrences(node);
    }

    public List getNames() {
        return names;
    }

    public Iterator iterator() {
        return names.iterator();
    }

    public String toString() {
        String result = "";
        for (Iterator i=names.iterator();i.hasNext();) {
            NameOccurrence occ = (NameOccurrence)i.next();
            result += occ.getImage();
        }
        return result;
    }

    private void buildOccurrences(ASTPrimaryExpression node) {
        for (int i=0; i<node.jjtGetNumChildren(); i++) {
            SimpleNode child = (SimpleNode)node.jjtGetChild(i);
            if (child instanceof ASTPrimaryPrefix) {
                ASTPrimaryPrefix prefix = (ASTPrimaryPrefix)child;
                if (prefix.usesSuperModifier()) {
                    add(new NameOccurrence(prefix, "super"));
                } else if (prefix.usesThisModifier()) {
                    add(new NameOccurrence(prefix, "this"));
                }
            }
            checkForNameChild(child);
        }
    }

    private void checkForNameChild(SimpleNode node) {
        if (node.getImage() != null) {
            add(new NameOccurrence(node, node.getImage()));
        }
        if (node.jjtGetNumChildren() > 0 && node.jjtGetChild(0) instanceof ASTName) {
            ASTName grandchild = (ASTName)node.jjtGetChild(0);
            for (StringTokenizer st = new StringTokenizer(grandchild.getImage(), "."); st.hasMoreTokens();) {
                add(new NameOccurrence(grandchild, st.nextToken()));
            }
        }
    }

    private void add(NameOccurrence name) {
        names.add(name);
        if (names.size() > 1) {
            NameOccurrence qualifiedName = (NameOccurrence)names.get(names.size()-2);
            qualifiedName.setNameWhichThisQualifies(name);
        }
    }

}
