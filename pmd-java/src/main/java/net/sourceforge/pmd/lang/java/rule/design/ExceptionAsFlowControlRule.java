/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;

/**
 * Catches the use of exception statements as a flow control device.
 *
 * @author Will Sargent
 */
public class ExceptionAsFlowControlRule extends AbstractJavaRulechainRule {

    /**
     * Methods that call the action of a functional interface argument themselves,
     * so that the action runs while the call is evaluated and an exception thrown
     * by it is relayed to the caller. A throw statement in a lambda that is passed
     * to one of them therefore reaches a try statement around the call, exactly
     * like a throw statement outside a lambda. The stream methods that take a
     * lambda argument are in the list as well, since an intermediate operation is
     * evaluated by the terminal operation of the same pipeline.
     */
    private static final Set<String> CALLS_THE_LAMBDA_ARGUMENT = new HashSet<>(Arrays.asList(
        "java.lang.Iterable#forEach",
        "java.util.Iterator#forEachRemaining",
        "java.util.Map#forEach",
        "java.util.Optional#ifPresent",
        "java.util.Optional#ifPresentOrElse",
        "java.util.stream.Stream#allMatch",
        "java.util.stream.Stream#anyMatch",
        "java.util.stream.Stream#collect",
        "java.util.stream.Stream#dropWhile",
        "java.util.stream.Stream#filter",
        "java.util.stream.Stream#flatMap",
        "java.util.stream.Stream#flatMapToDouble",
        "java.util.stream.Stream#flatMapToInt",
        "java.util.stream.Stream#flatMapToLong",
        "java.util.stream.Stream#forEach",
        "java.util.stream.Stream#forEachOrdered",
        "java.util.stream.Stream#generate",
        "java.util.stream.Stream#iterate",
        "java.util.stream.Stream#map",
        "java.util.stream.Stream#mapMulti",
        "java.util.stream.Stream#mapMultiToDouble",
        "java.util.stream.Stream#mapMultiToInt",
        "java.util.stream.Stream#mapMultiToLong",
        "java.util.stream.Stream#mapToDouble",
        "java.util.stream.Stream#mapToInt",
        "java.util.stream.Stream#mapToLong",
        "java.util.stream.Stream#max",
        "java.util.stream.Stream#min",
        "java.util.stream.Stream#noneMatch",
        "java.util.stream.Stream#peek",
        "java.util.stream.Stream#reduce",
        "java.util.stream.Stream#sorted",
        "java.util.stream.Stream#takeWhile"
    ));

    // TODO tests:
    //   - catch a supertype of the exception (unless this is unwanted)
    //   - throw statements with not just a new SomethingExpression, eg a method call returning an exception
    public ExceptionAsFlowControlRule() {
        super(ASTThrowStatement.class);
    }

    @Override
    public Object visit(ASTThrowStatement node, Object data) {
        JTypeMirror thrownType = node.getExpr().getTypeMirror();
        JavaNode parent = node.getParent();
        while (!(parent instanceof ASTBodyDeclaration)) {
            if (parent instanceof ASTLambdaExpression && !isLambdaArgumentCalled((ASTLambdaExpression) parent)) {
                // An exception thrown in a lambda body leaves the lambda, not
                // the enclosing method: it reaches whoever invokes the lambda,
                // which in general is not the try statement around it. See #4815.
                return null;
            }
            if (parent instanceof ASTCatchClause) {
                // if the exception is thrown in a catch block, then we
                // have to ignore the try stmt (jump past it).
                parent = parent.getParent().getParent();
                continue;
            }
            if (parent instanceof ASTTryStatement) {
                // maybe the exception is being caught here.
                for (ASTCatchClause catchClause : ((ASTTryStatement) parent).getCatchClauses()) {
                    if (catchClause.getParameter().getAllExceptionTypes().any(it -> thrownType.isSubtypeOf(it.getTypeMirror()))) {
                        if (!JavaAstUtils.isJustRethrowException(catchClause)) {
                            asCtx(data).addViolation(catchClause, node.getReportLocation().getStartLine());
                            return null;
                        } else {
                            break;
                        }
                    }
                }
            }
            parent = parent.getParent();
        }
        return null;
    }

    /**
     * Returns true if the lambda is an argument of a call that calls that argument
     * itself, so that an exception thrown by the lambda reaches a try statement
     * around the call. Every other lambda is opaque: it may be invoked later, on
     * another thread, or not at all.
     */
    private static boolean isLambdaArgumentCalled(ASTLambdaExpression lambda) {
        JavaNode argumentList = lambda.getParent();
        if (!(argumentList instanceof ASTArgumentList) || !(argumentList.getParent() instanceof ASTMethodCall)) {
            return false;
        }
        JMethodSig method = ((ASTMethodCall) argumentList.getParent()).getMethodType();
        JTypeDeclSymbol declaringType = method.getDeclaringType().getSymbol();
        return declaringType instanceof JClassSymbol
            && CALLS_THE_LAMBDA_ARGUMENT.contains(((JClassSymbol) declaringType).getBinaryName() + "#" + method.getName());
    }

}
