/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.comments;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaAccessNode;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.properties.StringProperty;

/**
 * Check for Methods, Fields and Nested Classes that have a default access
 * modifier
 *
 * @author Damián Techeira
 */
public class CommentDefaultAccessModifierRule extends AbstractCommentRule {

    private static final StringProperty REGEX_DESCRIPTOR = new StringProperty("regex", "Regular expression", "", 1.0f);
    private static final String MESSAGE = "To avoid mistakes add a comment "
            + "at the beginning of the %s %s if you want a default access modifier";
    private final Set<Integer> interestingLineNumberComments = new HashSet<Integer>();

    public CommentDefaultAccessModifierRule() {
        definePropertyDescriptor(REGEX_DESCRIPTOR);
    }

    public CommentDefaultAccessModifierRule(final String regex) {
        this();
        setRegex(regex);
    }

    public void setRegex(final String regex) {
        setProperty(CommentDefaultAccessModifierRule.REGEX_DESCRIPTOR, regex);
    }

    @Override
    public Object visit(final ASTCompilationUnit node, final Object data) {
        interestingLineNumberComments.clear();
        final List<Comment> comments = node.getComments();
        for (final Comment comment : comments) {
            if (comment.getImage().matches(getProperty(REGEX_DESCRIPTOR).trim())) {
                interestingLineNumberComments.add(comment.getBeginLine());
            }
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(final ASTMethodDeclaration decl, final Object data) {
        if (shouldReport(decl)) {
            addViolationWithMessage(data, decl,
                    String.format(MESSAGE, decl.getFirstChildOfType(ASTMethodDeclarator.class).getImage(), "method"));
        }
        return super.visit(decl, data);
    }

    @Override
    public Object visit(final ASTFieldDeclaration decl, final Object data) {
        if (shouldReport(decl)) {
            addViolationWithMessage(data, decl, String.format(MESSAGE,
                    decl.getFirstDescendantOfType(ASTVariableDeclaratorId.class).getImage(), "field"));
        }
        return super.visit(decl, data);
    }

    @Override
    public Object visit(final ASTClassOrInterfaceDeclaration decl, final Object data) {
        // check for nested classes
        if (decl.isNested() && shouldReport(decl)) {
            addViolationWithMessage(data, decl, String.format(MESSAGE, decl.getImage(), "nested class"));
        }
        return super.visit(decl, data);
    }

    @Override
    public Object visit(final ASTConstructorDeclaration decl, Object data) {
        if (shouldReport(decl)) {
            addViolationWithMessage(data, decl, String.format(MESSAGE, decl.getImage(), "constructor"));
        }
        return super.visit(decl, data);
    }

    private boolean shouldReport(final AbstractJavaAccessNode decl) {
        List<ASTClassOrInterfaceDeclaration> parentClassOrInterface = decl
                .getParentsOfType(ASTClassOrInterfaceDeclaration.class);
        // ignore if is a Interface
        return (!parentClassOrInterface.isEmpty() && !parentClassOrInterface.get(0).isInterface())
                // check if the field/method/nested class has a default access
                // modifier
                && decl.isPackagePrivate()
                // if is a default access modifier check if there is a comment
                // in this line
                && !interestingLineNumberComments.contains(decl.getBeginLine())
                // that it is not annotated with @VisibleForTesting
                && hasNoVisibleForTestingAnnotation(decl);
    }

    private boolean hasNoVisibleForTestingAnnotation(AbstractJavaAccessNode decl) {
        boolean result = true;
        ASTClassOrInterfaceBodyDeclaration parent = decl.getFirstParentOfType(ASTClassOrInterfaceBodyDeclaration.class);
        if (parent != null) {
            List<ASTAnnotation> annotations = parent.findChildrenOfType(ASTAnnotation.class);
            for (ASTAnnotation annotation : annotations) {
                List<ASTName> names = annotation.findDescendantsOfType(ASTName.class);
                for (ASTName name : names) {
                    if (name.hasImageEqualTo("VisibleForTesting")) {
                        result = false;
                        break;
                    }
                }
                if (result == false) {
                    break;
                }
            }
        }
        return result;
    }
}
