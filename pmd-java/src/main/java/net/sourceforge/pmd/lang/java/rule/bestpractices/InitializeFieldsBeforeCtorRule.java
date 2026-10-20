/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSuperExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThisExpression;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.AssignmentOp;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;

/**
 * Reports field assignments in a constructor that are executed after the
 * superclass constructor, but could be moved before the {@code super(...)} call.
 * Since Java 25 (JEP 513, Flexible Constructor Bodies), a constructor may assign
 * fields of its own class before calling the superclass constructor.
 *
 * <p>Only the assignments that directly follow the (explicit or implicit)
 * {@code super(...)} call are reported. The rule stops at the first other
 * statement, so that moving the reported assignments does not change the
 * order of other side effects.
 */
public class InitializeFieldsBeforeCtorRule extends AbstractJavaRulechainRule {

    public InitializeFieldsBeforeCtorRule() {
        super(ASTConstructorDeclaration.class);
    }

    @Override
    public Object visit(ASTConstructorDeclaration ctor, Object data) {
        ASTTypeDeclaration enclosingType = ctor.getEnclosingType();
        // Only classes that extend another class: there a superclass constructor,
        // which might call overridden methods, runs before the fields are assigned.
        if (!(enclosingType instanceof ASTClassDeclaration) || enclosingType.getSuperClassTypeNode() == null) {
            return null;
        }

        ASTBlock body = ctor.getBody();
        ASTExplicitConstructorInvocation invocation = body.children(ASTExplicitConstructorInvocation.class).first();
        if (invocation != null && invocation.isThis()) {
            // this(...) delegates to another constructor, which is checked on its own
            return null;
        }

        // Without an explicit invocation, the implicit super() call is executed first.
        boolean afterSuper = invocation == null;
        for (ASTStatement statement : body) {
            if (!afterSuper) {
                afterSuper = statement == invocation;
                continue;
            }
            String fieldName = getMovableFieldAssignment(statement);
            if (fieldName == null) {
                break;
            }
            asCtx(data).addViolation(statement, fieldName);
        }
        return null;
    }

    /**
     * Returns the name of the field, if the statement is a simple assignment
     * to a field of this class, that could be moved before the superclass
     * constructor call. Returns null otherwise.
     */
    private static String getMovableFieldAssignment(ASTStatement statement) {
        if (!(statement instanceof ASTExpressionStatement)) {
            return null;
        }
        ASTExpression expr = ((ASTExpressionStatement) statement).getExpr();
        if (!(expr instanceof ASTAssignmentExpression)) {
            return null;
        }
        ASTAssignmentExpression assignment = (ASTAssignmentExpression) expr;
        if (assignment.getOperator() != AssignmentOp.ASSIGN) {
            // compound assignments like += read the field first
            return null;
        }
        ASTExpression left = assignment.getLeftOperand();
        // a non-static field declared in this class (not inherited), as "x" or "this.x"
        if (!JavaAstUtils.isThisFieldAccess(left)) {
            return null;
        }
        JFieldSymbol field = (JFieldSymbol) ((ASTNamedReferenceExpr) left).getReferencedSym();
        ASTVariableId fieldDeclaration = field.tryGetNode();
        if (fieldDeclaration == null || fieldDeclaration.getInitializer() != null) {
            // fields with an initializer must not be assigned before super()
            return null;
        }
        if (!canBeEvaluatedBeforeSuper(assignment.getRightOperand())) {
            return null;
        }
        return field.getSimpleName();
    }

    /**
     * Whether the expression does not use the instance under construction.
     * Code before super(...) must not refer to {@code this}, neither explicitly
     * nor implicitly.
     */
    private static boolean canBeEvaluatedBeforeSuper(ASTExpression expression) {
        for (Node node : expression.descendantsOrSelf()) {
            if (node instanceof ASTThisExpression || node instanceof ASTSuperExpression) {
                return false;
            }
            if (node instanceof ASTMethodCall && isImplicitThisCall((ASTMethodCall) node)) {
                return false;
            }
            if (node instanceof ASTNamedReferenceExpr && isReadOfInstanceField((ASTNamedReferenceExpr) node)) {
                return false;
            }
            if (node instanceof ASTConstructorCall && mayCaptureThis((ASTConstructorCall) node)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Whether this is an unqualified call of an instance method, e.g. {@code foo()}.
     * Such a call uses {@code this}, also if the method is inherited.
     * Qualified calls like {@code this.foo()} are covered by the check for
     * {@code this} and {@code super} expressions.
     */
    private static boolean isImplicitThisCall(ASTMethodCall call) {
        if (call.getQualifier() != null) {
            return false;
        }
        JExecutableSymbol method = call.getMethodType().getSymbol();
        // unresolved: be conservative
        return method.isUnresolved() || !method.isStatic();
    }

    private static boolean isReadOfInstanceField(ASTNamedReferenceExpr reference) {
        // unresolved: be conservative
        return reference.getReferencedSym() == null
            || JavaAstUtils.isRefToFieldOfThisInstance(reference);
    }

    private static boolean mayCaptureThis(ASTConstructorCall call) {
        if (call.isAnonymousClass()) {
            return true;
        }
        if (call.getQualifier() != null) {
            // outer.new Inner(): the enclosing instance is given explicitly
            return false;
        }
        JTypeDeclSymbol symbol = call.getTypeMirror().getSymbol();
        if (!(symbol instanceof JClassSymbol)) {
            // unresolved, be conservative
            return true;
        }
        JClassSymbol classSymbol = (JClassSymbol) symbol;
        // local classes and inner (non-static member) classes may use "this"
        return classSymbol.isLocalClass()
            || (classSymbol.getEnclosingClass() != null && !classSymbol.isStatic());
    }
}
