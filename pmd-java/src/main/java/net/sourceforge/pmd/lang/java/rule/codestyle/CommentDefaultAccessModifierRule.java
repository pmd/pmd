/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AbstractAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaAccessNode;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.java.rule.AbstractIgnoredAnnotationRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * Check for Methods, Fields and Nested Classes that have a default access
 * modifier
 * This rule ignores all nodes annotated with @VisibleForTesting by default.
 * Use the ignoredAnnotationsDescriptor property to customize the ignored rules.
 *
 * @author Damián Techeira
 */
public class CommentDefaultAccessModifierRule extends AbstractIgnoredAnnotationRule {

    private static final PropertyDescriptor<Pattern> REGEX_DESCRIPTOR = PropertyFactory.regexProperty("regex")
                                                                                       .desc("Regular expression").defaultValue("\\/\\*\\s+(default|package)\\s+\\*\\/").build();
    private static final String MESSAGE = "To avoid mistakes add a comment "
            + "at the beginning of the %s %s if you want a default access modifier";
    private final Set<Integer> interestingLineNumberComments = new HashSet<>();

    public CommentDefaultAccessModifierRule() {
        definePropertyDescriptor(REGEX_DESCRIPTOR);
    }

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        Collection<String> ignoredStrings = new ArrayList<>();
        ignoredStrings.add("com.google.common.annotations.VisibleForTesting");
        ignoredStrings.add("android.support.annotation.VisibleForTesting");
        return ignoredStrings;
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
    public Object visit(final ASTAnnotationTypeDeclaration decl, final Object data) {
        if (!decl.isNested() && shouldReportTypeDeclaration(decl)) { // check for top-level annotation declarations
            addViolationWithMessage(data, decl, String.format(MESSAGE, decl.getImage(), "top-level annotation"));
        }
        return super.visit(decl, data);
    }

    @Override
    public Object visit(final ASTEnumDeclaration decl, final Object data) {
        if (!decl.isNested() && shouldReportTypeDeclaration(decl)) { // check for top-level enums
            addViolationWithMessage(data, decl, String.format(MESSAGE, decl.getImage(), "top-level enum"));
        }
        return super.visit(decl, data);
    }

    @Override
    public Object visit(final ASTClassOrInterfaceDeclaration decl, final Object data) {
        if (decl.isNested() && shouldReport(decl)) { // check for nested classes
            addViolationWithMessage(data, decl, String.format(MESSAGE, decl.getImage(), "nested class"));
        } else if (!decl.isNested() && shouldReportTypeDeclaration(decl)) { // and for top-level ones
            addViolationWithMessage(data, decl, String.format(MESSAGE, decl.getImage(), "top-level class"));
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

        boolean isConcreteClass = parentClassOrInterface.getTypeKind() == ASTAnyTypeDeclaration.TypeKind.CLASS;

        // ignore if it's an Interface / Annotation
        return isConcreteClass && shouldReportTypeDeclaration(decl);
    }

    private boolean shouldReportTypeDeclaration(final AbstractJavaAccessNode decl) {
        // check if the class/method/field has a default access
        // modifier
        return decl.isPackagePrivate()
                // if is a default access modifier check if there is a comment
                // in this line
                && !interestingLineNumberComments.contains(decl.getBeginLine())
                // that it is not annotated with e.g. @VisibleForTesting
                && !hasIgnoredAnnotation(decl);
    }
}
