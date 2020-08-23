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
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExclusiveOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInclusiveOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTMultiplicativeExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTRelationalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTShiftExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTTypeArgument;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpressionNotPlusMinus;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTWildcardBounds;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
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
