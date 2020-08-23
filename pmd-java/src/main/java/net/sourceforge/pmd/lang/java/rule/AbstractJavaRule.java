/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitor;
import net.sourceforge.pmd.lang.java.internal.JavaProcessingStage;
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
    }

    @Override
    public void apply(Node target, RuleContext ctx) {
        target.acceptVisitor(this, ctx);
    }

    public static boolean isQualifiedName(@Nullable String node) {
        return node != null && node.indexOf('.') != -1;
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


    @Override
    public boolean dependsOn(AstProcessingStage<?> stage) {
        if (!(stage instanceof JavaProcessingStage)) {
            throw new IllegalArgumentException("Processing stage wasn't a Java one: " + stage);
        }

        return true;
    }

}
