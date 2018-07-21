/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class MethodTypeAndNameIsInconsistentRule extends AbstractJavaRule {
    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        String nameOfMethod = node.getMethodName();
        ASTType t = null;
        ASTResultType rt = null;
        if (node.getResultType().jjtGetNumChildren() != 0) { // for non-void methods
            t = node.getResultType().getFirstChildOfType(ASTType.class);
            rt = node.getResultType();
        } else {
            rt = node.getResultType(); // for void methods
        }

        /**************** Should Return Boolean As Name Suggests *********************/

        if (nameOfMethod.startsWith("is") && nameOfMethod.length() > 2 && nameOfMethod.charAt(2) > 64
                && nameOfMethod.charAt(2) < 91) {
            // after is a capital letter expected to not addViolation to a method
            // called isotherm or so

            if (t != null && !(t.getType().getName().equals("boolean"))) {
                addViolation(data, node);
            }
        } else if ((nameOfMethod.startsWith("has") || nameOfMethod.startsWith("can")) && nameOfMethod.length() > 3
                && nameOfMethod.charAt(3) > 64 && nameOfMethod.charAt(3) < 91) {
            if (t != null && !(t.getType().getName().equals("boolean"))) {
                addViolation(data, node);
            }
        } else if ((nameOfMethod.startsWith("have") || nameOfMethod.startsWith("will")) && nameOfMethod.length() > 4
                && nameOfMethod.charAt(4) > 64 && nameOfMethod.charAt(4) < 91) {
            if (t != null && !(t.getType().getName().equals("boolean"))) {
                addViolation(data, node);
            }
        } else if (nameOfMethod.startsWith("should") && nameOfMethod.length() > 6 && nameOfMethod.charAt(6) > 64
                && nameOfMethod.charAt(6) < 91) {
            if (t != null && !(t.getType().getName().equals("boolean"))) {
                addViolation(data, node);
            }
        }

        /***************************************************************************/

        /**************** Should Return Void As Name Suggests *********************/

        if (nameOfMethod.startsWith("set") && nameOfMethod.length() > 3 && nameOfMethod.charAt(3) > 64
                && nameOfMethod.charAt(3) < 91) {
            // after set a capital letter expected to not addViolation to a method
            // called settings or so

            if (t != null && !(rt.isVoid())) { // set method shouldnt return any type except void linguistically
                addViolation(data, node);
            }
        }

        /***************************************************************************/

        /******* Should Return A Type As Name Suggests But It Returns void *********/

        if (nameOfMethod.startsWith("get") && nameOfMethod.length() > 3 && nameOfMethod.charAt(3) > 64
                && nameOfMethod.charAt(3) < 91) {
            // after get a capital letter expected to not addViolation to a method
            // called getaways or so
            if (rt.isVoid()) { // get method shouldnt return void linguistically
                addViolation(data, node);
            }
        }

        if (nameOfMethod.contains("To")) { // To in the middle somewhere
            if (rt.isVoid()) { // a transform method shouldnt return void linguistically
                addViolation(data, node);
            }
        } else if (nameOfMethod.startsWith("to") && nameOfMethod.length() > 2 && nameOfMethod.charAt(2) > 64
                && nameOfMethod.charAt(2) < 91) {
            // after to a capital letter expected to not addViolation to a method
            // called tokenize or so
            if (rt.isVoid()) { // a transform method shouldnt return void linguistically
                addViolation(data, node);
            }
        }

        /***************************************************************************/

        return super.visit(node, data);
    }
}
