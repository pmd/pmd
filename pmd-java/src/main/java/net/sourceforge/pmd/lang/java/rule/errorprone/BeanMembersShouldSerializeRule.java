/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.properties.PropertyFactory.stringProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.rule.AbstractLombokAwareRule;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.MethodNameDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class BeanMembersShouldSerializeRule extends AbstractLombokAwareRule {

    private String prefixProperty;

    private static final PropertyDescriptor<String> PREFIX_DESCRIPTOR = stringProperty("prefix").desc("A variable prefix to skip, i.e., m_").defaultValue("").build();

    public BeanMembersShouldSerializeRule() {
        definePropertyDescriptor(PREFIX_DESCRIPTOR);
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
    public Object visit(ASTCompilationUnit node, Object data) {
        prefixProperty = getProperty(PREFIX_DESCRIPTOR);
        super.visit(node, data);
        return data;
    }

    private static String[] imagesOf(List<? extends Node> nodes) {

        String[] imageArray = new String[nodes.size()];

        for (int i = 0; i < nodes.size(); i++) {
            imageArray[i] = nodes.get(i).getImage();
        }
        return imageArray;
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }

        if (hasLombokAnnotation(node)) {
            return super.visit(node, data);
        }

        Map<MethodNameDeclaration, List<NameOccurrence>> methods = node.getScope().getEnclosingScope(ClassScope.class)
                .getMethodDeclarations();
        List<ASTMethodDeclarator> getSetMethList = new ArrayList<>(methods.size());
        for (MethodNameDeclaration d : methods.keySet()) {
            ASTMethodDeclarator mnd = d.getMethodNameDeclaratorNode();
            if (isBeanAccessor(mnd)) {
                getSetMethList.add(mnd);
            }
        }

        String[] methNameArray = imagesOf(getSetMethList);

        Arrays.sort(methNameArray);

        Map<VariableNameDeclaration, List<NameOccurrence>> vars = node.getScope()
                .getDeclarations(VariableNameDeclaration.class);
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : vars.entrySet()) {
            VariableNameDeclaration decl = entry.getKey();
            AccessNode accessNodeParent = decl.getAccessNodeParent();
            if (entry.getValue().isEmpty() || accessNodeParent.isTransient() || accessNodeParent.isStatic()
                    || hasIgnoredAnnotation((Annotatable) accessNodeParent)) {
                continue;
            }
            String varName = StringUtils.capitalize(trimIfPrefix(decl.getImage()));
            boolean hasGetMethod = Arrays.binarySearch(methNameArray, "get" + varName) >= 0
                    || Arrays.binarySearch(methNameArray, "is" + varName) >= 0;
            boolean hasSetMethod = Arrays.binarySearch(methNameArray, "set" + varName) >= 0;
            // Note that a Setter method is not applicable to a final
            // variable...
            if (!hasGetMethod || !accessNodeParent.isFinal() && !hasSetMethod) {
                addViolation(data, decl.getNode(), decl.getImage());
            }
        }
        return super.visit(node, data);
    }

    private String trimIfPrefix(String img) {
        if (prefixProperty != null && img.startsWith(prefixProperty)) {
            return img.substring(prefixProperty.length());
        }
        return img;
    }

    private boolean isBeanAccessor(ASTMethodDeclarator meth) {

        String methodName = meth.getImage();

        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            return true;
        }
        if (methodName.startsWith("is")) {
            ASTResultType ret = ((ASTMethodDeclaration) meth.getParent()).getResultType();
            List<ASTPrimitiveType> primitives = ret.findDescendantsOfType(ASTPrimitiveType.class);
            if (!primitives.isEmpty() && primitives.get(0).isBoolean()) {
                return true;
            }
        }
        return false;
    }
}
