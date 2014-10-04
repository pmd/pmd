/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTMemberSelector;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class NameFinder {

    private List<JavaNameOccurrence> names = new ArrayList<JavaNameOccurrence>();

    public NameFinder(ASTPrimaryExpression node) {
        ASTPrimaryPrefix prefix = (ASTPrimaryPrefix) node.jjtGetChild(0);
        if (prefix.usesSuperModifier()) {
            add(new JavaNameOccurrence(prefix, "super"));
        } else if (prefix.usesThisModifier()) {
            add(new JavaNameOccurrence(prefix, "this"));
        }
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            checkForNameChild((JavaNode)node.jjtGetChild(i));
        }
    }

    public List<JavaNameOccurrence> getNames() {
        return names;
    }

    private void checkForNameChild(JavaNode node) {
        if (node.getImage() != null) {
            add(new JavaNameOccurrence(node, node.getImage()));
        }
        if (node.jjtGetNumChildren() > 0 && node.jjtGetChild(0) instanceof ASTName) {
            ASTName grandchild = (ASTName) node.jjtGetChild(0);
            for (StringTokenizer st = new StringTokenizer(grandchild.getImage(), "."); st.hasMoreTokens();) {
                add(new JavaNameOccurrence(grandchild, st.nextToken()));
            }
        }
        if (node.jjtGetNumChildren() > 1 && node.jjtGetChild(1) instanceof ASTMethodReference) {
            ASTMethodReference methodRef = (ASTMethodReference) node.jjtGetChild(1);
            add(new JavaNameOccurrence(methodRef, methodRef.getImage()));
        }
        if (node instanceof ASTPrimarySuffix) {
            ASTPrimarySuffix suffix = (ASTPrimarySuffix) node;
            if (suffix.isArguments()) {
                JavaNameOccurrence occurrence = names.get(names.size() - 1);
                occurrence.setIsMethodOrConstructorInvocation();
                ASTArguments args = (ASTArguments) ((ASTPrimarySuffix) node).jjtGetChild(0);
                occurrence.setArgumentCount(args.getArgumentCount());
            } else if (suffix.jjtGetNumChildren() == 1 && suffix.jjtGetChild(0) instanceof ASTMemberSelector) {
                ASTMemberSelector member = (ASTMemberSelector) suffix.jjtGetChild(0);
                if (member.jjtGetNumChildren() == 1 && member.jjtGetChild(0) instanceof ASTMethodReference) {
                    ASTMethodReference methodRef = (ASTMethodReference) member.jjtGetChild(0);
                    add(new JavaNameOccurrence(methodRef, methodRef.getImage()));
                } else {
                    add(new JavaNameOccurrence(member, member.getImage()));
                }
            }
        }
    }

    private void add(JavaNameOccurrence name) {
        names.add(name);
        if (names.size() > 1) {
            JavaNameOccurrence qualifiedName = names.get(names.size() - 2);
            qualifiedName.setNameWhichThisQualifies(name);
        }
    }


    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
        for (NameOccurrence occ: names) {
            result.append(occ);
            result.append(PMD.EOL);
        }
        return result.toString();
    }
}
