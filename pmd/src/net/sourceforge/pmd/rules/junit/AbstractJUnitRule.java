package net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTExtendsList;
import net.sourceforge.pmd.ast.ASTMarkerAnnotation;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTResultType;
import net.sourceforge.pmd.ast.ASTTypeParameters;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.typeresolution.TypeHelper;

import java.util.List;

public abstract class AbstractJUnitRule extends AbstractRule {
    
    public static Class junit4Class = null;

    public static Class junit3Class = null;

    static {
        try {
            junit4Class = Class.forName("org.junit.Test");
        } catch (Throwable t) {
            junit4Class = null;
        }

        try {
            junit3Class = Class.forName("junit.framework.TestCase");
        } catch (Throwable t) {
            junit3Class = null;
        }
    }
    
    public Object visit(ASTCompilationUnit node, Object data){
    	boolean isJunit3Class = isJUnit3Class(node);
        boolean isJunit4Class = isJUnit4Class(node);
        if(isJunit3Class || isJunit4Class){
            return super.visit(node, data);
        }
        return data;
    }
    
    public boolean isJUnitMethod(ASTMethodDeclaration method, Object data) {
        if (!method.isPublic() || method.isAbstract() || method.isNative() || method.isStatic()) {
            return false; // skip various inapplicable method variations
        }

        Node node = method.jjtGetChild(0);
        if (node instanceof ASTTypeParameters) {
            node = method.jjtGetChild(1);
        }
        return ((ASTResultType) node).isVoid() && method.getMethodName().startsWith("test");
    }
    
    public boolean isJUnit4Method(){
        return false;
    }
    
    public boolean isJUnit3Class(ASTCompilationUnit node) {
        if (node.getType() != null && TypeHelper.isA(node, junit3Class)) {
            return true;
        } else if (node.getType() == null) {
            ASTClassOrInterfaceDeclaration cid = node.getFirstChildOfType(ASTClassOrInterfaceDeclaration.class);
            if (cid == null) {
                return false;
            }
            ASTExtendsList extendsList = cid.getFirstChildOfType(ASTExtendsList.class);
            if(extendsList == null){
                return false;
            }

            if(((ASTClassOrInterfaceType)extendsList.jjtGetChild(0)).getImage().endsWith("TestCase")){
                return true;
            }
            String className = cid.getImage();
            return className.endsWith("Test");
        }
        return false;
    }

    public boolean isJUnit4Class(ASTCompilationUnit node){
        List<ASTMarkerAnnotation> lstAnnotations = node.findChildrenOfType(ASTMarkerAnnotation.class);
        for(ASTMarkerAnnotation annotation : lstAnnotations){
            if(annotation.getType() == null){
                ASTName name = (ASTName)annotation.jjtGetChild(0);
                if("Test".equals(name.getImage())){
                    return true;
                }
            } else if(annotation.getType().equals(junit4Class)){
                return true;
            }
        }
        return false;
    }
}
