/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AbstractAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaAccessNode;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.java.rule.documentation.AbstractCommentRule;
import net.sourceforge.pmd.properties.RegexProperty;

/**
 * Check for Methods, Fields and Nested Classes that have a default access
 * modifier
 *
 * @author Damián Techeira
 */
public class CommentDefaultAccessModifierRule extends AbstractCommentRule {

    private static final RegexProperty REGEX_DESCRIPTOR = RegexProperty.named("regex")
            .desc("Regular expression").defaultValue("\\/\\*\\s+(default|package)\\s+\\*\\/").uiOrder(1.0f).build();
    private static final String MESSAGE = "To avoid mistakes add a comment "
            + "at the beginning of the %s %s if you want a default access modifier";
    private final Set<Integer> interestingLineNumberComments = new HashSet<Integer>();

    public CommentDefaultAccessModifierRule() {
        definePropertyDescriptor(REGEX_DESCRIPTOR);
    }

    @Override
    public Object visit(final ASTCompilationUnit node, final Object data) {
        interestingLineNumberComments.clear();
        final List<Comment> comments = node.getComments();
        for (final Comment comment : comments) {
            if (getProperty(REGEX_DESCRIPTOR).matcher(comment.getImage()).matches()) {
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
        final AbstractAnyTypeDeclaration parentClassOrInterface = decl
                .getFirstParentOfType(AbstractAnyTypeDeclaration.class);
        
        final boolean isConcreteClass = parentClassOrInterface instanceof ASTClassOrInterfaceDeclaration
                && !((ASTClassOrInterfaceDeclaration) parentClassOrInterface).isInterface();
        final boolean isEnumConstructor = parentClassOrInterface instanceof ASTEnumDeclaration
                && decl instanceof ASTConstructorDeclaration;
        
        // ignore if it's an Interface / Annotation / Enum constructor
        return (isConcreteClass || !isEnumConstructor)
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
                final ASTName name = annotation.getFirstDescendantOfType(ASTName.class);
                if (name.hasImageEqualTo("VisibleForTesting")) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
}
