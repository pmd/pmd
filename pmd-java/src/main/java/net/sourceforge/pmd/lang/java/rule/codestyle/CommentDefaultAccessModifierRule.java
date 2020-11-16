/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTModifierList;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
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
    private static final PropertyDescriptor<Boolean> TOP_LEVEL_TYPES = PropertyFactory.booleanProperty("checkTopLevelTypes")
            .desc("Check for default access modifier in top-level classes, annotations, and enums")
            .defaultValue(false).build();

    public CommentDefaultAccessModifierRule() {
        definePropertyDescriptor(REGEX_DESCRIPTOR);
        definePropertyDescriptor(TOP_LEVEL_TYPES);
    }

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        Collection<String> ignoredStrings = new ArrayList<>();
        ignoredStrings.add("com.google.common.annotations.VisibleForTesting");
        ignoredStrings.add("android.support.annotation.VisibleForTesting");
        return ignoredStrings;
    }

    @Override
    public Object visit(final ASTMethodDeclaration decl, final Object data) {
        if (shouldReport(decl)) {
            addViolation(data, decl, decl.getName(), "method");
        }
        return super.visit(decl, data);
    }

    @Override
    public Object visit(final ASTFieldDeclaration decl, final Object data) {
        if (shouldReport(decl)) {
            addViolation(data, decl, decl.getVarIds().firstOrThrow().getName(), "field");
        }
        return super.visit(decl, data);
    }

    @Override
    public Object visit(final ASTAnnotationTypeDeclaration decl, final Object data) {
        if (!decl.isNested() && shouldReportTypeDeclaration(decl)) { // check for top-level annotation declarations
            addViolation(data, decl, decl.getSimpleName(), "top-level annotation");
        }
        return super.visit(decl, data);
    }

    @Override
    public Object visit(final ASTEnumDeclaration decl, final Object data) {
        if (!decl.isNested() && shouldReportTypeDeclaration(decl)) { // check for top-level enums
            addViolation(data, decl, decl.getSimpleName(), "top-level enum");
        }
        return super.visit(decl, data);
    }

    @Override
    public Object visit(final ASTClassOrInterfaceDeclaration decl, final Object data) {
        if (decl.isNested() && shouldReport(decl)) { // check for nested classes
            addViolation(data, decl, decl.getSimpleName(), "nested class");
        } else if (!decl.isNested() && shouldReportTypeDeclaration(decl)) { // and for top-level ones
            addViolation(data, decl, decl.getSimpleName(), "top-level class");
        }
        return super.visit(decl, data);
    }

    @Override
    public Object visit(final ASTConstructorDeclaration decl, Object data) {
        if (shouldReport(decl)) {
            addViolation(data, decl, decl.getName(), "constructor");
        }
        return super.visit(decl, data);
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
        return decl.isPackagePrivate()
            // if is a default access modifier check if there is a comment
            // in this line
            && !hasOkComment(decl)
            // that it is not annotated with e.g. @VisibleForTesting
            && !hasIgnoredAnnotation(decl);
    }

    private boolean hasOkComment(AccessNode node) {
        ASTModifierList modifiers = node.getModifiers();
        Iterable<JavaccToken> tokens = () -> GenericToken.range(modifiers.getFirstToken(), modifiers.getLastToken());
        Pattern regex = getProperty(REGEX_DESCRIPTOR);
        for (JavaccToken token : tokens) {
            for (JavaccToken special : GenericToken.previousSpecials(token)) {
                if (Comment.isComment(special)) {
                    return regex.matcher(special.getImageCs()).matches();
                }
            }
        }
        return false;
    }

    private boolean shouldReportTypeDeclaration(ASTAnyTypeDeclaration decl) {
        // don't report on interfaces
        return !(decl.isInterface() && !decl.isAnnotation())
            && isMissingComment(decl)
            // either nested or top level and we should check it
            && (decl.isNested() || getProperty(TOP_LEVEL_TYPES));
    }
}
