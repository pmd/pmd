/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.xpath;

import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.XPathFunctionContext;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.Comment;

/**
 * The XPath query "//VariableDeclarator[contains(getCommentOn(),
 * '//password')]" will find all variables declared that are annotated with the
 * password comment.
 *
 * @author Andy Throgmorton
 */
@InternalApi
@Deprecated
public class GetCommentOnFunction implements Function {

    public static void registerSelfInSimpleContext() {
        // see http://jaxen.org/extensions.html
        ((SimpleFunctionContext) XPathFunctionContext.getInstance()).registerFunction(null, "getCommentOn",
                new GetCommentOnFunction());
    }

    @Override
    public Object call(Context context, List args) throws FunctionCallException {
        if (!args.isEmpty()) {
            return Boolean.FALSE;
        }
        Node n = (Node) context.getNodeSet().get(0);
        if (n instanceof AbstractNode) {
            int codeBeginLine = ((AbstractNode) n).getBeginLine();
            int codeEndLine = ((AbstractNode) n).getEndLine();

            List<Comment> commentList = ((AbstractNode) n).getFirstParentOfType(ASTCompilationUnit.class).getComments();
            for (Comment comment : commentList) {
                if (comment.getBeginLine() == codeBeginLine || comment.getEndLine() == codeEndLine) {
                    return comment.getImage();
                }
            }
        }
        return Boolean.FALSE;
    }
}
