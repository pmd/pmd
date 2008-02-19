package net.sourceforge.pmd.rules.junit;

import java.util.List;

import net.sourceforge.pmd.AbstractJavaRule;
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
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.typeresolution.TypeHelper;

@SuppressWarnings("PMD.AvoidCatchingThrowable") // Don't think we can otherwise here...
public abstract class AbstractJUnitRule extends AbstractJavaRule {

    public static Class junit4Class = null;

    public static Class junit3Class = null;

    private boolean isJUnit3Class;
    private boolean isJUnit4Class;


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

    	isJUnit3Class = isJUnit4Class = false;

    	isJUnit3Class = isJUnit3Class(node);
    	if (!isJUnit3Class) {
    		isJUnit4Class = isJUnit4Class(node);
    	}

        if(isJUnit3Class || isJUnit4Class){
            return super.visit(node, data);
        }
        return data;
    }

    public boolean isJUnitMethod(ASTMethodDeclaration method, Object data) {

        if (!method.isPublic() || method.isAbstract() || method.isNative() || method.isStatic()) {
            return false; // skip various inapplicable method variations
        }

        if (isJUnit3Class) {
        	return isJUnit3Method(method);
        }
        else {
        	return isJUnit4Method(method);
        }
    }

    private boolean isJUnit4Method(ASTMethodDeclaration method){
    	return doesNodeContainJUnitAnnotation((SimpleNode)method.jjtGetParent());
    }

    private boolean isJUnit3Method(ASTMethodDeclaration method) {
    	Node node = method.jjtGetChild(0);
        if (node instanceof ASTTypeParameters) {
            node = method.jjtGetChild(1);
        }
        return ((ASTResultType) node).isVoid() && method.getMethodName().startsWith("test");
    }

    private boolean isJUnit3Class(ASTCompilationUnit node) {
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

    private boolean isJUnit4Class(ASTCompilationUnit node){
        return doesNodeContainJUnitAnnotation(node);
    }

    private boolean doesNodeContainJUnitAnnotation(SimpleNode node) {
    	List<ASTMarkerAnnotation> lstAnnotations = node.findChildrenOfType(ASTMarkerAnnotation.class);
        for(ASTMarkerAnnotation annotation : lstAnnotations){
        	if(annotation.getType() == null){
                ASTName name = (ASTName)annotation.jjtGetChild(0);
                if("Test".equals(name.getImage())){
                    return true;
                }
            }
            else if(annotation.getType().equals(junit4Class)){
                return true;
            }
        }
        return false;
    }

}
