/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;


/**
 * Detects fields that are declared after methods, constructors, etc. It was a
 * XPath rule, but the Java version is much faster. The XPath rule for
 * reference:
 *
 * <pre>
//ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration/FieldDeclaration
[not(.//ClassOrInterfaceBodyDeclaration) or $ignoreAnonymousClassDeclarations = 'false']
[../preceding-sibling::ClassOrInterfaceBodyDeclaration
    [  count(ClassOrInterfaceDeclaration) &gt; 0
    or count(ConstructorDeclaration) &gt; 0
    or count(MethodDeclaration) &gt; 0
    or count(AnnotationTypeDeclaration) &gt; 0
    or ($ignoreEnumDeclarations = 'false' and count(EnumDeclaration) &gt; 0)
    ]
]
 * </pre>
 */
public class FieldDeclarationsShouldBeAtStartOfClassRule extends AbstractJavaRule {

    private final PropertyDescriptor<Boolean> ignoreEnumDeclarations = booleanProperty("ignoreEnumDeclarations").defaultValue(true).desc("Ignore Enum Declarations that precede fields.").build();
    private final PropertyDescriptor<Boolean> ignoreAnonymousClassDeclarations = booleanProperty("ignoreAnonymousClassDeclarations").defaultValue(true).desc("Ignore Field Declarations, that are initialized with anonymous class declarations").build();
    private final PropertyDescriptor<Boolean> ignoreInterfaceDeclarations = booleanProperty("ignoreInterfaceDeclarations").defaultValue(false).desc("Ignore Interface Declarations that precede fields.").build();


    public FieldDeclarationsShouldBeAtStartOfClassRule() {
        definePropertyDescriptor(ignoreEnumDeclarations);
        definePropertyDescriptor(ignoreAnonymousClassDeclarations);
        definePropertyDescriptor(ignoreInterfaceDeclarations);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        Node parent = node.getParent().getParent();
        for (int i = 0; i < parent.getNumChildren(); i++) {
            Node child = parent.getChild(i);
            if (child.getNumChildren() > 0) {
                child = skipAnnotations(child);
            }
            if (child.equals(node)) {
                break;
            }
            if (child instanceof ASTFieldDeclaration) {
                continue;
            }
            if (node.hasDescendantOfType(ASTClassOrInterfaceBodyDeclaration.class)
                    && getProperty(ignoreAnonymousClassDeclarations)) {
                continue;
            }
            if (child instanceof ASTMethodDeclaration || child instanceof ASTConstructorDeclaration
                    || child instanceof ASTAnnotationTypeDeclaration) {
                addViolation(data, node);
                break;
            }
            if (child instanceof ASTClassOrInterfaceDeclaration) {
                ASTClassOrInterfaceDeclaration declaration = (ASTClassOrInterfaceDeclaration) child;
                if (declaration.isInterface() && getProperty(ignoreInterfaceDeclarations)) {
                    continue;
                } else {
                    addViolation(data, node);
                    break;
                }
            }
            if (child instanceof ASTEnumDeclaration && !getProperty(ignoreEnumDeclarations)) {
                addViolation(data, node);
                break;
            }
        }
        return data;
    }

    /**
     * Ignore all annotations, until anything, that is not an annotation and
     * return this node
     *
     * @param child
     *            the node from where to start the search
     * @return the first child or the first child after annotations
     */
    private Node skipAnnotations(Node child) {
        Node nextChild = child.getChild(0);
        for (int j = 0; j < child.getNumChildren(); j++) {
            if (!(child.getChild(j) instanceof ASTAnnotation)) {
                nextChild = child.getChild(j);
                break;
            }
        }
        return nextChild;
    }
}
