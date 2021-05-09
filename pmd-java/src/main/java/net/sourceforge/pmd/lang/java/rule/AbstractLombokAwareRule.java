/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.util.Collection;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;


/**
 * Base class for rules, that should ignore classes/fields that are annotated
 * with Lombok annotations.
 *
 * @author Andreas Dangel
 * @deprecated Internal API
 */
@Deprecated
@InternalApi
public class AbstractLombokAwareRule extends AbstractIgnoredAnnotationRule {

    private boolean lombokImported = false;
    private boolean classHasLombokAnnotation = false;
    private static final String LOMBOK_PACKAGE = "lombok";

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        return JavaRuleUtil.LOMBOK_ANNOTATIONS;
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        lombokImported = false;
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        if (!lombokImported && node.getImage() != null & node.getImage().startsWith(LOMBOK_PACKAGE)) {
            lombokImported = true;
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        boolean oldValue = classHasLombokAnnotation;
        classHasLombokAnnotation = hasLombokAnnotation(node);
        Object result = super.visit(node, data);
        classHasLombokAnnotation = oldValue;
        return result;
    }

    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        boolean oldValue = classHasLombokAnnotation;
        classHasLombokAnnotation = hasLombokAnnotation(node);
        Object result = super.visit(node, data);
        classHasLombokAnnotation = oldValue;
        return result;
    }

    /**
     * Checks whether the given node is annotated with any lombok annotation.
     * The node should be annotateable.
     *
     * @param node
     *            the Annotatable node to check
     * @return <code>true</code> if a lombok annotation has been found
     */
    protected boolean hasLombokAnnotation(Annotatable node) {
        return JavaRuleUtil.LOMBOK_ANNOTATIONS.stream().anyMatch(node::isAnnotationPresent);
    }
}
