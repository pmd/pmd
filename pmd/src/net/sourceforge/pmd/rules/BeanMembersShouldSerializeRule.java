/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTUnmodifiedClassDeclaration;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BeanMembersShouldSerializeRule extends AbstractRule {

    public Object visit(ASTUnmodifiedClassDeclaration node, Object data) {
        List methList = new ArrayList();
        node.findChildrenOfType(ASTMethodDeclarator.class, methList);

        List getSetMethList = new ArrayList();
        for (int i = 0; i < methList.size(); i++){
            ASTMethodDeclarator meth = (ASTMethodDeclarator)methList.get(i);
            String methName = meth.getImage();
            if (methName.startsWith("get") || methName.startsWith("set") || methName.startsWith("is")){
                getSetMethList.add(meth);
            }
        }
        String[] methNameArray = new String[getSetMethList.size()];
        for (int i = 0; i < getSetMethList.size(); i++){
            String methName = ((ASTMethodDeclarator)getSetMethList.get(i)).getImage();
            methNameArray[i] = methName;
        }

        Arrays.sort(methNameArray);

        for (Iterator i = node.getScope().getVariableDeclarations(true).keySet().iterator();i.hasNext();) {
            VariableNameDeclaration decl = (VariableNameDeclaration)i.next();
            if (decl.getAccessNodeParent().isTransient() || decl.getAccessNodeParent().isStatic()){
                continue;
            }
            String varName = decl.getImage();
            String firstChar = varName.substring(0,1);
            varName = firstChar.toUpperCase() + varName.substring(1,varName.length());
            boolean hasGetMethod =Arrays.binarySearch(methNameArray,"get" + varName) >= 0  || Arrays.binarySearch(methNameArray,"is" + varName) >= 0;
            boolean hasSetMethod = Arrays.binarySearch(methNameArray,"set" + varName) >= 0;
            if (!hasGetMethod || !hasSetMethod) {
                RuleContext ctx = (RuleContext)data;
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, decl.getLine(), MessageFormat.format(getMessage(), new Object[] {decl.getImage()})));
            }
        }
        return super.visit(node, data);
    }
}
