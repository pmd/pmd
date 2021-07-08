/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaPropertyUtil;
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
public class CommentDefaultAccessModifierRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Pattern> REGEX_DESCRIPTOR = PropertyFactory.regexProperty("regex")
            .desc("Regular expression").defaultValue("\\/\\*\\s+(default|package)\\s+\\*\\/").build();
    private static final PropertyDescriptor<Boolean> TOP_LEVEL_TYPES = PropertyFactory.booleanProperty("checkTopLevelTypes")
            .desc("Check for default access modifier in top-level classes, annotations, and enums")
            .defaultValue(false).build();
    private static final PropertyDescriptor<List<String>> IGNORED_ANNOTS =
        JavaPropertyUtil.ignoredAnnotationsDescriptor(
            "com.google.common.annotations.VisibleForTesting",
            "android.support.annotation.VisibleForTesting"
        );

    private static final String MESSAGE = "To avoid mistakes add a comment at the beginning of the {0} {1} if you want a default access modifier";
    private final Set<Integer> interestingLineNumberComments = new HashSet<>();

    public CommentDefaultAccessModifierRule() {
        super(ASTCompilationUnit.class, ASTMethodDeclaration.class, ASTAnyTypeDeclaration.class, ASTConstructorDeclaration.class, ASTFieldDeclaration.class);
        definePropertyDescriptor(IGNORED_ANNOTS);
        definePropertyDescriptor(REGEX_DESCRIPTOR);
        definePropertyDescriptor(TOP_LEVEL_TYPES);
    }

    @Override
    public Object visit(final ASTCompilationUnit node, final Object data) {
        interestingLineNumberComments.clear();
        for (final Comment comment : node.getComments()) {
            if (getProperty(REGEX_DESCRIPTOR).matcher(comment.getText()).matches()) {
                interestingLineNumberComments.add(comment.getBeginLine());
            }
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(final ASTMethodDeclaration decl, final Object data) {
        if (shouldReport(decl)) {
            report((RuleContext) data, decl, "method", PrettyPrintingUtil.displaySignature(decl));
        }
        return super.visit(decl, data);
    }

    @Override
    public Object visit(final ASTFieldDeclaration decl, final Object data) {
        if (shouldReport(decl)) {
            report((RuleContext) data, decl, "field", decl.getVarIds().firstOrThrow().getName());
        }
        return super.visit(decl, data);
    }

    @Override
    public Object visit(final ASTAnnotationTypeDeclaration decl, final Object data) {
        if (!decl.isNested() && shouldReportTypeDeclaration(decl)) { // check for top-level annotation declarations
            report((RuleContext) data, decl, "top-level annotation", decl.getSimpleName());
        }
        return super.visit(decl, data);
    }

    @Override
    public Object visit(final ASTEnumDeclaration decl, final Object data) {
        if (!decl.isNested() && shouldReportTypeDeclaration(decl)) { // check for top-level enums
            report((RuleContext) data, decl, "top-level enum", decl.getSimpleName());
        }
        return super.visit(decl, data);
    }

    @Override
    public Object visit(final ASTClassOrInterfaceDeclaration decl, final Object data) {
        if (decl.isNested() && shouldReport(decl)) { // check for nested classes
            report((RuleContext) data, decl, "nested class", decl.getSimpleName());
        } else if (!decl.isNested() && shouldReportTypeDeclaration(decl)) { // and for top-level ones
            report((RuleContext) data, decl, "top-level class", decl.getSimpleName());
        }
        return super.visit(decl, data);
    }

    @Override
    public Object visit(final ASTConstructorDeclaration decl, Object data) {
        if (shouldReport(decl)) {
            report((RuleContext) data, decl, "constructor", PrettyPrintingUtil.displaySignature(decl));
        }
        return super.visit(decl, data);
    }

    private void report(RuleContext data, AccessNode decl, String kind, String description) {
        addViolationWithMessage(data, decl, MESSAGE, new String[] {kind, description, });
    }

    private boolean shouldReport(final AccessNode decl) {
        final ASTAnyTypeDeclaration enclosing = decl.getEnclosingType();

        boolean isConcreteClass = !enclosing.isInterface() && !enclosing.isEnum();

        // ignore if it's inside an interface / Annotation
        return isConcreteClass && isMissingComment(decl);
    }

    private boolean isMissingComment(AccessNode decl) {
        // check if the class/method/field has a default access
        // modifier
        return decl.getVisibility() == Visibility.V_PACKAGE
            // if is a default access modifier check if there is a comment
            // in this line
            && !hasOkComment(decl)
            // that it is not annotated with e.g. @VisibleForTesting
            && isNotIgnored(decl);
    }

    private boolean isNotIgnored(AccessNode decl) {
        return getProperty(IGNORED_ANNOTS).stream().noneMatch(decl::isAnnotationPresent);
    }

    private boolean hasOkComment(AccessNode node) {
        Pattern regex = getProperty(REGEX_DESCRIPTOR);
        return Comment.getLeadingComments(node)
                      .anyMatch(it -> regex.matcher(it.getImageCs()).matches());
    }

    private boolean shouldReportTypeDeclaration(ASTAnyTypeDeclaration decl) {
        // don't report on interfaces
        return !(decl.isRegularInterface() && !decl.isAnnotation())
            && isMissingComment(decl)
            // either nested or top level and we should check it
            && (decl.isNested() || getProperty(TOP_LEVEL_TYPES));
    }
}
