/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.properties.PropertyFactory.stringProperty;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.rule.AbstractIgnoredAnnotationRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class BeanMembersShouldSerializeRule extends AbstractIgnoredAnnotationRule {

    private String prefixProperty;

    private static final PropertyDescriptor<String> PREFIX_DESCRIPTOR = stringProperty("prefix")
            .desc("A variable prefix to skip, i.e., m_").defaultValue("").build();

    public BeanMembersShouldSerializeRule() {
        definePropertyDescriptor(PREFIX_DESCRIPTOR);
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTClassOrInterfaceDeclaration.class);
    }

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        return Arrays.asList(
            "lombok.Data",
            "lombok.Getter",
            "lombok.Value"
        );
    }

    @Override
    public void start(RuleContext ctx) {
        super.start(ctx);
        prefixProperty = getProperty(PREFIX_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }

        if (JavaRuleUtil.hasLombokAnnotation(node)) {
            return data;
        }

        Set<String> accessors =
            node.getDeclarations(ASTMethodDeclaration.class)
                .filter(JavaRuleUtil::isGetterOrSetter)
                .collect(Collectors.mapping(ASTMethodDeclaration::getName, Collectors.toSet()));

        NodeStream<ASTVariableDeclaratorId> fields =
            node.getDeclarations(ASTFieldDeclaration.class)
                .flatMap(ASTFieldDeclaration::getVarIds);

        for (ASTVariableDeclaratorId field : fields) {
            if (field.getLocalUsages().isEmpty()
                || field.getModifiers().hasAny(JModifier.TRANSIENT, JModifier.STATIC)
                || hasIgnoredAnnotation(field)) {
                continue;
            }
            String varName = StringUtils.capitalize(trimIfPrefix(field.getName()));
            boolean hasGetMethod = accessors.contains("get" + varName)
                || accessors.contains("is" + varName);
            boolean hasSetMethod = accessors.contains("set" + varName);
            // Note that a Setter method is not applicable to a final
            // variable...
            if (!hasGetMethod || !field.hasModifiers(JModifier.FINAL) && !hasSetMethod) {
                addViolation(data, field, field.getName());
            }
        }
        return data;
    }

    private String trimIfPrefix(String img) {
        if (prefixProperty != null && img.startsWith(prefixProperty)) {
            return img.substring(prefixProperty.length());
        }
        return img;
    }
}
