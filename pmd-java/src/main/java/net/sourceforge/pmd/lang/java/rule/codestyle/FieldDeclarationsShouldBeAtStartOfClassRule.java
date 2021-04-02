/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;


/**
 * Detects fields that are declared after methods, constructors, etc. It was a
 * XPath rule, but the Java version is much faster.
 */
public class FieldDeclarationsShouldBeAtStartOfClassRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Boolean> IGNORE_ANONYMOUS_CLASS_DECLARATIONS =
        booleanProperty("ignoreAnonymousClassDeclarations")
            .defaultValue(true)
            .desc("Ignore field declarations, that are initialized with an anonymous class creation expression").build();

    public FieldDeclarationsShouldBeAtStartOfClassRule() {
        super(ASTAnyTypeDeclaration.class);
        definePropertyDescriptor(IGNORE_ANONYMOUS_CLASS_DECLARATIONS);
    }

    @Override
    public Object visitJavaNode(JavaNode node, Object data) {
        assert node instanceof ASTAnyTypeDeclaration;
        return visit((ASTAnyTypeDeclaration) node, data);
    }

    public Object visit(ASTAnyTypeDeclaration node, Object data) {
        boolean inStartOfClass = true;
        for (ASTBodyDeclaration declaration : node.getDeclarations()) {
            if (!isAllowedAtStartOfClass(declaration)) {
                inStartOfClass = false;
            }
            if (!inStartOfClass && declaration instanceof ASTFieldDeclaration) {
                ASTFieldDeclaration field = (ASTFieldDeclaration) declaration;
                if (!isInitializerOk(field)) {
                    addViolation(data, declaration);
                }
            }
        }
        return null;
    }

    private boolean isAllowedAtStartOfClass(ASTBodyDeclaration declaration) {
        return declaration instanceof ASTFieldDeclaration
            || declaration instanceof ASTInitializer
            || declaration instanceof ASTEnumConstant;
    }

    private boolean isInitializerOk(ASTFieldDeclaration fieldDeclaration) {
        if (!getProperty(IGNORE_ANONYMOUS_CLASS_DECLARATIONS)
            || fieldDeclaration.getVarIds().count() != 1) {
            return false;
        }
        ASTExpression initializer = fieldDeclaration.getVarIds().firstOrThrow().getInitializer();
        return JavaRuleUtil.isAnonymousClassCreation(initializer);
    }
}
