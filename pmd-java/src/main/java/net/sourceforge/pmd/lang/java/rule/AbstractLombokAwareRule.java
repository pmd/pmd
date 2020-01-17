/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.Annotatable;


/**
 * Base class for rules, that should ignore classes/fields that are annotated
 * with Lombok annotations.
 *
 * @author Andreas Dangel
 */
public class AbstractLombokAwareRule extends AbstractIgnoredAnnotationRule {

    private boolean lombokImported = false;
    private boolean classHasLombokAnnotation = false;
    private static final String LOMBOK_PACKAGE = "lombok";
    private static final Set<String> LOMBOK_ANNOTATIONS = new HashSet<>();

    static {
        LOMBOK_ANNOTATIONS.add("lombok.Data");
        LOMBOK_ANNOTATIONS.add("lombok.Getter");
        LOMBOK_ANNOTATIONS.add("lombok.Setter");
        LOMBOK_ANNOTATIONS.add("lombok.Value");
        LOMBOK_ANNOTATIONS.add("lombok.RequiredArgsConstructor");
        LOMBOK_ANNOTATIONS.add("lombok.AllArgsConstructor");
        LOMBOK_ANNOTATIONS.add("lombok.NoArgsConstructor");
        LOMBOK_ANNOTATIONS.add("lombok.Builder");
    }

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        return LOMBOK_ANNOTATIONS;
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        lombokImported = false;
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        ASTName name = node.getFirstChildOfType(ASTName.class);
        if (!lombokImported && name != null && name.getImage() != null & name.getImage().startsWith(LOMBOK_PACKAGE)) {
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
     * Returns whether there have been class level Lombok annotations found.
     * Note: this can only be queried after the class declaration node has been
     * processed.
     *
     * @return <code>true</code> if a lombok annotation at the class level has
     *         been found
     */
    protected boolean hasClassLombokAnnotation() {
        return classHasLombokAnnotation;
    }

    /**
     * @deprecated As of release 6.2.0, replaced by {@link #hasLombokAnnotation(Annotatable)}
     * Checks whether the given node is annotated with any lombok annotation.
     * The node can be any node, e.g. class declaration or field declaration.
     *
     * @param node
     *            the node to check
     * @return <code>true</code> if a lombok annotation has been found
     */
    @Deprecated
    protected boolean hasLombokAnnotation(Node node) {
        boolean result = false;
        Node parent = node.getParent();
        List<ASTAnnotation> annotations = parent.findChildrenOfType(ASTAnnotation.class);
        for (ASTAnnotation annotation : annotations) {
            ASTName name = annotation.getFirstDescendantOfType(ASTName.class);
            if (name != null) {
                String annotationName = name.getImage();
                if (lombokImported) {
                    if (LOMBOK_ANNOTATIONS.contains(annotationName)) {
                        result = true;
                    }
                } else {
                    if (annotationName.startsWith(LOMBOK_PACKAGE + ".")) {
                        String shortName = annotationName.substring(LOMBOK_PACKAGE.length() + 1);
                        if (LOMBOK_ANNOTATIONS.contains(shortName)) {
                            result = true;
                        }
                    }
                }
            }
        }
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
        return node.isAnyAnnotationPresent(LOMBOK_ANNOTATIONS);
    }
}
