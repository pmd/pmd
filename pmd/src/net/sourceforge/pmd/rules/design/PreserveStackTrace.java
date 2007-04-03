package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTArgumentList;
import net.sourceforge.pmd.ast.ASTCastExpression;
import net.sourceforge.pmd.ast.ASTCatchStatement;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTThrowStatement;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PreserveStackTrace extends AbstractRule {

    private List<ASTName> nameNodes = new ArrayList<ASTName>();

    public Object visit(ASTCatchStatement node, Object data) {
        String target = (((SimpleNode) node.jjtGetChild(0).jjtGetChild(1)).getImage());
        List<ASTThrowStatement> lstThrowStatements = node.findChildrenOfType(ASTThrowStatement.class);
        for (ASTThrowStatement throwStatement : lstThrowStatements) {
            SimpleNode sn = (SimpleNode) throwStatement.jjtGetChild(0).jjtGetChild(0);
            if (sn.getClass().equals(ASTCastExpression.class)) {
                ASTPrimaryExpression expr = (ASTPrimaryExpression) sn.jjtGetChild(1);
                if (expr.jjtGetNumChildren() > 1 && expr.jjtGetChild(1).getClass().equals(ASTPrimaryPrefix.class)) {
                    RuleContext ctx = (RuleContext) data;
                    addViolation(ctx, throwStatement);
                }
                continue;
            }
            ASTArgumentList args = throwStatement.getFirstChildOfType(ASTArgumentList.class);

            if (args != null) {
                ck(data, target, throwStatement, args);
            } else {
                SimpleNode child = (SimpleNode) throwStatement.jjtGetChild(0);
                while (child != null && child.jjtGetNumChildren() > 0
                        && !child.getClass().equals(ASTName.class)) {
                    child = (SimpleNode) child.jjtGetChild(0);
                }
                if (child != null){
                    if( child.getClass().equals(ASTName.class) && (!target.equals(child.getImage()) && !child.hasImageEqualTo(target + ".fillInStackTrace"))) {
                        Map<VariableNameDeclaration, List<NameOccurrence>> vars = ((ASTName) child).getScope().getVariableDeclarations();
	                    for (VariableNameDeclaration decl: vars.keySet()) {
	                        args = ((SimpleNode) decl.getNode().jjtGetParent())
	                                .getFirstChildOfType(ASTArgumentList.class);
	                        if (args != null) {
	                            ck(data, target, throwStatement, args);
	                        }
	                    }
                    } else if(child.getClass().equals(ASTClassOrInterfaceType.class)){
                       addViolation(data, throwStatement);
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    private void ck(Object data, String target, ASTThrowStatement throwStatement,
                    ASTArgumentList args) {
        boolean match = false;
        nameNodes.clear();
        args.findChildrenOfType(ASTName.class, nameNodes);
        for (ASTName nameNode : nameNodes) {
            if (target.equals(nameNode.getImage())) {
                match = true;
                break;
            }
        }
        if (!match) {
            RuleContext ctx = (RuleContext) data;
            addViolation(ctx, throwStatement);
        }
    }
}
