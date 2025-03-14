/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import java.util.List;
import java.util.Optional;

import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaComment;


/**
 * The XPath query "//VariableDeclarator[contains(getCommentOn(),
 * '//password')]" will find all variables declared that are annotated with the
 * password comment.
 *
 * @author Andy Throgmorton
 */
public class GetCommentOnFunction extends BaseJavaXPathFunction {


    public static final GetCommentOnFunction INSTANCE = new GetCommentOnFunction();

    protected GetCommentOnFunction() {
        super("getCommentOn");
    }

    @Override
    public Type getResultType() {
        return Type.OPTIONAL_STRING;
    }


    @Override
    public boolean dependsOnContext() {
        return true;
    }


    @Override
    public FunctionCall makeCallExpression() {
        return (contextNode, arguments) -> {
            int codeBeginLine = contextNode.getBeginLine();
            int codeEndLine = contextNode.getEndLine();

            List<JavaComment> commentList = contextNode.ancestorsOrSelf().filterIs(ASTCompilationUnit.class).first().getComments();
            for (JavaComment comment : commentList) {
                FileLocation location = comment.getReportLocation();
                if (location.getStartLine() == codeBeginLine || location.getEndLine() == codeEndLine) {
                    return Optional.of(comment.getText().toString());
                }
            }
            return Optional.<String>empty();
        };
    }
}



