/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTPrimitiveType;
import net.sourceforge.pmd.ast.ASTResultType;
import net.sourceforge.pmd.symboltable.MethodNameDeclaration;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BeanMembersShouldSerializeRule extends AbstractRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }

        Map methods = node.getScope().getEnclosingClassScope().getMethodDeclarations();
        List getSetMethList = new ArrayList();
        for (Iterator i = methods.keySet().iterator(); i.hasNext();) {
            ASTMethodDeclarator mnd = ((MethodNameDeclaration) i.next()).getMethodNameDeclaratorNode();
            if (isBeanAccessor(mnd)) {
                getSetMethList.add(mnd);
            }
        }

        String[] methNameArray = new String[getSetMethList.size()];
        for (int i = 0; i < getSetMethList.size(); i++) {
            methNameArray[i] = ((ASTMethodDeclarator) getSetMethList.get(i)).getImage();
        }

        Arrays.sort(methNameArray);

        Map vars = node.getScope().getVariableDeclarations();
        for (Iterator i = vars.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration decl = (VariableNameDeclaration) i.next();
            if (((List) vars.get(decl)).isEmpty() || decl.getAccessNodeParent().isTransient() || decl.getAccessNodeParent().isStatic()) {
                continue;
            }
            String varName = trimIfPrefix(decl.getImage());
            varName = varName.substring(0, 1).toUpperCase() + varName.substring(1, varName.length());
            boolean hasGetMethod = Arrays.binarySearch(methNameArray, "get" + varName) >= 0 || Arrays.binarySearch(methNameArray, "is" + varName) >= 0;
            boolean hasSetMethod = Arrays.binarySearch(methNameArray, "set" + varName) >= 0;
            if (!hasGetMethod || !hasSetMethod) {
                addViolation(data, decl.getNode(), decl.getImage());
            }
        }
        return super.visit(node, data);
    }

    private String trimIfPrefix(String img) {
        if (getStringProperty("prefix") != null && img.startsWith(getStringProperty("prefix"))) {
            return img.substring(getStringProperty("prefix").length());
        }
        return img;
    }

    private boolean isBeanAccessor(ASTMethodDeclarator meth) {
        if (meth.getImage().startsWith("get") || meth.getImage().startsWith("set")) {
            return true;
        }
        if (meth.getImage().startsWith("is")) {
            ASTResultType ret = (ASTResultType) meth.jjtGetParent().jjtGetChild(0);
            List primitives = ret.findChildrenOfType(ASTPrimitiveType.class);
            if (!primitives.isEmpty() && ((ASTPrimitiveType) primitives.get(0)).isBoolean()) {
                return true;
            }
        }
        return false;
    }
}
