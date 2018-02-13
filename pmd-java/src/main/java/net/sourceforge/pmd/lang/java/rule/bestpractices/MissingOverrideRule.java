/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Stack;

import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;


/**
 * Flags missing @Override annotations.
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
public class MissingOverrideRule extends AbstractJavaRule {

    private final Stack<Class<?>> currentExploredClass = new Stack<>();


    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        currentExploredClass.clear();
        return super.visit(node, data);
    }


    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        currentExploredClass.push(node.getType());
        super.visit(node, data);
        currentExploredClass.pop();

        return data;
    }

    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        currentExploredClass.push(node.getType());
        super.visit(node, data);
        currentExploredClass.pop();

        return data;
    }



    @Override
    public Object visit(ASTAllocationExpression node, Object data) {
        if (node.isAnonymousClass()) {
            currentExploredClass.push(node.getType());
        }
        super.visit(node, data);

        if (node.isAnonymousClass()) {
            currentExploredClass.pop();
        }

        return data;
    }


    @Override
    public Object visit(ASTEnumConstant node, Object data) {
        // FIXME, ASTEnumConstant needs typeres support!
        //        if (node.isAnonymousClass()) {
        //            currentExploredClass.push(node.getType());
        //        }
        super.visit(node, data);

        //        if (node.isAnonymousClass()) {
        //            currentExploredClass.pop();
        //        }

        return data;
    }


    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (currentExploredClass.peek() == null) {
            return super.visit(node, data);
        }

        for (ASTAnnotation annot : node.getDeclaredAnnotations()) {
            if (Override.class.equals(annot.getType())) {
                // we assume the compiler has already checked it, so it's correct
                return super.visit(node, data);
            }
        }

        ASTFormalParameters params = node.getFormalParameters();
        Class<?>[] paramTypes = new Class[params.getParameterCount()];
        int i = 0;
        for (ASTFormalParameter p : params) {
            Class<?> pType = p.getType();
            if (pType == null) {
                // fail, couldn't resolve one parameter
                return super.visit(node, data);
            }

            paramTypes[i++] = pType;
        }

        if (isMethodOverridden(node.getMethodName(), paramTypes, currentExploredClass.peek())) {
            // this method lacks an @Override annotation
            addViolation(data, node, new Object[]{node.getQualifiedName().getOperation()});
        }

        return super.visit(node, data);
    }


    private boolean isMethodOverridden(String name, Class<?>[] paramTypes, final Class<?> exploredType) {
        return isMethodDeclaredInType(name, paramTypes, exploredType, true);
    }


    private boolean isMethodDeclaredInType(String name, final Class<?>[] paramTypes, final Class<?> exploredType, boolean skip) {

        if (!skip) {
            for (Method dm : exploredType.getDeclaredMethods()) {

                if (name.equals(dm.getName()) && Arrays.equals(paramTypes, dm.getParameterTypes())) {
                    return true;
                }
            }
        }

        Class<?> superClass = exploredType.getSuperclass();
        if (superClass != null) {
            if (isMethodDeclaredInType(name, paramTypes, superClass, false)) {
                return true;
            }
        }

        for (Class<?> iface : exploredType.getInterfaces()) {
            if (isMethodDeclaredInType(name, paramTypes, iface, false)) {
                return true;
            }
        }

        return false;
    }


}



