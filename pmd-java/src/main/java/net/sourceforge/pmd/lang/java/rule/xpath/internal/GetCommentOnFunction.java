/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaComment;
import net.sourceforge.pmd.lang.rule.xpath.internal.AstElementNode;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.EmptyAtomicSequence;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;


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
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[0];
    }


    @Override
    public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return SequenceType.OPTIONAL_STRING;
    }


    @Override
    public boolean dependsOnFocus() {
        return true;
    }


    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {
            @Override
            public Sequence call(XPathContext context, Sequence[] arguments) {
                Node contextNode = ((AstElementNode) context.getContextItem()).getUnderlyingNode();

                int codeBeginLine = contextNode.getBeginLine();
                int codeEndLine = contextNode.getEndLine();

                List<JavaComment> commentList = contextNode.getFirstParentOfType(ASTCompilationUnit.class).getComments();
                for (JavaComment comment : commentList) {
                    if (comment.getBeginLine() == codeBeginLine || comment.getEndLine() == codeEndLine) {
                        return new StringValue(comment.getText());
                    }
                }
                return EmptyAtomicSequence.INSTANCE;
            }

        };
    }
}



