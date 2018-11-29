/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.JavaProcessingStage;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;


/**
 * Base class for Java rules. Any rule written in Java to analyse Java source should extend from
 * this base class.
 *
 * TODO add documentation
 *
 */
public abstract class AbstractJavaRule extends AbstractRule implements JavaParserVisitor, ImmutableLanguage {

    public AbstractJavaRule() {
        super.setLanguage(LanguageRegistry.getLanguage(JavaLanguageModule.NAME));
        // Enable Type Resolution on Java Rules by default
        super.setTypeResolution(true);
    }

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        visitAll(nodes, ctx);
    }

    protected void visitAll(List<? extends Node> nodes, RuleContext ctx) {
        for (Object element : nodes) {
            /*
                It is important to note that we are assuming that all nodes here are of type Compilation Unit,
                but our caller method may be called with any type of node, and that's why we need to check the kind
                of instance of each element
            */
            if (element instanceof ASTCompilationUnit) {
                ASTCompilationUnit node = (ASTCompilationUnit) element;
                visit(node, ctx);
            }
        }
    }

    /**
     * Gets the Image of the first parent node of type
     * ASTClassOrInterfaceDeclaration or <code>null</code>
     *
     * @param node
     *            the node which will be searched
     */
    protected final String getDeclaringType(Node node) {
        ASTClassOrInterfaceDeclaration c = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        if (c != null) {
            return c.getImage();
        }
        return null;
    }

    public static boolean isQualifiedName(Node node) {
        return node.getImage().indexOf('.') != -1;
    }

    public static boolean importsPackage(ASTCompilationUnit node, String packageName) {
        List<ASTImportDeclaration> nodes = node.findChildrenOfType(ASTImportDeclaration.class);
        for (ASTImportDeclaration n : nodes) {
            if (n.getPackageName().startsWith(packageName)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isSuppressed(Node node) {
        return JavaRuleViolation.isSupressed(node, this);
    }


    @Override
    public final boolean dependsOn(AstProcessingStage<?> stage) {
        if (!(stage instanceof JavaProcessingStage)) {
            throw new IllegalArgumentException("Processing stage wasn't a Java one: " + stage);
        }
        return ((JavaProcessingStage) stage).ruleDependsOnThisStage(this);
    }

}
