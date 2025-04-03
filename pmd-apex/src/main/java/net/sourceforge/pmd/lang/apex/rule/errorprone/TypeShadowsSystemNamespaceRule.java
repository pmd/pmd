/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexVisitor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.reporting.RuleContext;

import com.google.common.reflect.ClassPath;

/**
 * Finds custom apex types with the same name as standard built-in system types.
 * Declaring such a custom apex type shadows the system type which can lead to confusion
 * and unexpected behavior.
 *
 * @since 7.13.0
 * @implNote This find the available system types by searching for classes in the dependency
 *     io.github.apex-dev-tools:standard-types in the package {@code com.nawforce.runforce.System}.
 */
public class TypeShadowsSystemNamespaceRule extends AbstractRule {
    private Set<String> systemTypes;

    @Override
    public void initialize(LanguageProcessor languageProcessor) {
        String systemPackageName = com.nawforce.runforce.System.System.class.getPackage().getName();

        try {
            systemTypes = ClassPath.from(ClassLoader.getSystemClassLoader())
                    .getTopLevelClasses(systemPackageName)
                    .stream()
                    .map(ClassPath.ClassInfo::getSimpleName)
                    .map(s -> s.toLowerCase(Locale.ROOT))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTUserClass.class, ASTUserEnum.class, ASTUserInterface.class);
    }

    @Override
    public void apply(Node target, RuleContext ctx) {
        target.acceptVisitor(new ApexVisitor<RuleContext, Void>() {

            private void checkNode(ApexNode<?> node, RuleContext ruleContext) {
                String definingType = node.getDefiningType();
                if (definingType == null) {
                    return;
                }

                if (systemTypes.contains(definingType.toLowerCase(Locale.ROOT))) {
                    ruleContext.addViolation(node);
                }
            }

            @Override
            public Void visit(ASTUserClass node, RuleContext data) {
                checkNode(node, data);
                return null;
            }

            @Override
            public Void visit(ASTUserEnum node, RuleContext data) {
                checkNode(node, data);
                return null;
            }

            @Override
            public Void visit(ASTUserInterface node, RuleContext data) {
                checkNode(node, data);
                return null;
            }

            @Override
            public Void visitNode(Node node, RuleContext param) {
                return null;
            }
        }, ctx);
    }
}
