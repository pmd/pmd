/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class NameOccurrences {

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

    private void buildOccurrences(ASTPrimaryExpression node) {
        ASTPrimaryPrefix prefix = (ASTPrimaryPrefix) node.jjtGetChild(0);
        if (prefix.usesSuperModifier()) {
            add(new NameOccurrence(prefix, "super"));
        } else if (prefix.usesThisModifier()) {
            add(new NameOccurrence(prefix, "this"));
        }
        checkForNameChild(prefix);

        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
            checkForNameChild((ASTPrimarySuffix) node.jjtGetChild(i));
        }
    }

    private void checkForNameChild(SimpleNode node) {
        // TODO when is this null?
        if (node.getImage() != null) {
            add(new NameOccurrence(node, node.getImage()));
        }
        if (node.jjtGetNumChildren() > 0 && node.jjtGetChild(0) instanceof ASTName) {
            ASTName grandchild = (ASTName) node.jjtGetChild(0);
            for (StringTokenizer st = new StringTokenizer(grandchild.getImage(), "."); st.hasMoreTokens();) {
                add(new NameOccurrence(grandchild, st.nextToken()));
            }
        }
        if (node instanceof ASTPrimarySuffix && ((ASTPrimarySuffix) node).isArguments()) {
            ((NameOccurrence) names.get(names.size() - 1)).setIsMethodOrConstructorInvocation();
        }
    }

    private void add(NameOccurrence name) {
        names.add(name);
        if (names.size() > 1) {
            NameOccurrence qualifiedName = (NameOccurrence) names.get(names.size() - 2);
            qualifiedName.setNameWhichThisQualifies(name);
        }
    }


    public String toString() {
        String result = "";
        for (Iterator i = names.iterator(); i.hasNext();) {
            NameOccurrence occ = (NameOccurrence) i.next();
            result += occ.getImage();
        }
        return result;
    }
}
