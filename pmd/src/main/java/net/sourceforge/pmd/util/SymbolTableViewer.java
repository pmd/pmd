/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;

public class SymbolTableViewer extends JavaParserVisitorAdapter {

    private int depth;
    
    public Object visit(ASTCompilationUnit node, Object data) {
        depth++;
        System.out.println(spaces() + node.getScope());
        super.visit(node, data);
        depth--;
        return data;
    }

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        depth++;
        System.out.println(spaces() + node.getScope());
        super.visit(node, data);
        depth--;
        return data;
    }

    private String spaces() {
        StringBuffer sb = new StringBuffer(depth);
        for (int i=0; i<depth; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

/*
    public Object visit(ASTClassOrInterfaceBodyDeclaration node, Object data) {
        System.out.println(node.getScope());
        node.dump("");
        return super.visit(node, data);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object visit(ASTEnumDeclaration node, Object data) {
        System.out.println(node.getScope());
        node.dump("");
        return super.visit(node, data);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object visit(ASTBlock node, Object data) {
        System.out.println(node.getScope());
        node.dump("");
        return super.visit(node, data);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object visit(ASTTryStatement node, Object data) {
        System.out.println(node.getScope());
        node.dump("");
        return super.visit(node, data);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object visit(ASTCatchStatement node, Object data) {
        System.out.println(node.getScope());
        node.dump("");
        return super.visit(node, data);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object visit(ASTFinallyStatement node, Object data) {
        System.out.println(node.getScope());
        node.dump("");
        return super.visit(node, data);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        System.out.println(node.getScope());
        node.dump("");
        return super.visit(node, data);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object visit(ASTConstructorDeclaration node, Object data) {
        System.out.println(node.getScope());
        node.dump("");
        return super.visit(node, data);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object visit(ASTSwitchLabel node, Object data) {
        System.out.println(node.getScope());
        node.dump("");
        return super.visit(node, data);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object visit(ASTIfStatement node, Object data) {
        System.out.println(node.getScope());
        node.dump("");
        return super.visit(node, data);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object visit(ASTForStatement node, Object data) {
        System.out.println(node.getScope());
        node.dump("");
        return super.visit(node, data);  //To change body of implemented methods use File | Settings | File Templates.
    }
*/

}
